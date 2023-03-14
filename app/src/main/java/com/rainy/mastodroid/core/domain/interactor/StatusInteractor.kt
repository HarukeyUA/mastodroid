/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.interactor

import com.rainy.mastodroid.core.domain.data.remote.StatusRemoteDataSource
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.status.statusThread.StatusNode
import com.rainy.mastodroid.core.domain.model.status.statusThread.StatusThreadedContext

interface StatusInteractor {
    suspend fun getStatusDetails(id: String): Status

    suspend fun getContextTreesForStatus(id: String): StatusThreadedContext
    fun buildStatusForest(statuses: List<Status>): List<StatusNode>
}

class StatusInteractorImpl(
    private val statusRemoteDataSource: StatusRemoteDataSource
) : StatusInteractor {

    override suspend fun getStatusDetails(id: String): Status {
        return statusRemoteDataSource.getStatusDetails(id)
    }

    override suspend fun getContextTreesForStatus(id: String): StatusThreadedContext {
        val statuses = statusRemoteDataSource.getStatusContext(id)
        val descendantsForest = buildStatusForest(statuses.descendants)
        return StatusThreadedContext(
            ancestors = statuses.ancestors,
            descendants = descendantsForest
        )
    }

    override fun buildStatusForest(statuses: List<Status>): List<StatusNode> {
        val repliesIndexes = mutableMapOf<String, StatusNode>()
        statuses.forEach {
            repliesIndexes[it.originalId] =
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
