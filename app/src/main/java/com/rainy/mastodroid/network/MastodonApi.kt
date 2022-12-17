package com.rainy.mastodroid.network

import com.rainy.mastodroid.core.data.model.response.status.StatusResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface MastodonApi {

    @GET("api/v1/timelines/home")
    suspend fun getHomeTimeline(
        @Query("max_id") maxId: String? = null,
        @Query("since_id") sinceId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int = 20
    ): List<StatusResponse>
}
