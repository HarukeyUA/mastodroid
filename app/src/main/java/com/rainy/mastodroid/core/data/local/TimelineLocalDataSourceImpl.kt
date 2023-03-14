/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.local

import androidx.paging.PagingSource
import com.rainy.mastodroid.core.data.model.entity.status.StatusInTimeline
import com.rainy.mastodroid.core.data.model.entity.status.TimelineElementEntity
import com.rainy.mastodroid.core.data.model.entity.status.toStatusEntity
import com.rainy.mastodroid.core.domain.data.remote.TimelineLocalDataSource
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.status.toDomain
import com.rainy.mastodroid.database.TimelineDao

class TimelineLocalDataSourceImpl(
    private val timelineDao: TimelineDao
): TimelineLocalDataSource {
    override fun getPagingSource(): PagingSource<Int, StatusInTimeline> {
        return timelineDao.getTimelinePaging()
    }

    override suspend fun replaceStatuses(list: List<Status>) {
        timelineDao.replaceStatuses(list.map(Status::toStatusEntity))
    }

    override suspend fun insertStatuses(list: List<Status>) {
        val statusInTimeline = list.map { TimelineElementEntity(statusId = it.originalId) }
        timelineDao.insertAllElements(statusInTimeline)
        timelineDao.insertAllStatuses(list.map(Status::toStatusEntity))
    }

    override suspend fun updateStatus(status: Status) {
        timelineDao.updateStatus(status.toStatusEntity())
    }

    override suspend fun getStatusById(id: String): Status? {
        return timelineDao.getTimelineStatusById(id)?.toDomain()
    }

    override suspend fun getLastStatus(): Status? {
        return timelineDao.getLastStatus()?.toDomain()
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

}