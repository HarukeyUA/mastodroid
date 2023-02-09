package com.rainy.mastodroid.core.domain.data.remote

import androidx.paging.PagingSource
import com.rainy.mastodroid.core.data.model.entity.status.StatusEntity
import com.rainy.mastodroid.core.domain.model.status.Status

interface TimelineLocalDataSource {
    fun getPagingSource(): PagingSource<Int, StatusEntity>

    suspend fun replaceStatuses(list: List<Status>)
    suspend fun insertStatuses(list: List<Status>)

    suspend fun updateStatus(status: Status)

    suspend fun getStatusById(id: String): Status?

    suspend fun getLastStatus(): Status?
}