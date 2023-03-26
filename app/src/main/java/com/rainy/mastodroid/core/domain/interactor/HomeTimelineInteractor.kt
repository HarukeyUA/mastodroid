/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.interactor

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rainy.mastodroid.core.domain.data.remote.StatusLocalDataSource
import com.rainy.mastodroid.core.domain.data.remote.TimelineRemoteDataSource
import com.rainy.mastodroid.core.domain.model.status.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface HomeTimelineInteractor {
    fun getTimeLinePaging(scope: CoroutineScope): Flow<PagingData<Status>>
}

class HomeTimelineInteractorImpl(
    timelineRemoteDataSource: TimelineRemoteDataSource,
    private val statusLocalDataSource: StatusLocalDataSource,
) : HomeTimelineInteractor {

    @OptIn(ExperimentalPagingApi::class)
    private val rawPagingSource = Pager(
        config = PagingConfig(
            pageSize = HOME_TIMELINE_LOAD_SIZE,
            initialLoadSize = HOME_TIMELINE_LOAD_SIZE,
            enablePlaceholders = true, // Workaround for https://issuetracker.google.com/issues/235319241
        ),
        remoteMediator = HomeTimelineMediator(
            timelineRemoteDataSource,
            statusLocalDataSource
        )
    ) {
        statusLocalDataSource.getHomeTimelinePagingSource()
    }.flow

    override fun getTimeLinePaging(scope: CoroutineScope) =
        rawPagingSource.cachedIn(scope)

    companion object {
        private const val HOME_TIMELINE_LOAD_SIZE = 40
    }
}
