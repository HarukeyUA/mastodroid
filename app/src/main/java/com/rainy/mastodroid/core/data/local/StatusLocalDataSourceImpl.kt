/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.local

import androidx.paging.PagingSource
import com.rainy.mastodroid.core.data.model.entity.StatusContextEntity
import com.rainy.mastodroid.core.data.model.entity.StatusInTimeline
import com.rainy.mastodroid.core.data.model.entity.TimelineElementEntity
import com.rainy.mastodroid.core.data.model.entity.status.toStatusAccountEntity
import com.rainy.mastodroid.core.data.model.entity.status.toStatusEntity
import com.rainy.mastodroid.core.domain.data.remote.StatusLocalDataSource
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.status.StatusContext
import com.rainy.mastodroid.core.domain.model.status.toDomain
import com.rainy.mastodroid.database.TimelineDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

class StatusLocalDataSourceImpl(
    private val timelineDao: TimelineDao
) : StatusLocalDataSource {

    override fun getPagingSource(): PagingSource<Int, StatusInTimeline> {
        return timelineDao.getTimelinePaging()
    }

    override suspend fun replaceTimelineStatuses(list: List<Status>) {
        val statusInTimeline = list.map(::getTimeLineElement)
        timelineDao.replaceStatuses(list.map(Status::toStatusEntity), statusInTimeline)
    }

    override suspend fun insertTimelineStatuses(list: List<Status>) {
        val statusInTimeline = list.map(::getTimeLineElement)
        timelineDao.insertAllElements(statusInTimeline)
        timelineDao.insertAllStatuses(list.map(Status::toStatusEntity))
    }

    override suspend fun updateStatus(status: Status) {
        timelineDao.updateStatus(status.toStatusEntity())
    }

    override suspend fun getStatusById(id: String): Status? {
        return timelineDao.getTimelineStatusById(id)?.toDomain()
    }

    override fun getStatusFlowById(id: String): Flow<Status?> {
        return timelineDao.getStatusFlowById(id).map { it?.toDomain() }
    }

    override suspend fun getLastTimeLineElementId(): String? {
        return timelineDao.getLastTimelineElement()?.timelineStatusId
    }

    override suspend fun setFavourite(id: String) {
        timelineDao.setFavourite(id)
    }

    override suspend fun unFavourite(id: String) {
        timelineDao.unFavourite(id)
    }

    override suspend fun setRebloged(id: String) {
        timelineDao.setRebloged(id)
    }

    override suspend fun unReblog(id: String) {
        timelineDao.unReblog(id)
    }

    override suspend fun insertStatusContext(statusInContext: StatusContext, forStatusId: String) {
        val ancestors = statusInContext.ancestors.map(Status::toStatusEntity)
        val descendants = statusInContext.descendants.map(Status::toStatusEntity)
        timelineDao.insertAllStatuses(ancestors)
        timelineDao.insertAllStatuses(descendants)
        timelineDao.replaceStatusContext(
            ancestors.mapIndexed { index, status ->
                StatusContextEntity(
                    statusId = status.originalId,
                    contextForStatusId = forStatusId,
                    contextStatusType = StatusContextEntity.ContextStatusType.ANCESTOR,
                    index = index
                )
            },
            descendants.mapIndexed { index, status ->
                StatusContextEntity(
                    statusId = status.originalId,
                    contextForStatusId = forStatusId,
                    contextStatusType = StatusContextEntity.ContextStatusType.DESCENDANT,
                    index = index
                )
            },
            forStatusId
        )
    }

    override suspend fun insertStatus(status: Status) {
        timelineDao.insertStatus(status.toStatusEntity())
    }

    override fun getContextFlowForStatus(statusId: String): Flow<StatusContext> {
        val ancestors = timelineDao.getAncestorsForStatus(statusId)
        val descendants = timelineDao.getDescednantsForStatus(statusId)

        return combine(ancestors, descendants) { ancestorsList, descendantsList ->
            StatusContext(
                ancestors = ancestorsList.mapNotNull { it.statusEntity?.toDomain() },
                descendants = descendantsList.mapNotNull { it.statusEntity?.toDomain() }
            )
        }
    }

    private fun getTimeLineElement(
        status: Status
    ) = TimelineElementEntity(
        statusId = status.id,
        timelineStatusId = status.reblogId ?: status.id,
        reblogInfo = if (status.reblogId != null && status.reblogAuthorAccount != null) {
            TimelineElementEntity.TimelineElementReblogInfoEntity(
                reblogAuthor = status.reblogAuthorAccount.toStatusAccountEntity(),
                reblogId = status.reblogId
            )
        } else {
            null
        }
    )

}