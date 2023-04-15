/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.interactor

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.rainy.mastodroid.core.data.model.entity.status.AccountStatusTimelineType
import com.rainy.mastodroid.core.domain.data.local.StatusLocalDataSource
import com.rainy.mastodroid.core.domain.data.remote.TimelineRemoteDataSource
import com.rainy.mastodroid.core.domain.model.status.Status
import kotlinx.coroutines.flow.Flow

interface TimelineInteractor {
    fun getAccountTimelinePaging(
        accountId: String,
        accountStatusTimelineType: AccountStatusTimelineType
    ): Flow<PagingData<Status>>

    val homeTimelinePaging: Flow<PagingData<Status>>
}

class TimelineInteractorImpl(
    private val timelineRemoteDataSource: TimelineRemoteDataSource,
    private val statusLocalDataSource: StatusLocalDataSource,
) : TimelineInteractor {

    @OptIn(ExperimentalPagingApi::class)
    override val homeTimelinePaging = Pager(
        config = PagingConfig(
            pageSize = HOME_TIMELINE_LOAD_SIZE,
            initialLoadSize = HOME_TIMELINE_LOAD_SIZE,
            enablePlaceholders = true, // Workaround for https://issuetracker.google.com/issues/235319241
        ),
        remoteMediator = TimelineRemoteMediator(
            shouldReorderStatuses = true,
            lastCachedElementId = statusLocalDataSource::getLastTimeLineElementId,
            getRemoteStatuses = { olderThanId, limit ->
                timelineRemoteDataSource.getHomeStatuses(olderThanId = olderThanId, limit = limit)
            },
            replaceCachedStatuses = statusLocalDataSource::replaceTimelineStatuses,
            insertStatusCache = statusLocalDataSource::insertTimelineStatuses
        )
    ) {
        statusLocalDataSource.getHomeTimelinePagingSource()
    }.flow

    @OptIn(ExperimentalPagingApi::class)
    override fun getAccountTimelinePaging(
        accountId: String,
        accountStatusTimelineType: AccountStatusTimelineType
    ): Flow<PagingData<Status>> {
        return Pager(
            config = PagingConfig(
                pageSize = HOME_TIMELINE_LOAD_SIZE,
                initialLoadSize = HOME_TIMELINE_LOAD_SIZE,
                enablePlaceholders = true, // Workaround for https://issuetracker.google.com/issues/235319241
            ),
            remoteMediator = TimelineRemoteMediator(
                shouldReorderStatuses = false,
                lastCachedElementId = {
                    statusLocalDataSource.getLastAccountTimeLineElementId(
                        accountId = accountId,
                        accountStatusTimelineType = accountStatusTimelineType
                    )
                },
                getRemoteStatuses = { olderThanId, limit ->
                    timelineRemoteDataSource.getAccountStatuses(
                        accountId = accountId,
                        olderThanId = olderThanId,
                        limit = limit,
                        excludeReplies = accountStatusTimelineType != AccountStatusTimelineType.POSTS_REPLIES,
                        newerThanId = null,
                        onlyMedia = accountStatusTimelineType == AccountStatusTimelineType.MEDIA
                    )
                },
                replaceCachedStatuses = {
                    statusLocalDataSource.replaceAccountTimelineStatuses(
                        it,
                        accountId,
                        accountStatusTimelineType
                    )
                },
                insertStatusCache = {
                    statusLocalDataSource.insertAccountTimelineStatuses(
                        it,
                        accountId,
                        accountStatusTimelineType
                    )
                }
            )
        ) {
            statusLocalDataSource.getAccountTimelinePagingSource(
                accountId = accountId,
                timelineType = accountStatusTimelineType
            )
        }.flow
    }

    companion object {
        private const val HOME_TIMELINE_LOAD_SIZE = 20
    }
}
