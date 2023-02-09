package com.rainy.mastodroid.core.data.local

import androidx.paging.PagingSource
import com.rainy.mastodroid.core.data.model.entity.status.StatusEntity
import com.rainy.mastodroid.core.data.model.entity.status.toStatusEntity
import com.rainy.mastodroid.core.domain.data.remote.TimelineLocalDataSource
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.status.toDomain
import com.rainy.mastodroid.database.TimelineDao

class TimelineLocalDataSourceImpl(
    private val timelineDao: TimelineDao
): TimelineLocalDataSource {
    override fun getPagingSource(): PagingSource<Int, StatusEntity> {
        return timelineDao.getTimelinePaging()
    }

    override suspend fun replaceStatuses(list: List<Status>) {
        timelineDao.replaceStatuses(list.map(Status::toStatusEntity))
    }

    override suspend fun insertStatuses(list: List<Status>) {
        timelineDao.insertAll(list.map(Status::toStatusEntity))
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

}