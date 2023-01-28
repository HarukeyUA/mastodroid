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
    val actionId: String,
    val authorDisplayName: String,
    val authorDisplayNameEmojis: List<CustomEmojiItemModel>,
    val rebblogedByDisplayName: String?,
    val rebblogedByDisplayNameEmojis: List<CustomEmojiItemModel>,
    val authorAccountHandle: String,
    val authorAvatarUrl: String,
    val content: String,
    val lastUpdate: Instant?,
    val edited: Boolean,
    val emojis: List<CustomEmojiItemModel>,
    val attachments: List<MediaAttachmentItemModel>,
    val favorites: Int,
    val reblogs: Int,
    val replies: Int,
    val isFavorite: Boolean,
    val isRebloged: Boolean,
    val isSensitive: Boolean,
    val spoilerText: String,
    val isSensitiveExpanded: Boolean
) {
    val isSubjectForAutoPlay =
        attachments.size == 1 && attachments.firstOrNull() is VideoAttachmentItemModel && (!isSensitive || isSensitiveExpanded)
}

fun Status.toStatusListItemModel(): StatusListItemModel {
    return StatusListItemModel(
        id = originalId,
        actionId = actionId,
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
        },
        favorites = favouritesCount,
        reblogs = reblogsCount,
        replies = repliesCount,
        isFavorite = favourited,
        isRebloged = reblogged,
        isSensitive = sensitive || spoilerText.isNotEmpty(),
        spoilerText = spoilerText,
        isSensitiveExpanded = false,
        rebblogedByDisplayName = reblogAuthorAccount?.displayName,
        rebblogedByDisplayNameEmojis = reblogAuthorAccount?.customEmojis?.map(CustomEmoji::toItemModel)
            ?: listOf()
    )
}
