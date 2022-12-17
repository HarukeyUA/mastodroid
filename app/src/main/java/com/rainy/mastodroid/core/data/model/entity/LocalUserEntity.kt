package com.rainy.mastodroid.core.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rainy.mastodroid.core.domain.model.user.User
import kotlinx.datetime.Instant

// TODO: Store profile fields, emojis and source if needed
@Entity
data class LocalUserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val remoteId: String,
    val instanceHost: String,
    val authToken: String,
    val accountUri: String,
    val avatarUrl: String,
    val avatarStaticUrl: String,
    val bot: Boolean,
    val createdAt: Instant?,
    val displayName: String,
    val followersCount: Int,
    val followingCount: Int,
    val headerUrl: String,
    val headerStaticUrl: String,
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

fun User.toLocalUserEntity(
    authToken: String,
    instanceHost: String
): LocalUserEntity {
    return LocalUserEntity(
        remoteId = id,
        authToken = authToken,
        instanceHost = instanceHost,
        accountUri = accountUri,
        avatarUrl = avatarUrl,
        avatarStaticUrl = avatarStaticUrl,
        bot = bot,
        createdAt = createdAt,
        displayName = displayName,
        followersCount = followersCount,
        followingCount = followingCount,
        headerUrl = headerUrl,
        headerStaticUrl = headerStaticUrl,
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
