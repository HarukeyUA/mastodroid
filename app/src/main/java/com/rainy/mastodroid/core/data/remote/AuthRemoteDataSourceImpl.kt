/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.remote

import com.rainy.mastodroid.core.domain.data.remote.AuthRemoteDataSource
import com.rainy.mastodroid.core.domain.model.auth.AppAuthCredentials
import com.rainy.mastodroid.core.domain.model.auth.toDomain
import com.rainy.mastodroid.core.domain.model.user.User
import com.rainy.mastodroid.core.domain.model.user.toDomain
import com.rainy.mastodroid.network.MastodonPublicApi

class AuthRemoteDataSourceImpl(
    private val publicApi: MastodonPublicApi
) : AuthRemoteDataSource {
    override suspend fun authenticateApp(instanceHost: String): AppAuthCredentials {
        return publicApi.authenticateApp(
            host = instanceHost,
            clientName = APP_NAME,
            redirectUris = OAUTH_REDIRECT_URI,
            scopes = OAUTH_SCOPES,
            website = ""
        ).toDomain()
    }

    override suspend fun authenticateUser(
        instanceHost: String,
        clientId: String,
        clientSecret: String,
        code: String
    ): String {
        return publicApi.authenticateUser(
            host = instanceHost,
            clientId = clientId,
            clientSecret = clientSecret,
            redirectUri = OAUTH_REDIRECT_URI,
            code = code,
            grantType = AUTH_GRANT_TYPE
        ).accessToken
    }

    override suspend fun verifyCredentials(
        instanceHost: String,
        authToken: String
    ): User {
        return publicApi.verifyCredentials(
            host = instanceHost,
            authToken = "Bearer ${authToken.trim()}"
        ).toDomain()
    }

    companion object {
        const val AUTH_GRANT_TYPE = "authorization_code"
        const val OAUTH_SCOPES = "read write follow push"
        const val OAUTH_REDIRECT_URI = "mastodroid://oauth_redirect"
        const val APP_NAME = "Mastodroid"
    }
}
