/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.interactor

import com.rainy.mastodroid.core.domain.data.local.LocalUserLocalDataSource
import com.rainy.mastodroid.core.domain.data.remote.AuthRemoteDataSource
import com.rainy.mastodroid.core.domain.model.auth.AppAuthCredentials

interface AuthInteractor {
    suspend fun authenticateApp(instanceHost: String): AppAuthCredentials

    suspend fun authenticateUser(
        instanceHost: String,
        clientId: String,
        clientSecret: String,
        oauthCode: String
    )

    suspend fun isLoggedIn(): Boolean
}

class AuthInteractorImpl(
    private val remoteAuthRemoteDataSource: AuthRemoteDataSource,
    private val localUserLocalDataSource: LocalUserLocalDataSource
) : AuthInteractor {

    override suspend fun authenticateApp(instanceHost: String): AppAuthCredentials {
        return remoteAuthRemoteDataSource.authenticateApp(instanceHost)
    }

    override suspend fun authenticateUser(
        instanceHost: String,
        clientId: String,
        clientSecret: String,
        oauthCode: String
    ) {
        val authToken =
            remoteAuthRemoteDataSource.authenticateUser(
                instanceHost,
                clientId,
                clientSecret,
                oauthCode
            )
        val userInfo = remoteAuthRemoteDataSource.verifyCredentials(instanceHost, authToken)
        localUserLocalDataSource.insertUser(
            account = userInfo,
            authToken = authToken,
            instanceHost = instanceHost
        )
    }

    override suspend fun isLoggedIn(): Boolean {
        return localUserLocalDataSource.isLoggedIn()
    }
}
