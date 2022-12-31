package com.rainy.mastodroid.features.home.model

import androidx.compose.runtime.Stable
import com.rainy.mastodroid.core.domain.model.mediaAttachment.GifvAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.ImageAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.VideoAttachment
import com.rainy.mastodroid.core.domain.model.status.Status
import kotlinx.datetime.Instant

@Stable
data class StatusListItemModel(
    val id: String,
    val authorDisplayName: String,
    val authorAccountHandle: String,
    val authorAvatarUrl: String,
    val content: String,
    val lastUpdate: Instant?,
    val edited: Boolean,
    val attachments: List<MediaAttachmentItemModel>
)

fun Status.toStatusListItemModel(): StatusListItemModel {
    return StatusListItemModel(
        id = id,
        authorDisplayName = account.displayName,
        authorAccountHandle = account.accountUri,
        authorAvatarUrl = account.avatarUrl,
        content = content,
        lastUpdate = editedAt ?: createdAt,
        edited = editedAt != null,
        attachments = mediaAttachments.map {
            when (it) {
                is ImageAttachment -> it.toItemModel()
                is VideoAttachment -> it.toItemModel()
                is GifvAttachment -> it.toItemModel()
            }
        }
    )
}
