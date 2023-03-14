/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.entity.status

import com.rainy.mastodroid.core.domain.model.user.CustomEmoji
import com.rainy.mastodroid.core.domain.model.user.User
import com.rainy.mastodroid.core.domain.model.user.UserField
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class StatusAccountEntity(
    val accountUri: String,
    val avatarUrl: String,
    val avatarStaticUrl: String,
    val bot: Boolean,
    val createdAt: Instant?,
    val displayName: String,
    val customEmojis: List<StatusCustomEmojiEntity>,
    val fields: List<StatusAccountUserFieldEntity>,
    val followersCount: Int,
    val followingCount: Int,
    val headerUrl: String,
    val headerStaticUrl: String,
    val id: String,
    val locked: Boolean,
    val note: String,
    val statusesCount: Int,
    val url: String,
    val username: String,
    val group: Boolean,
    val discoverable: Boolean,
    val suspended: Boolean,
    val limited: Boolean,
)

fun User.toStatusAccountEntity(): StatusAccountEntity {
    return StatusAccountEntity(
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
        id = id,
        locked = locked,
        note = note,
        statusesCount = statusesCount,
        url = url,
        username = username,
        group = group,
        discoverable = discoverable,
        suspended = suspended,
        limited = limited
    )
}
