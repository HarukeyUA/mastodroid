package com.rainy.mastodroid.features.statusDetails.model

import androidx.compose.runtime.Stable
import com.rainy.mastodroid.core.domain.model.mediaAttachment.GifvAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.ImageAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.VideoAttachment
import com.rainy.mastodroid.core.domain.model.status.Status
import com.rainy.mastodroid.core.domain.model.user.CustomEmoji
import com.rainy.mastodroid.ui.elements.statusListItem.model.CustomEmojiItemModel
import com.rainy.mastodroid.ui.elements.statusListItem.model.MediaAttachmentItemModel
import com.rainy.mastodroid.ui.elements.statusListItem.model.VideoAttachmentItemModel
import com.rainy.mastodroid.ui.elements.statusListItem.model.toItemModel
import com.rainy.mastodroid.util.ImmutableWrap
import kotlinx.datetime.Instant

@Stable
data class FocusedStatusItemModel(
    val id: String,
    val actionId: String,
    val inReplyToId: String?,
    val authorDisplayName: String,
    val authorDisplayNameEmojis: ImmutableWrap<List<CustomEmojiItemModel>>,
    val rebblogedByDisplayName: String?,
    val rebblogedByDisplayNameEmojis: ImmutableWrap<List<CustomEmojiItemModel>>,
    val authorAccountHandle: String,
    val authorAvatarUrl: String,
    val content: String,
    val edited: Boolean,
    val emojis: ImmutableWrap<List<CustomEmojiItemModel>>,
    val attachments: ImmutableWrap<List<MediaAttachmentItemModel>>,
    val favorites: Int,
    val reblogs: Int,
    val replies: Int,
    val isFavorite: Boolean,
    val isRebloged: Boolean,
    val isSensitive: Boolean,
    val spoilerText: String,
    val isSensitiveExpanded: Boolean,
    val createdAt: ImmutableWrap<Instant>?,
    val updatedAt: ImmutableWrap<Instant>?,
    val applicationName: String?,
) {
    val isSubjectForAutoPlay =
        attachments.content.size == 1 && attachments.content.firstOrNull() is VideoAttachmentItemModel && (!isSensitive || isSensitiveExpanded)
}

fun Status.toFocusedStatusItemModel(): FocusedStatusItemModel {
    return FocusedStatusItemModel(
        id = originalId,
        actionId = actionId,
        inReplyToId = inReplyToId,
        authorDisplayName = account.displayName,
        authorAccountHandle = account.accountUri,
        authorAvatarUrl = account.avatarUrl,
        authorDisplayNameEmojis = ImmutableWrap(account.customEmojis.map(CustomEmoji::toItemModel)),
        content = content,
        updatedAt = editedAt?.let(::ImmutableWrap),
        edited = editedAt != null,
        emojis = ImmutableWrap(customEmojis.map(CustomEmoji::toItemModel)),
        attachments = ImmutableWrap(mediaAttachments.map {
            when (it) {
                is ImageAttachment -> it.toItemModel()
                is VideoAttachment -> it.toItemModel()
                is GifvAttachment -> it.toItemModel()
            }
        }),
        favorites = favouritesCount,
        reblogs = reblogsCount,
        replies = repliesCount,
        isFavorite = favourited,
        isRebloged = reblogged,
        isSensitive = sensitive || spoilerText.isNotEmpty(),
        spoilerText = spoilerText,
        isSensitiveExpanded = false,
        rebblogedByDisplayName = reblogAuthorAccount?.displayName,
        rebblogedByDisplayNameEmojis = ImmutableWrap(
            reblogAuthorAccount?.customEmojis?.map(CustomEmoji::toItemModel)
                ?: listOf()
        ),
        createdAt = createdAt?.let(::ImmutableWrap),
        applicationName = application?.name
    )
}

