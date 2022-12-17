package com.rainy.mastodroid.network

import com.rainy.mastodroid.core.data.model.response.AuthAppResponse
import com.rainy.mastodroid.core.data.model.response.AuthTokenResponse
import com.rainy.mastodroid.core.data.model.response.user.AccountResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface MastodonPublicApi {

    @FormUrlEncoded
    @POST("api/v1/apps")
    suspend fun authenticateApp(
        @Header(HOST_HEADER) host: String,
        @Field("client_name") clientName: String,
        @Field("redirect_uris") redirectUris: String,
        @Field("scopes") scopes: String,
        @Field("website") website: String
    ): AuthAppResponse

    @FormUrlEncoded
    @POST("oauth/token")
    suspend fun authenticateUser(
        @Header(HOST_HEADER) host: String,
        @Field("client_id") clientId: String,
        @Field("client_secret") clientSecret: String,
        @Field("redirect_uri") redirectUri: String,
        @Field("code") code: String,
        @Field("grant_type") grantType: String
    ): AuthTokenResponse

    @GET("api/v1/accounts/verify_credentials")
    suspend fun verifyCredentials(
        @Header(HOST_HEADER) host: String,
        @Header("Authorization") authToken: String,
    ): AccountResponse

    companion object {
        const val HOST_HEADER = "INSTANCE_HOST"
    }
}
