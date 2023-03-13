package com.rainy.mastodroid.core.data.remote

import com.rainy.mastodroid.core.domain.data.remote.StatusRemoteDataSource
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.status.StatusContext
import com.rainy.mastodroid.core.domain.model.status.toDomain
import com.rainy.mastodroid.network.MastodonApi

class StatusRemoteDataSourceImpl(private val mastodonApi: MastodonApi) : StatusRemoteDataSource {

    override suspend fun favoriteStatus(id: String): Status? {
        return mastodonApi.favoriteStatus(id).toDomain()
    }

    override suspend fun unfavoriteStatus(id: String): Status? {
        return mastodonApi.unfavoriteStatus(id).toDomain()
    }

    override suspend fun reblogStatus(id: String): Status? {
        return mastodonApi.reblogStatus(id).toDomain()
    }

    override suspend fun unreblogStatus(id: String): Status? {
        return mastodonApi.unreblogStatus(id).toDomain()
    }

    override suspend fun getStatusDetails(id: String): Status {
        return mastodonApi.getStatusDetails(id).toDomain()
            ?: throw IllegalStateException("Unable to get status details, instance didn't return id or author?")
    }

    override suspend fun getStatusContext(id: String): StatusContext {
        return mastodonApi.getStatusContext(id).toDomain()
    }
}