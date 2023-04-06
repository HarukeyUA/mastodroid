/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.data.remote

import com.rainy.mastodroid.core.domain.model.user.Account
import com.rainy.mastodroid.core.domain.model.user.AccountRelationships
import com.rainy.mastodroid.core.domain.model.user.FeaturedTag

interface AccountRemoteDataSource {
    suspend fun getAccount(id: String): Account

    suspend fun getFeaturedTagsForAccount(id: String): List<FeaturedTag>

    suspend fun getRelationshipsWithAccount(id: String): AccountRelationships
}