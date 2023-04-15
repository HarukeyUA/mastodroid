/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.ui.elements.statusListItem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import com.rainy.mastodroid.core.domain.model.status.statusThread.ReplyType
import com.rainy.mastodroid.ui.elements.statusListItem.model.CustomEmojiItemModel
import com.rainy.mastodroid.ui.elements.statusListItem.model.MediaAttachmentItemModel
import com.rainy.mastodroid.ui.elements.statusListItem.model.StatusListItemModel
import com.rainy.mastodroid.ui.theme.MastodroidTheme
import com.rainy.mastodroid.util.ColorSchemePreviews
import com.rainy.mastodroid.util.ImmutableWrap
import kotlinx.datetime.Instant

@Composable
fun StatusListItem(
    item: StatusListItemModel,
    reply: ReplyType,
    repliedTo: ReplyType,
    onUrlClicked: (url: String) -> Unit,
    onFavoriteClicked: (id: String, action: Boolean) -> Unit,
    onReblogClicked: (id: String, action: Boolean) -> Unit,
    onSensitiveExpandClicked: (id: String) -> Unit,
    exoPlayer: ImmutableWrap<ExoPlayer>? = null,
    onClick: (String) -> Unit = {},
    onAccountClick: (String) -> Unit = {}
) {
    val onStatusClickedLambda = remember(item) {
        { onClick(item.actionId) }
    }
    val onFavoriteClickedLambda = remember(item) {
        { action: Boolean -> onFavoriteClicked(item.actionId, action) }
    }
    val onReblogClickedLambda = remember(item) {
        { action: Boolean -> onReblogClicked(item.actionId, action) }
    }
    val onAccountClickLambda = remember(item) {
        { onAccountClick(item.authorId) }
    }
    val onSensitiveExpandClickedLambda = remember(item) {
        { onSensitiveExpandClicked(item.id) }
    }
    StatusCard(fullAccountName = item.authorDisplayName,
        accountUserName = item.authorAccountHandle,
        accountAvatarUrl = item.authorAvatarUrl,
        updatedTime = item.lastUpdate,
        isEdited = item.edited,
        usernameEmojis = item.authorDisplayNameEmojis,
        favorites = item.favorites,
        reblogs = item.reblogs,
        replies = item.replies,
        isFavorite = item.isFavorite,
        isRebloged = item.isRebloged,
        rebblogedByAccountUserName = item.rebblogedByDisplayName,
        rebblogedByUsernameEmojis = item.rebblogedByDisplayNameEmojis,
        repliedTo = repliedTo,
        reply = reply,
        onFavoriteClicked = onFavoriteClickedLambda,
        onReblogClicked = onReblogClickedLambda,
        onStatusClicked = onStatusClickedLambda,
        onAccountClick = onAccountClickLambda,
        content = {
            if (item.isSensitive) {
                SpoilerStatusContent(
                    text = item.spoilerText,
                    isExpanded = item.isSensitiveExpanded,
                    onExpandClicked = onSensitiveExpandClickedLambda,
                    content = {
                        StatusContent(
                            contentText = item.content,
                            customEmojis = item.emojis,
                            attachments = item.attachments,
                            onUrlClicked = onUrlClicked,
                            exoPlayer = exoPlayer,
                            onTextClicked = onStatusClickedLambda
                        )
                    }
                )
            } else {
                StatusContent(
                    contentText = item.content,
                    customEmojis = item.emojis,
                    attachments = item.attachments,
                    onUrlClicked = onUrlClicked,
                    exoPlayer = exoPlayer,
                    onTextClicked = onStatusClickedLambda
                )
            }
        }

    )
}

@Composable
fun StatusContent(
    contentText: AnnotatedString,
    customEmojis: ImmutableWrap<List<CustomEmojiItemModel>>,
    attachments: ImmutableWrap<List<MediaAttachmentItemModel>>,
    onUrlClicked: (url: String) -> Unit,
    onTextClicked: () -> Unit,
    exoPlayer: ImmutableWrap<ExoPlayer>?,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        if (contentText.isNotEmpty()) {
            StatusTextContent(
                text = contentText,
                customEmoji = customEmojis,
                onTextClicked = onTextClicked,
                onUrlClicked = onUrlClicked
            )
        }
        if (attachments.content.isNotEmpty()) {
            StatusAttachmentsPreview(
                attachments = attachments,
                exoPlayer = exoPlayer
            )
        }
    }
}

@Composable
@ColorSchemePreviews
private fun StatusListItemAttachmentsWithTextPreview(
    @PreviewParameter(StatusListItemPreviewProvider::class) status: StatusListItemModel
) {
    MastodroidTheme {
        Surface {
            StatusListItem(
                item = status,
                reply = ReplyType.NONE,
                repliedTo = ReplyType.NONE,
                onFavoriteClicked = { _: String, _: Boolean -> },
                onUrlClicked = {},
                onReblogClicked = { _: String, _: Boolean -> },
                onSensitiveExpandClicked = {}
            )
        }
    }
}

@Composable
@ColorSchemePreviews
private fun StatusListItemAttachmentsPreview(
    @PreviewParameter(StatusListItemPreviewProvider::class) status: StatusListItemModel
) {
    MastodroidTheme {
        Surface {
            StatusListItem(
                item = status,
                reply = ReplyType.NONE,
                repliedTo = ReplyType.NONE,
                onFavoriteClicked = { _: String, _: Boolean -> },
                onUrlClicked = {},
                onReblogClicked = { _: String, _: Boolean -> },
                onSensitiveExpandClicked = {}
            )
        }
    }
}

@Composable
@ColorSchemePreviews
private fun StatusListItemTextPreview(
    @PreviewParameter(StatusListItemPreviewProvider::class) status: StatusListItemModel
) {
    MastodroidTheme {
        Surface {
            StatusListItem(
                item = status,
                reply = ReplyType.NONE,
                repliedTo = ReplyType.NONE,
                onFavoriteClicked = { _: String, _: Boolean -> },
                onUrlClicked = {},
                onReblogClicked = { _: String, _: Boolean -> },
                onSensitiveExpandClicked = {}
            )
        }

    }
}

internal class StatusListItemPreviewProvider :
    PreviewParameterProvider<StatusListItemModel> {
    override val values: Sequence<StatusListItemModel> = sequenceOf(
        StatusListItemModel(
            id = "101",
            actionId = "",
            authorDisplayName = AnnotatedString("Talis, clemens exemplars saepe imperium de peritus, rusticus vortex."),
            authorDisplayNameEmojis = ImmutableWrap(listOf()),
            authorAccountHandle = "Orgia, exsul, et liberi.",
            authorAvatarUrl = "",
            content = AnnotatedString("Hercle, burgus mirabilis!, victrix!"),
            lastUpdate = ImmutableWrap(Instant.parse("2022-12-17T23:11:43.130Z")),
            edited = true,
            emojis = ImmutableWrap(listOf()),
            attachments = ImmutableWrap(listOf()),
            favorites = 34,
            reblogs = 342,
            replies = 16,
            isFavorite = false,
            isRebloged = false,
            isSensitive = false,
            spoilerText = "",
            isSensitiveExpanded = false,
            rebblogedByDisplayNameEmojis = ImmutableWrap(listOf()),
            rebblogedByDisplayName = null,
            inReplyToId = null,
            authorId = "101"
        )
    )
}
