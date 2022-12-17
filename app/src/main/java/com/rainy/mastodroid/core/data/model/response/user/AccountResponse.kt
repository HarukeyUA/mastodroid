package com.rainy.mastodroid.core.data.model.response.user

import com.rainy.mastodroid.core.data.model.response.CustomEmojiResponse
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccountResponse(
    @SerialName("acct")
    val accountUri: String?,
    @SerialName("avatar")
    val avatar: String?,
    @SerialName("avatar_static")
    val avatarStatic: String?,
    @SerialName("bot")
    val bot: Boolean?,
    @SerialName("created_at")
    val createdAt: Instant?,
    @SerialName("display_name")
    val displayName: String?,
    @SerialName("emojis")
    val emojis: List<CustomEmojiResponse>?,
    @SerialName("fields")
    val fields: List<AccountFieldResponse>?,
    @SerialName("followers_count")
    val followersCount: Int?,
    @SerialName("following_count")
    val followingCount: Int?,
    @SerialName("header")
    val header: String?,
    @SerialName("header_static")
    val headerStatic: String?,
    @SerialName("id")
    val id: String,
    @SerialName("locked")
    val locked: Boolean?,
    @SerialName("note")
    val note: String?,
    @SerialName("source")
    val source: AccountSourceResponse?,
    @SerialName("statuses_count")
    val statusesCount: Int?,
    @SerialName("url")
    val url: String?,
    @SerialName("username")
    val username: String?,
    @SerialName("group")
    val group: Boolean?,
    @SerialName("discoverable")
    val discoverable: Boolean?,
    @SerialName("moved")
    val moved: AccountResponse?,
    @SerialName("suspended")
    val suspended: Boolean?,
    @SerialName("limited")
    val limited: Boolean?,
)
