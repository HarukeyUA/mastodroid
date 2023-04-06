/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.local

import com.rainy.mastodroid.core.data.model.entity.status.StatusAccountUserFieldEntity
import com.rainy.mastodroid.core.data.model.entity.status.StatusCustomEmojiEntity
import com.rainy.mastodroid.core.domain.data.local.AccountLocalDataSource
import com.rainy.mastodroid.core.domain.model.user.Account
import com.rainy.mastodroid.core.domain.model.user.toDomain
import com.rainy.mastodroid.database.MastodroidDatabase
import com.rainy.mastodroid.database.extensions.upsertAccount
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AccountLocalDataSourceImpl(
    private val db: MastodroidDatabase
) : AccountLocalDataSource {
    override fun getAccountFlowById(id: String): Flow<Account?> {
        return db.asFlowOfOneOrNull {
            accountQueries.accountById(
                id
            )
        }.map {
            it?.let {
                with(it) {
                    Account(
                        accountUri = accountUri,
                        avatarUrl = avatarUrl,
                        avatarStaticUrl = avatarStaticUrl,
                        bot = bot,
                        createdAt = createdAt,
                        displayName = displayName,
                        customEmojis = customEmojis.map(StatusCustomEmojiEntity::toDomain),
                        fields = fields.map(StatusAccountUserFieldEntity::toDomain),
                        followersCount = followersCount,
                        followingCount = followingCount,
                        headerUrl = headerUrl,
                        headerStaticUrl = headerStaticUrl,
                        id = id,
                        locked = locked,
                        note = note,
                        source = null,
                        statusesCount = statusesCount,
                        url = url,
                        username = username,
                        group = groupActor,
                        discoverable = discoverable,
                        suspended = suspended,
                        limited = limited
                    )
                }
            }

        }
    }

    override suspend fun insertAccount(account: Account) {
        db.await {
            upsertAccount(account)
        }
    }
}