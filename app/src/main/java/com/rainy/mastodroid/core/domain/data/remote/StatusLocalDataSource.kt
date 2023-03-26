/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.data.remote

import androidx.paging.PagingSource
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.status.StatusContext
import com.rainy.mastodroidDb.TimelineWithOffset
import kotlinx.coroutines.flow.Flow

interface StatusLocalDataSource {
    fun getHomeTimelinePagingSource(): PagingSource<Int, Status>

    suspend fun replaceTimelineStatuses(statuses: List<Status>)
    suspend fun insertTimelineStatuses(statuses: List<Status>)

    suspend fun getStatusById(id: String): Status?

    suspend fun getLastTimeLineElementId(): String?
    suspend fun setFavourite(id: String)
    suspend fun unFavourite(id: String)
    suspend fun setRebloged(id: String)
    suspend fun unReblog(id: String)
    suspend fun insertStatusContext(statusInContext: StatusContext, forStatusId: String)
    suspend fun insertStatus(status: Status)
    fun getContextFlowForStatus(statusId: String): Flow<StatusContext>
    fun getStatusFlowById(id: String): Flow<Status?>
}