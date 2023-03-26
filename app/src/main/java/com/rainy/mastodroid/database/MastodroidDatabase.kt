/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.database

import app.cash.sqldelight.Query
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOne
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.db.SqlDriver
import com.rainy.mastodroid.Database
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.asContextElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.RejectedExecutionException
import java.util.concurrent.atomic.AtomicInteger
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume

class MastodroidDatabase(
    private val db: Database,
    private val driver: SqlDriver,
    private val queryDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val transactionDispatcher: CoroutineDispatcher = queryDispatcher,
) : Database by db {

    private val suspendingTransactionId = ThreadLocal<Int>()

    suspend fun <T> await(inTransaction: Boolean = false, block: suspend Database.() -> T): T {
        return execute(inTransaction, block)
    }

    suspend fun <T : Any> awaitAsList(
        inTransaction: Boolean = false,
        block: suspend Database.() -> Query<T>,
    ): List<T> {
        return execute(inTransaction) { block(db).executeAsList() }
    }

    suspend fun <T : Any> awaitAsOne(
        inTransaction: Boolean = false,
        block: suspend Database.() -> Query<T>,
    ): T {
        return execute(inTransaction) { block(db).executeAsOne() }
    }

    suspend fun <T : Any> awaitAsOneOrNull(
        inTransaction: Boolean = false,
        block: suspend Database.() -> Query<T>,
    ): T? {
        return execute(inTransaction) { block(db).executeAsOneOrNull() }
    }

    fun <T : Any> asFlowOfList(block: Database.() -> Query<T>): Flow<List<T>> {
        return block(db).asFlow().mapToList(queryDispatcher)
    }

    fun <T : Any> asFlowOfOne(block: Database.() -> Query<T>): Flow<T> {
        return block(db).asFlow().mapToOne(queryDispatcher)
    }

    fun <T : Any> asFlowOfOneOrNull(block: Database.() -> Query<T>): Flow<T?> {
        return block(db).asFlow().mapToOneOrNull(queryDispatcher)
    }

    private suspend fun <T> execute(inTransaction: Boolean, block: suspend Database.() -> T): T {
        return when {
            inTransaction -> withTransaction { block(db) }
            driver.currentTransaction() != null -> block(db)
            else -> {
                // Use the transaction dispatcher if we are on a transaction coroutine, otherwise
                // use the database dispatchers.
                val context = coroutineContext[TransactionElement]?.transactionDispatcher ?: queryDispatcher
                return withContext(context) { block(db) }
            }
        }
    }

    /**
     * Calls the specified suspending [block] in a database transaction. The transaction will be
     * marked as successful unless an exception is thrown in the suspending [block] or the coroutine
     * is cancelled.
     *
     * SQLDelight will only perform at most one transaction at a time, additional transactions are queued
     * and executed on a first come, first serve order.
     *
     * Performing blocking database operations is not permitted in a coroutine scope other than the
     * one received by the suspending block. It is recommended that all function invoked within
     * the [block] be suspending functions.
     *
     * The dispatcher used to execute the given [block] will utilize threads from SQLDelight's query executor.
     */
    private suspend fun <T> withTransaction(block: suspend () -> T): T {
        // Use inherited transaction context if available, this allows nested suspending transactions.
        val transactionContext =
            coroutineContext[TransactionElement]?.transactionDispatcher ?: createTransactionContext()

        return withContext(transactionContext) {
            val transactionElement = coroutineContext[TransactionElement]!!
            transactionElement.acquire()
            try {
                transactionWithResult {
                    runBlocking(transactionContext) {
                        block()
                    }
                }
            } finally {
                transactionElement.release()
            }
        }
    }

    /**
     * Creates a [CoroutineContext] for performing database operations within a coroutine transaction.
     *
     * The context is a combination of a dispatcher, a [TransactionElement] and a thread local element.
     *
     * * The dispatcher will dispatch coroutines to a single thread that is taken over from the SQLDelight
     * query executor. If the coroutine context is switched, suspending functions will be able to
     * dispatch to the transaction thread.
     *
     * * The [TransactionElement] serves as an indicator for inherited context, meaning, if there is a
     * switch of context, suspending DAO methods will be able to use the indicator to dispatch the
     * database operation to the transaction thread.
     *
     * * The thread local element serves as a second indicator and marks threads that are used to
     * execute coroutines within the coroutine transaction, more specifically it allows us to identify
     * if a blocking method is invoked within the transaction coroutine. Never assign meaning to
     * this value, for now all we care is if its present or not.
     */
    private suspend fun createTransactionContext(): CoroutineContext {
        val controlJob = Job()
        // make sure to tie the control job to this context to avoid blocking the transaction if
        // context get cancelled before we can even start using this job. Otherwise, the acquired
        // transaction thread will forever wait for the controlJob to be cancelled.
        // see b/148181325
        coroutineContext[Job]?.invokeOnCompletion {
            controlJob.cancel()
        }

        val dispatcher = transactionDispatcher.acquireTransactionThread(controlJob)
        val transactionElement = TransactionElement(controlJob, dispatcher)
        val threadLocalElement =
            suspendingTransactionId.asContextElement(System.identityHashCode(controlJob))
        return dispatcher + transactionElement + threadLocalElement
    }

    /**
     * Acquires a thread from the executor and returns a [ContinuationInterceptor] to dispatch
     * coroutines to the acquired thread. The [controlJob] is used to control the release of the
     * thread by cancelling the job.
     */
    private suspend fun CoroutineDispatcher.acquireTransactionThread(
        controlJob: Job,
    ): ContinuationInterceptor {
        return suspendCancellableCoroutine { continuation ->
            continuation.invokeOnCancellation {
                // We got cancelled while waiting to acquire a thread, we can't stop our attempt to
                // acquire a thread, but we can cancel the controlling job so once it gets acquired it
                // is quickly released.
                controlJob.cancel()
            }
            try {
                dispatch(EmptyCoroutineContext) {
                    runBlocking {
                        // Thread acquired, resume coroutine
                        continuation.resume(coroutineContext[ContinuationInterceptor]!!)
                        controlJob.join()
                    }
                }
            } catch (ex: RejectedExecutionException) {
                // Couldn't acquire a thread, cancel coroutine
                continuation.cancel(
                    IllegalStateException(
                        "Unable to acquire a thread to perform the database transaction",
                        ex,
                    ),
                )
            }
        }
    }
}

/**
 * A [CoroutineContext.Element] that indicates there is an on-going database transaction.
 */
private class TransactionElement(
    private val transactionThreadControlJob: Job,
    val transactionDispatcher: ContinuationInterceptor,
) : CoroutineContext.Element {

    companion object Key : CoroutineContext.Key<TransactionElement>

    override val key: CoroutineContext.Key<TransactionElement>
        get() = TransactionElement

    /**
     * Number of transactions (including nested ones) started with this element.
     * Call [acquire] to increase the count and [release] to decrease it. If the count reaches zero
     * when [release] is invoked then the transaction job is cancelled and the transaction thread
     * is released.
     */
    private val referenceCount = AtomicInteger(0)

    fun acquire() {
        referenceCount.incrementAndGet()
    }

    fun release() {
        val count = referenceCount.decrementAndGet()
        if (count < 0) {
            throw IllegalStateException("Transaction was never started or was already released")
        } else if (count == 0) {
            // Cancel the job that controls the transaction thread, causing it to be released.
            transactionThreadControlJob.cancel()
        }
    }
}
