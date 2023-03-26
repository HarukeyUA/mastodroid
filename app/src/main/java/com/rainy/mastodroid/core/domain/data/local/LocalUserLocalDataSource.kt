/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.data.local

import com.rainy.mastodroid.core.domain.model.user.LocalUserAuthInfo
import com.rainy.mastodroid.core.domain.model.user.Account

interface LocalUserLocalDataSource {
    suspend fun insertUser(account: Account, authToken: String, instanceHost: String)

    suspend fun getUserAuthInfo(): LocalUserAuthInfo?

    suspend fun isLoggedIn(): Boolean
}
