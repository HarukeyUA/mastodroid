/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.database.extensions

import com.rainy.mastodroid.Database
import com.rainy.mastodroid.core.data.model.entity.status.toStatusAccountUserFieldEntity
import com.rainy.mastodroid.core.data.model.entity.status.toStatusCustomEmojiEntity
import com.rainy.mastodroid.core.domain.model.user.Account
import com.rainy.mastodroid.core.domain.model.user.CustomEmoji
import com.rainy.mastodroid.core.domain.model.user.UserField

fun Database.upsertAccount(account: Account) {
    with(account) {
        accountQueries.upsertAccount(
            accountUri = accountUri,
            avatarUrl = avatarUrl,
            avatarStaticUrl = avatarStaticUrl,
            bot = bot,
            createdAt = createdAt,
            displayName = displayName,
            customEmojis = customEmojis.map(CustomEmoji::toStatusCustomEmojiEntity),
            fields = fields.map(UserField::toStatusAccountUserFieldEntity),
            followersCount = followersCount,
            followingCount = followingCount,
            headerUrl = headerUrl,
            headerStaticUrl = headerStaticUrl,
            locked = locked,
            note = note,
            statusesCount = statusesCount,
            url = url,
            username = username,
            groupActor = group,
            discoverable = discoverable,
            suspended = suspended,
            limited = limited,
            id = id
        )
    }
}