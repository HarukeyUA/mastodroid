/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.network

import com.rainy.mastodroid.core.data.model.response.FeaturedTagResponse
import com.rainy.mastodroid.core.data.model.response.status.StatusContextResponse
import com.rainy.mastodroid.core.data.model.response.status.StatusResponse
import com.rainy.mastodroid.core.data.model.response.user.AccountRelationshipResponse
import com.rainy.mastodroid.core.data.model.response.user.AccountResponse
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface MastodonApi {

    @GET("api/v1/timelines/home")
    suspend fun getHomeTimeline(
        @Query("max_id") maxId: String? = null,
        @Query("since_id") sinceId: String? = null,
        @Query("min_id") minId: String? = null,
        @Query("limit") limit: Int = 20
    ): List<StatusResponse>

    @POST("api/v1/statuses/{id}/favourite")
    suspend fun favoriteStatus(
        @Path("id") id: String
    ): StatusResponse

    @POST("api/v1/statuses/{id}/unfavourite")
    suspend fun unfavoriteStatus(
        @Path("id") id: String
    ): StatusResponse

    @POST("api/v1/statuses/{id}/reblog")
    suspend fun reblogStatus(
        @Path("id") id: String
    ): StatusResponse

    @POST("api/v1/statuses/{id}/unreblog")
    suspend fun unreblogStatus(
        @Path("id") id: String
    ): StatusResponse

    @GET("api/v1/statuses/{id}/context")
    suspend fun getStatusContext(
        @Path("id") id: String
    ): StatusContextResponse

    @GET("api/v1/statuses/{id}")
    suspend fun getStatusDetails(
        @Path("id") id: String
    ): StatusResponse

    @GET("api/v1/accounts/{id}")
    suspend fun getAccount(
        @Path("id") id: String
    ): AccountResponse

    @GET("api/v1/accounts/{id}/featured_tags")
    suspend fun getFeaturedTagsForAccount(
        @Path("id") id: String
    ): List<FeaturedTagResponse>

    @POST("api/v1/accounts/{id}/follow")
    suspend fun followAccount(
        @Path("id") id: String
    ): AccountRelationshipResponse

    @POST("api/v1/accounts/{id}/unfollow")
    suspend fun unfollowAccount(
        @Path("id") id: String
    ): AccountRelationshipResponse

    @GET("api/v1/accounts/relationships")
    suspend fun getRelationships(
        @Query("id[]") list: List<String>
    ): List<AccountRelationshipResponse>


}
