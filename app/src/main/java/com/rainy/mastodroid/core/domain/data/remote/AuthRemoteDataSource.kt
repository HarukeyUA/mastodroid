/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.data.remote

import com.rainy.mastodroid.core.domain.model.auth.AppAuthCredentials
import com.rainy.mastodroid.core.domain.model.user.Account

interface AuthRemoteDataSource {
    suspend fun authenticateApp(instanceHost: String): AppAuthCredentials

    suspend fun authenticateUser(
        instanceHost: String,
        clientId: String,
        clientSecret: String,
        code: String
    ): String

    suspend fun verifyCredentials(
        instanceHost: String,
        authToken: String
    ): Account
}
