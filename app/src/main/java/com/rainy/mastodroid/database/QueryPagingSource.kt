/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.database

import androidx.paging.PagingSource
import androidx.paging.PagingState
import app.cash.sqldelight.Query
import app.cash.sqldelight.db.SqlCursor
import kotlin.properties.Delegates

class OffsetQueryPagingSource<RowType : Any, DomainType : Any>(
    private val queryProvider: (limit: Int, offset: Int) -> Query<RowType>,
    private val countQuery: Query<Int>,
    private val mapper: (RowType) -> DomainType,
    private val db: MastodroidDatabase
) : PagingSource<Int, DomainType>(), Query.Listener {

    private var currentQuery: Query<RowType>? by Delegates.observable(null) { _, old, new ->
        old?.removeListener(this)
        new?.addListener(this)
    }

    init {
        registerInvalidatedCallback {
            currentQuery?.removeListener(this)
            currentQuery = null
        }
    }

    override fun queryResultsChanged() = invalidate()

    override val jumpingSupported get() = true

    override suspend fun load(
        params: LoadParams<Int>,
    ): LoadResult<Int, DomainType> {
        val key = params.key ?: 0
        val limit = when (params) {
            is LoadParams.Prepend<*> -> minOf(key, params.loadSize)
            else -> params.loadSize
        }
        val loadResult = db.await(true) {
            val count = countQuery.executeAsOne()
            val offset = when (params) {
                is LoadParams.Prepend<*> -> maxOf(0, key - params.loadSize)
                is LoadParams.Append<*> -> key
                is LoadParams.Refresh<*> -> if (key >= count) maxOf(
                    0,
                    count - params.loadSize
                ) else key

                else -> error("Unknown PagingSourceLoadParams ${params::class}")
            }
            val data = queryProvider(limit, offset)
                .also { currentQuery = it }
                .executeAsList()
                .map(mapper)
            val nextPosToLoad = offset + data.size
            LoadResult.Page(
                data = data,
                prevKey = offset.takeIf { it > 0 && data.isNotEmpty() },
                nextKey = nextPosToLoad.takeIf { data.isNotEmpty() && data.size >= limit && it < count },
                itemsBefore = offset,
                itemsAfter = maxOf(0, count - nextPosToLoad),
            )
        }
        return (if (invalid) LoadResult.Invalid() else loadResult)
    }

    override fun getRefreshKey(state: PagingState<Int, DomainType>) =
        state.anchorPosition?.let { maxOf(0, it - (state.config.initialLoadSize / 2)) }
}

fun Query<Long>.toInt(): Query<Int> =
    object : Query<Int>({ cursor -> mapper(cursor).toInt() }) {
        override fun <R> execute(mapper: (SqlCursor) -> R) = this@toInt.execute(mapper)
        override fun addListener(listener: Listener) = this@toInt.addListener(listener)
        override fun removeListener(listener: Listener) = this@toInt.removeListener(listener)
    }
