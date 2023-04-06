/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.data.local

import com.rainy.mastodroid.core.domain.model.user.Account
import kotlinx.coroutines.flow.Flow

interface AccountLocalDataSource {
    fun getAccountFlowById(id: String): Flow<Account?>

    suspend fun insertAccount(account: Account)
}