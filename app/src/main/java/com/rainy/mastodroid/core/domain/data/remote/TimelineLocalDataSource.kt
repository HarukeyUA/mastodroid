/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.data.remote

import androidx.paging.PagingSource
import com.rainy.mastodroid.core.data.model.entity.status.StatusInTimeline
import com.rainy.mastodroid.core.domain.model.status.Status

interface TimelineLocalDataSource {
    fun getPagingSource(): PagingSource<Int, StatusInTimeline>

    suspend fun replaceTimelineStatuses(list: List<Status>)
    suspend fun insertTimelineStatuses(list: List<Status>)

    suspend fun updateStatus(status: Status)

    suspend fun getStatusById(id: String): Status?

    suspend fun getLastTimeLineElementId(): String?
    suspend fun setFavourite(id: String)
    suspend fun unFavourite(id: String)
    suspend fun setRebloged(id: String)
    suspend fun unReblog(id: String)
}