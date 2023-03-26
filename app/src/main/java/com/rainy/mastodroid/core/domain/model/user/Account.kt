/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.model.user

import com.rainy.mastodroid.core.data.model.response.CustomEmojiResponse
import com.rainy.mastodroid.core.data.model.response.user.AccountResponse
import kotlinx.datetime.Instant

data class Account(
    val accountUri: String,
    val avatarUrl: String,
    val avatarStaticUrl: String,
    val bot: Boolean,
    val createdAt: Instant?,
    val displayName: String,
    val customEmojis: List<CustomEmoji>,
    val fields: List<UserField>,
    val followersCount: Long,
    val followingCount: Long,
    val headerUrl: String,
    val headerStaticUrl: String,
    val id: String,
    val locked: Boolean,
    val note: String,
    val source: UserSource?,
    val statusesCount: Long,
    val url: String,
    val username: String,
    val group: Boolean,
    val discoverable: Boolean,
    val suspended: Boolean,
    val limited: Boolean,
)

fun AccountResponse.toDomain(): Account {
    return Account(
        accountUri = accountUri ?: "",
        avatarUrl = avatar ?: "",
        avatarStaticUrl = avatarStatic ?: "",
        bot = bot ?: false,
        createdAt = createdAt,
        displayName = displayName ?: "",
        customEmojis = emojis?.mapNotNull(CustomEmojiResponse::toDomain) ?: listOf(),
        fields = fields?.map(com.rainy.mastodroid.core.data.model.response.user.AccountFieldResponse::toDomain)
            ?: listOf(),
        followersCount = followersCount ?: 0,
        followingCount = followingCount ?: 0,
        headerUrl = header ?: "",
        headerStaticUrl = headerStatic ?: "",
        id = id,
        locked = locked ?: false,
        note = note ?: "",
        source = source?.toDomain(),
        statusesCount = statusesCount ?: 0,
        url = url ?: "",
        username = username ?: "",
        group = group ?: false,
        discoverable = discoverable ?: true,
        suspended = suspended ?: false,
        limited = limited ?: false
    )
}
