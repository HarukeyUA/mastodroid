package com.rainy.mastodroid.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import androidx.media3.exoplayer.ExoPlayer
import com.rainy.mastodroid.features.home.model.ImageAttachmentItemModel
import com.rainy.mastodroid.features.home.model.StatusListItemModel
import com.rainy.mastodroid.ui.styledText.annotateMastodonContent
import com.rainy.mastodroid.ui.theme.MastodroidTheme
import com.rainy.mastodroid.util.ColorSchemePreview
import com.rainy.mastodroid.util.ImmutableWrap
import kotlinx.datetime.Instant

@Composable
fun StatusListItem(
    item: StatusListItemModel,
    onUrlClicked: (url: String) -> Unit,
    onFavoriteClicked: (id: String, actionId: String, action: Boolean) -> Unit,
    onReblogClicked: (id: String, actionId: String, action: Boolean) -> Unit,
    onSensitiveExpandClicked: (id: String) -> Unit,
    isReply: Boolean,
    isRepliedTo: Boolean,
    exoPlayer: ImmutableWrap<ExoPlayer>? = null,
) {
    val view = LocalView.current
    val contentText by remember {
        derivedStateOf {
            if (view.isInEditMode) {
                AnnotatedString(item.content)
            } else {
                item.content.annotateMastodonContent(item.emojis.content.fastMap { it.shortcode })
            }
        }
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
        isRepliedTo = isRepliedTo,
        isReply = isReply,
        onFavoriteClicked = { action: Boolean ->
            onFavoriteClicked(item.id, item.actionId, action)
        },
        onReblogClicked = { action ->
            onReblogClicked(item.id, item.actionId, action)
        },
        content = {
            if (item.isSensitive) {
                SpoilerStatusContent(
                    text = item.spoilerText,
                    isExpanded = item.isSensitiveExpanded,
                    onExpandClicked = { onSensitiveExpandClicked(item.id) },
                    content = {
                        StatusContent(contentText, item, onUrlClicked, exoPlayer)
                    }
                )
            } else {
                StatusContent(contentText, item, onUrlClicked, exoPlayer)
            }
        }

    )
}

@Composable
fun StatusContent(
    contentText: AnnotatedString,
    item: StatusListItemModel,
    onUrlClicked: (url: String) -> Unit,
    exoPlayer: ImmutableWrap<ExoPlayer>?,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = modifier
    ) {
        if (contentText.isNotEmpty()) {
            StatusTextContent(text = contentText, customEmoji = item.emojis) { url ->
                onUrlClicked(url)
            }
        }
        if (item.attachments.content.isNotEmpty()) {
            StatusAttachmentsPreview(
                attachments = item.attachments,
                exoPlayer = exoPlayer
            )
        }
    }
}

@Composable
@ColorSchemePreview
private fun StatusListItemAttachmentsWithTextPreview() {
    MastodroidTheme {
        StatusListItem(
            item = StatusListItemModel(
                id = "101",
                actionId = "",
                authorDisplayName = "Talis, clemens exemplars saepe imperium de peritus, rusticus vortex.",
                authorDisplayNameEmojis = ImmutableWrap(listOf()),
                authorAccountHandle = "Orgia, exsul, et liberi.",
                authorAvatarUrl = "",
                content = "Hercle, burgus mirabilis!, victrix!",
                lastUpdate = ImmutableWrap(Instant.parse("2022-12-17T23:11:43.130Z")),
                edited = true,
                emojis = ImmutableWrap(listOf()),
                attachments = ImmutableWrap(
                    listOf(
                        ImageAttachmentItemModel(
                            id = "",
                            url = "",
                            previewUrl = "",
                            remoteUrl = "",
                            description = "",
                            blurHash = "",
                            width = 300,
                            height = 300,
                            aspect = 1f
                        ),
                        ImageAttachmentItemModel(
                            id = "",
                            url = "",
                            previewUrl = "",
                            remoteUrl = "",
                            description = "",
                            blurHash = "",
                            width = 300,
                            height = 300,
                            aspect = 1f
                        )
                    )
                ),
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
                inReplyToId = null
            ),
            isRepliedTo = false,
            isReply = false,
            onFavoriteClicked = { _, _, _ -> },
            onUrlClicked = {},
            onReblogClicked = { _, _, _ -> },
            onSensitiveExpandClicked = {}
        )
    }
}

@Composable
@ColorSchemePreview
private fun StatusListItemAttachmentsPreview() {
    MastodroidTheme {
        StatusListItem(
            item = StatusListItemModel(
                id = "101",
                actionId = "",
                authorDisplayName = "Talis, clemens exemplars saepe imperium de peritus, rusticus vortex.",
                authorDisplayNameEmojis = ImmutableWrap(listOf()),
                authorAccountHandle = "Orgia, exsul, et liberi.",
                authorAvatarUrl = "",
                content = "",
                lastUpdate = ImmutableWrap(Instant.parse("2022-12-17T23:11:43.130Z")),
                edited = true,
                emojis = ImmutableWrap(listOf()),
                attachments = ImmutableWrap(
                    listOf(
                        ImageAttachmentItemModel(
                            id = "",
                            url = "",
                            previewUrl = "",
                            remoteUrl = "",
                            description = "",
                            blurHash = "",
                            width = 300,
                            height = 300,
                            aspect = 1f
                        ),
                        ImageAttachmentItemModel(
                            id = "",
                            url = "",
                            previewUrl = "",
                            remoteUrl = "",
                            description = "",
                            blurHash = "",
                            width = 300,
                            height = 300,
                            aspect = 1f
                        )
                    )
                ),
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
                inReplyToId = null
            ),
            isRepliedTo = false,
            isReply = false,
            onFavoriteClicked = { _, _, _ -> },
            onUrlClicked = {},
            onReblogClicked = { _, _, _ -> },
            onSensitiveExpandClicked = {}
        )
    }
}

@Composable
@ColorSchemePreview
private fun StatusListItemTextPreview() {
    MastodroidTheme {
        StatusListItem(
            item = StatusListItemModel(
                id = "101",
                actionId = "",
                authorDisplayName = "Talis, clemens exemplars saepe imperium de peritus, rusticus vortex.",
                authorDisplayNameEmojis = ImmutableWrap(listOf()),
                authorAccountHandle = "Orgia, exsul, et liberi.",
                authorAvatarUrl = "",
                content = "Hercle, burgus mirabilis!, victrix!",
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
                inReplyToId = null
            ),
            isRepliedTo = false,
            isReply = false,
            onFavoriteClicked = { _, _, _ -> },
            onUrlClicked = {},
            onReblogClicked = { _, _, _ -> },
            onSensitiveExpandClicked = {}
        )
    }
}
