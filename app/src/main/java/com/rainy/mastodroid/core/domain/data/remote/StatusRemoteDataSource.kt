package com.rainy.mastodroid.core.domain.data.remote

import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.status.StatusContext

interface StatusRemoteDataSource {
    suspend fun favoriteStatus(id: String): Status?

    suspend fun unfavoriteStatus(id: String): Status?

    suspend fun reblogStatus(id: String): Status?

    suspend fun unreblogStatus(id: String): Status?
    suspend fun getStatusDetails(id: String): Status
    suspend fun getStatusContext(id: String): StatusContext
}