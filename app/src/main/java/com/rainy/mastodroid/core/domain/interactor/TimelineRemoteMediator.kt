/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.interactor

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.rainy.mastodroid.core.domain.model.status.Status
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class TimelineRemoteMediator(
    private val shouldReorderStatuses: Boolean = true,
    private val lastCachedElementId: suspend () -> String?,
    private val getRemoteStatuses: suspend (olderThanId: String?, limit: Int) -> List<Status>,
    private val replaceCachedStatuses: suspend (statuses: List<Status>) -> Unit,
    private val insertStatusCache: suspend (statuses: List<Status>) -> Unit
) : RemoteMediator<Int, Status>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Status>
    ): MediatorResult {
        try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> lastCachedElementId()
                    ?: return MediatorResult.Success(endOfPaginationReached = true)
            }

            val statuses = getRemoteStatuses(loadKey, state.config.pageSize)

            if (loadType == LoadType.REFRESH) {
                replaceCachedStatuses(
                    if (shouldReorderStatuses) {
                        reorderStatuses(statuses)
                    } else {
                        statuses
                    }
                )
            } else {
                insertStatusCache(
                    if (shouldReorderStatuses) {
                        reorderStatuses(statuses)
                    } else {
                        statuses
                    }
                )
            }

            return MediatorResult.Success(endOfPaginationReached = statuses.isEmpty())
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }

    private fun reorderStatuses(statuses: List<Status>): List<Status> {
        val repliesIndexes = mutableMapOf<String, StatusNode>()
        statuses.forEach { repliesIndexes[it.id] = StatusNode(content = it) }
        repliesIndexes.forEach { (_, statusNode) ->
            val parent = repliesIndexes[statusNode.content.inReplyToId]
            parent?.also {
                statusNode.parent = parent
                parent.children.add(statusNode)
            }
        }

        return extractContent(repliesIndexes.values.filter { it.parent == null })
    }

    private fun extractContent(statusNode: List<StatusNode>): List<Status> {
        return statusNode.flatMap {
            if (it.children.isEmpty()) {
                listOf(it.content)
            } else {
                buildList {
                    add(it.content)
                    addAll(extractContent(it.children))
                }
            }
        }
    }

    private data class StatusNode(
        var parent: StatusNode? = null,
        val children: MutableList<StatusNode> = mutableListOf(),
        val content: Status
    )
}
