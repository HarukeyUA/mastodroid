package com.rainy.mastodroid.core.data.model.entity.status

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import com.rainy.mastodroid.core.data.model.response.StatusVisibility
import com.rainy.mastodroid.core.domain.model.mediaAttachment.GifvAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.ImageAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.VideoAttachment
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.status.StatusMention
import com.rainy.mastodroid.core.domain.model.status.StatusTag
import com.rainy.mastodroid.core.domain.model.user.CustomEmoji
import kotlinx.datetime.Instant

private const val ORIGINAL_ID_KEY = "originalId"

@Entity(
    primaryKeys = [
        ORIGINAL_ID_KEY,
    ]
)
data class StatusEntity(
    @ColumnInfo(name = ORIGINAL_ID_KEY)
    val originalId: String,
    val rebloggedStatusId: String?,
    val reblogAuthorAccount: StatusAccountEntity?,
    val uri: String,
    val createdAt: Instant?,
    val account: StatusAccountEntity,
    val content: String,
    val visibility: StatusVisibility,
    val sensitive: Boolean,
    val spoilerText: String,
    @Embedded("app_")
    val application: StatusApplicationEntity?,
    val mentions: List<StatusMentionEntity>,
    val tags: List<StatusTagEntity>,
    val customEmojis: List<StatusCustomEmojiEntity>,
    val reblogsCount: Int,
    val favouritesCount: Int,
    val repliesCount: Int,
    val url: String?,
    val inReplyToId: String?,
    val inReplyToAccountId: String?,
    @Embedded("urlPreview_")
    val urlPreviewCard: UrlPreviewCardEntity?,
    val language: String?,
    val text: String?,
    val editedAt: Instant?,
    val favourited: Boolean,
    val reblogged: Boolean,
    val muted: Boolean,
    val bookmarked: Boolean,
    val pinned: Boolean,
    val mediaAttachments: List<MediaAttachmentEntity>
)

fun Status.toStatusEntity(): StatusEntity {
    return StatusEntity(
        originalId = originalId,
        rebloggedStatusId = rebloggedStatusId,
        reblogAuthorAccount = reblogAuthorAccount?.toStatusAccountEntity(),
        uri = uri,
        createdAt = createdAt,
        account = account.toStatusAccountEntity(),
        content = content,
        visibility = visibility,
        sensitive = sensitive,
        spoilerText = spoilerText,
        application = application?.toStatusApplicationEntity(),
        mentions = mentions.map(StatusMention::toStatusMentionEntity),
        tags = tags.map(StatusTag::toStatusTagEntity),
        customEmojis = customEmojis.map(CustomEmoji::toStatusCustomEmojiEntity),
        reblogsCount = reblogsCount,
        favouritesCount = favouritesCount,
        repliesCount = repliesCount,
        url = url,
        inReplyToId = inReplyToId,
        inReplyToAccountId = inReplyToAccountId,
        urlPreviewCard = urlPreviewCard?.toUrlPreviewCardEntity(),
        language = language,
        text = text,
        editedAt = editedAt,
        favourited = favourited,
        reblogged = reblogged,
        muted = muted,
        bookmarked = bookmarked,
        pinned = pinned,
        mediaAttachments = mediaAttachments.map {
            when (it) {
                is GifvAttachment -> it.toGifvAttachmentEntity()
                is ImageAttachment -> it.toImageAttachmentEntity()
                is VideoAttachment -> it.toVideoAttachmentEntity()
            }
        }

    )
}