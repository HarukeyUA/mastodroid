/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.local

import com.rainy.mastodroid.core.domain.data.local.LocalUserLocalDataSource
import com.rainy.mastodroid.core.domain.model.user.Account
import com.rainy.mastodroid.core.domain.model.user.LocalUserAuthInfo
import com.rainy.mastodroid.database.MastodroidDatabase

class LocalUserLocalDataSourceImpl(
    private val db: MastodroidDatabase
) : LocalUserLocalDataSource {

    override suspend fun insertUser(account: Account, authToken: String, instanceHost: String) {
        db.await {
            loggedAccountQueries.insertAccount(
                remoteId = account.id,
                authToken = authToken,
                instaceHost = instanceHost
            )
        }
    }

    override suspend fun getUserAuthInfo(): LocalUserAuthInfo? {
        val user = db.awaitAsOneOrNull {
            loggedAccountQueries.getUser()
        }

        return user?.let {
            LocalUserAuthInfo(
                authToken = user.authToken,
                instanceHost = user.instanceHost
            )
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return getUserAuthInfo() != null
    }
}
