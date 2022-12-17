package com.rainy.mastodroid.core.domain.model.user

import com.rainy.mastodroid.core.data.model.entity.LocalUserEntity
import com.rainy.mastodroid.core.data.model.response.CustomEmojiResponse
import com.rainy.mastodroid.core.data.model.response.user.AccountResponse
import kotlinx.datetime.Instant

data class User(
    val accountUri: String,
    val avatarUrl: String,
    val avatarStaticUrl: String,
    val bot: Boolean,
    val createdAt: Instant?,
    val displayName: String,
    val customEmojis: List<CustomEmoji>,
    val fields: List<UserField>,
    val followersCount: Int,
    val followingCount: Int,
    val headerUrl: String,
    val headerStaticUrl: String,
    val id: String,
    val locked: Boolean,
    val note: String,
    val source: UserSource?,
    val statusesCount: Int,
    val url: String,
    val username: String,
    val group: Boolean,
    val discoverable: Boolean,
    val suspended: Boolean,
    val limited: Boolean,
)

fun AccountResponse.toDomain(): User {
    return User(
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

fun LocalUserEntity.toDomain(): User {
    return User(
        accountUri = accountUri,
        avatarUrl = avatarUrl,
        avatarStaticUrl = avatarStaticUrl,
        bot = bot,
        createdAt = createdAt,
        displayName = displayName,
        customEmojis = listOf(),
        fields = listOf(),
        followersCount = followersCount,
        followingCount = followingCount,
        headerUrl = headerUrl,
        headerStaticUrl = headerStaticUrl,
        id = remoteId,
        locked = locked,
        note = note,
        source = null,
        statusesCount = statusesCount,
        url = url,
        username = username,
        group = group,
        discoverable = discoverable,
        suspended = suspended,
        limited = limited
    )
}
