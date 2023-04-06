/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.interactor

import com.rainy.mastodroid.core.domain.data.local.AccountLocalDataSource
import com.rainy.mastodroid.core.domain.data.remote.AccountRemoteDataSource
import com.rainy.mastodroid.core.domain.model.user.Account
import com.rainy.mastodroid.core.domain.model.user.AccountRelationships
import com.rainy.mastodroid.core.domain.model.user.FeaturedTag
import kotlinx.coroutines.flow.Flow

interface AccountInteractor {
    fun getAccountFlow(id: String): Flow<Account?>

    suspend fun fetchAccount(id: String): Account

    suspend fun getFeaturedTags(id: String): List<FeaturedTag>

    suspend fun getRelationshipsWithAccount(id: String): AccountRelationships
}

class AccountInteractorImpl(
    private val accountLocalDataSource: AccountLocalDataSource,
    private val accountRemoteDataSource: AccountRemoteDataSource
) : AccountInteractor {

    override fun getAccountFlow(id: String): Flow<Account?> {
        return accountLocalDataSource.getAccountFlowById(id)
    }

    override suspend fun fetchAccount(id: String): Account {
        val account = accountRemoteDataSource.getAccount(id)
        accountLocalDataSource.insertAccount(account)
        return account
    }

    override suspend fun getFeaturedTags(id: String): List<FeaturedTag> {
        return accountRemoteDataSource.getFeaturedTagsForAccount(id)
    }

    override suspend fun getRelationshipsWithAccount(id: String): AccountRelationships {
        return accountRemoteDataSource.getRelationshipsWithAccount(id)
    }
}