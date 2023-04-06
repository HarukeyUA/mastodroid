/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.interactor

import com.rainy.mastodroid.core.domain.data.remote.StatusRemoteDataSource
import com.rainy.mastodroid.core.domain.data.local.StatusLocalDataSource
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.status.statusThread.StatusNode
import com.rainy.mastodroid.core.domain.model.status.statusThread.StatusThreadedContext
import com.rainy.mastodroid.util.dispatchOrThrow
import com.rainy.mastodroid.util.onErrorValue
import com.rainy.mastodroid.util.wrapResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface StatusInteractor {
    suspend fun fetchStatusDetails(id: String)

    suspend fun getContextTreesForStatus(id: String)
    fun getStatusContextFlow(id: String): Flow<StatusThreadedContext>
    fun getStatusFlow(id: String): Flow<Status?>
    suspend fun setReblogStatus(id: String, action: Boolean)
    suspend fun setFavoriteStatus(id: String, action: Boolean)
}

class StatusInteractorImpl(
    private val statusRemoteDataSource: StatusRemoteDataSource,
    private val statusLocalDataSource: StatusLocalDataSource
) : StatusInteractor {

    override suspend fun fetchStatusDetails(id: String) {
        val status = statusRemoteDataSource.getStatusDetails(id)
        statusLocalDataSource.insertStatus(status)
    }

    override suspend fun getContextTreesForStatus(id: String) {
        val statuses = statusRemoteDataSource.getStatusContext(id)
        statusLocalDataSource.insertStatusContext(statuses, id)
    }

    override fun getStatusContextFlow(id: String): Flow<StatusThreadedContext> {
        return statusLocalDataSource.getContextFlowForStatus(id)
            .map {
                StatusThreadedContext(
                    ancestors = it.ancestors,
                    descendants = buildStatusForest(it.descendants)
                )
            }
    }

    override fun getStatusFlow(id: String): Flow<Status?> {
        return statusLocalDataSource.getStatusFlowById(id)
    }

    override suspend fun setFavoriteStatus(id: String, action: Boolean) {
        if (action) {
            statusLocalDataSource.setFavourite(id)
        } else {
            statusLocalDataSource.unFavourite(id)
        }
        wrapResult {
            if (action) {
                statusRemoteDataSource.favoriteStatus(id)
            } else {
                statusRemoteDataSource.unfavoriteStatus(id)
            }
            return@wrapResult
        }.onErrorValue {
            if (!action) {
                statusLocalDataSource.setFavourite(id)
            } else {
                statusLocalDataSource.unFavourite(id)
            }
        }.dispatchOrThrow()
    }

    override suspend fun setReblogStatus(id: String, action: Boolean) {
        if (action) {
            statusLocalDataSource.setRebloged(id)
        } else {
            statusLocalDataSource.unReblog(id)
        }
        wrapResult {
            if (action) {
                statusRemoteDataSource.reblogStatus(id)
            } else {
                statusRemoteDataSource.unreblogStatus(id)
            }
            return@wrapResult
        }.onErrorValue {
            if (!action) {
                statusLocalDataSource.setRebloged(id)
            } else {
                statusLocalDataSource.unReblog(id)
            }
        }.dispatchOrThrow()
    }

    private fun buildStatusForest(statuses: List<Status>): List<StatusNode> {
        val repliesIndexes = mutableMapOf<String, StatusNode>()
        statuses.forEach {
            repliesIndexes[it.id] =
                StatusNode(content = it)
        }
        repliesIndexes.forEach { (_, statusNode) ->
            val parent = repliesIndexes[statusNode.content.inReplyToId]
            parent?.also {
                statusNode.parent = parent
                parent.children.add(statusNode)
            }
        }

        return repliesIndexes.values.filter { it.parent == null }
    }
}
