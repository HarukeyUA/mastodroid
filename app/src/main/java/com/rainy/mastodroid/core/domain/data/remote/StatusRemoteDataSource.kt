package com.rainy.mastodroid.core.domain.data.remote

import com.rainy.mastodroid.core.domain.model.status.Status

interface StatusRemoteDataSource {
    suspend fun favoriteStatus(id: String): Status?

    suspend fun unfavoriteStatus(id: String): Status?

    suspend fun reblogStatus(id: String): Status?

    suspend fun unreblogStatus(id: String): Status?
}