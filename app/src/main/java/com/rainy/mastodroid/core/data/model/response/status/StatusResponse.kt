package com.rainy.mastodroid.core.data.model.response.status

import com.rainy.mastodroid.core.data.model.response.CustomEmojiResponse
import com.rainy.mastodroid.core.data.model.response.Visibility
import com.rainy.mastodroid.core.data.model.response.user.AccountResponse
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusResponse(
    @SerialName("id")
    val id: String?,
    @SerialName("uri")
    val uri: String?,
    @SerialName("created_at")
    val createdAt: Instant?,
    @SerialName("account")
    val account: AccountResponse?,
    @SerialName("content")
    val content: String?,
    @SerialName("visibility")
    val visibility: Visibility?,
    @SerialName("sensitive")
    val sensitive: Boolean?,
    @SerialName("spoiler_text")
    val spoilerText: String?,
    //@SerialName("media_attachments")
    //val mediaAttachments: List<Unit>, // TODO: https://docs.joinmastodon.org/entities/MediaAttachment/
    @SerialName("application") // Optional
    val application: StatusApplicationResponse?,
    @SerialName("mentions")
    val mentions: List<StatusMentionResponse>?,
    @SerialName("tags")
    val tags: List<StatusTagResponse>?,
    @SerialName("emojis")
    val emojis: List<CustomEmojiResponse>?,
    @SerialName("reblogs_count")
    val reblogsCount: Int?,
    @SerialName("favourites_count")
    val favouritesCount: Int?,
    @SerialName("replies_count")
    val repliesCount: Int?,
    @SerialName("url") // Optional
    val url: String?,
    @SerialName("in_reply_to_id") // Optional
    val inReplyToId: String?,
    @SerialName("in_reply_to_account_id") // Optional
    val inReplyToAccountId: String?,
    @SerialName("reblog") // Optional
    val reblog: StatusResponse?,
    //@SerialName("poll") // Optional
    //val poll: Unit?, // TODO: https://docs.joinmastodon.org/entities/Poll/
    @SerialName("card") // Optional
    val previewCard: PreviewCardResponse?,
    @SerialName("language") // Optional
    val language: String?,
    @SerialName("text") // Optional
    val text: String?,
    @SerialName("edited_at") // Optional
    val editedAt: Instant?,
    @SerialName("favourited") // Optional
    val favourited: Boolean?,
    @SerialName("reblogged") // Optional
    val reblogged: Boolean?,
    @SerialName("muted") // Optional
    val muted: Boolean?,
    @SerialName("bookmarked") // Optional
    val bookmarked: Boolean?,
    @SerialName("pinned") // Optional
    val pinned: Boolean?,
    //@SerialName("filtered") // Optional
    //val filtered: Unit? // TODO: entities/FilterResult
)
