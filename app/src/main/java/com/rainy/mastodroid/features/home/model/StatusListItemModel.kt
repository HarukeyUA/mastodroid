package com.rainy.mastodroid.features.home.model

import androidx.compose.runtime.Stable
import com.rainy.mastodroid.core.domain.model.mediaAttachment.GifvAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.ImageAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.VideoAttachment
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.user.CustomEmoji
import kotlinx.datetime.Instant

@Stable
data class StatusListItemModel(
    val id: String,
    val authorDisplayName: String,
    val authorDisplayNameEmojis: List<CustomEmojiItemModel>,
    val authorAccountHandle: String,
    val authorAvatarUrl: String,
    val content: String,
    val lastUpdate: Instant?,
    val edited: Boolean,
    val emojis: List<CustomEmojiItemModel>,
    val attachments: List<MediaAttachmentItemModel>
)

fun Status.toStatusListItemModel(): StatusListItemModel {
    return StatusListItemModel(
        id = originalId,
        authorDisplayName = account.displayName,
        authorAccountHandle = account.accountUri,
        authorAvatarUrl = account.avatarUrl,
        authorDisplayNameEmojis = account.customEmojis.map(CustomEmoji::toItemModel),
        content = content,
        lastUpdate = editedAt ?: createdAt,
        edited = editedAt != null,
        emojis = customEmojis.map(CustomEmoji::toItemModel),
        attachments = mediaAttachments.map {
            when (it) {
                is ImageAttachment -> it.toItemModel()
                is VideoAttachment -> it.toItemModel()
                is GifvAttachment -> it.toItemModel()
            }
        }
    )
}
