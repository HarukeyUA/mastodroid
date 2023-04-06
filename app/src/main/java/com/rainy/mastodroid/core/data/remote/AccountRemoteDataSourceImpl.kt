/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.remote

import com.rainy.mastodroid.core.data.model.response.FeaturedTagResponse
import com.rainy.mastodroid.core.domain.data.remote.AccountRemoteDataSource
import com.rainy.mastodroid.core.domain.model.user.Account
import com.rainy.mastodroid.core.domain.model.user.AccountRelationships
import com.rainy.mastodroid.core.domain.model.user.FeaturedTag
import com.rainy.mastodroid.core.domain.model.user.toDomain
import com.rainy.mastodroid.network.MastodonApi

class AccountRemoteDataSourceImpl(
    private val mastodonApi: MastodonApi
) : AccountRemoteDataSource {

    override suspend fun getAccount(id: String): Account {
        return mastodonApi.getAccount(id).toDomain()
    }

    override suspend fun getFeaturedTagsForAccount(id: String): List<FeaturedTag> {
        return mastodonApi.getFeaturedTagsForAccount(id).map(FeaturedTagResponse::toDomain)
    }

    override suspend fun getRelationshipsWithAccount(id: String): AccountRelationships {
        return mastodonApi.getRelationships(listOf(id)).first().toDomain()
    }
}