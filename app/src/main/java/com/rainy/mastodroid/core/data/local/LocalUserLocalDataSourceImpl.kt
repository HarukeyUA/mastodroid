/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.local

import com.rainy.mastodroid.core.data.model.entity.toLocalUserEntity
import com.rainy.mastodroid.core.domain.data.local.LocalUserLocalDataSource
import com.rainy.mastodroid.core.domain.model.user.LocalUserAuthInfo
import com.rainy.mastodroid.core.domain.model.user.User
import com.rainy.mastodroid.core.domain.model.user.toDomain
import com.rainy.mastodroid.database.LocalUserDao

class LocalUserLocalDataSourceImpl(
    private val localUserDao: LocalUserDao
) : LocalUserLocalDataSource {

    override suspend fun insertUser(user: User, authToken: String, instanceHost: String) {
        localUserDao.insertUser(
            user.toLocalUserEntity(
                authToken = authToken,
                instanceHost = instanceHost
            )
        )
    }

    override suspend fun getUser(): User? {
        return localUserDao.getUser()?.toDomain()
    }

    override suspend fun getUserAuthInfo(): LocalUserAuthInfo? {
        val user = localUserDao.getUser()

        return user?.let {
            LocalUserAuthInfo(
                authToken = user.authToken,
                instanceHost = user.instanceHost
            )
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return getUser() != null
    }
}
