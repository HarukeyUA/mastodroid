/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.remote

import androidx.annotation.IntRange
import com.rainy.mastodroid.core.data.model.response.status.StatusResponse
import com.rainy.mastodroid.core.domain.data.remote.TimelineRemoteDataSource
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.status.toDomain
import com.rainy.mastodroid.network.MastodonApi

class TimelineRemoteDataSourceImpl(private val mastodonApi: MastodonApi) :
    TimelineRemoteDataSource {

    override suspend fun getHomeStatuses(
        olderThanId: String?,
        newerThanId: String?,
        @IntRange(1, 40) limit: Int
    ): List<Status> {
        return mastodonApi.getHomeTimeline(
            maxId = olderThanId,
            sinceId = newerThanId,
            limit = limit
        ).mapNotNull(StatusResponse::toDomain)
    }

    override suspend fun getAccountStatuses(
        accountId: String,
        olderThanId: String?,
        newerThanId: String?,
        @IntRange(1, 40) limit: Int,
        excludeReplies: Boolean,
        onlyMedia: Boolean
    ): List<Status> {
        return mastodonApi.getAccountStatuses(
            accountId = accountId,
            maxId = olderThanId,
            sinceId = newerThanId,
            limit = limit,
            excludeReplies = excludeReplies,
            onlyMedia = onlyMedia
        ).mapNotNull(StatusResponse::toDomain)
    }
}
