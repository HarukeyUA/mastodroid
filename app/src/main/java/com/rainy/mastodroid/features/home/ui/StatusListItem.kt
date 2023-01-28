package com.rainy.mastodroid.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.media3.exoplayer.ExoPlayer
import com.rainy.mastodroid.features.home.model.ImageAttachmentItemModel
import com.rainy.mastodroid.features.home.model.StatusListItemModel
import com.rainy.mastodroid.ui.styledText.annotateMastodonContent
import com.rainy.mastodroid.ui.theme.MastodroidTheme
import com.rainy.mastodroid.util.ColorSchemePreview
import kotlinx.datetime.Instant

@Composable
fun StatusListItem(
    item: StatusListItemModel,
    exoPlayer: ExoPlayer? = null,
    onUrlClicked: (url: String) -> Unit,
    onFavoriteClicked: (id: String, action: Boolean) -> Unit,
    onReblogClicked: (id: String, action: Boolean) -> Unit,
    onSensitiveExpandClicked: (id: String) -> Unit
) {
    val view = LocalView.current
    val contentText by remember {
        derivedStateOf {
            if (view.isInEditMode) {
                AnnotatedString(item.content)
            } else {
                item.content.annotateMastodonContent(item.emojis.map { it.shortcode })
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
        onFavoriteClicked = { action: Boolean ->
            onFavoriteClicked(item.actionId, action)
        },
        onReblogClicked = { action ->
            onReblogClicked(item.actionId, action)
        },
        content = {
            if (item.isSensitive) {
                SpoilerStatusContent(
                    text = item.spoilerText,
                    isExpanded = item.isSensitiveExpanded,
                    onExpandClicked = { onSensitiveExpandClicked(item.id) }) {
                    StatusContent(contentText, item, onUrlClicked, exoPlayer)
                }
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
    exoPlayer: ExoPlayer?
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (contentText.isNotEmpty()) {
            StatusTextContent(text = contentText, customEmoji = item.emojis) { url ->
                onUrlClicked(url)
            }
        }
        if (item.attachments.isNotEmpty()) {
            StatusAttachmentsPreview(attachments = item.attachments, exoPlayer = exoPlayer)
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
                authorDisplayNameEmojis = listOf(),
                authorAccountHandle = "Orgia, exsul, et liberi.",
                authorAvatarUrl = "",
                content = "Hercle, burgus mirabilis!, victrix!",
                lastUpdate = Instant.parse("2022-12-17T23:11:43.130Z"),
                edited = true,
                emojis = listOf(),
                attachments = listOf(
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
                ),
                favorites = 34,
                reblogs = 342,
                replies = 16,
                isFavorite = false,
                isRebloged = false,
                isSensitive = false,
                spoilerText = "",
                isSensitiveExpanded = false,
                rebblogedByDisplayNameEmojis = listOf(),
                rebblogedByDisplayName = null
            ),
            onFavoriteClicked = { _, _ -> },
            onUrlClicked = {},
            onReblogClicked = { _, _ -> },
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
                authorDisplayNameEmojis = listOf(),
                authorAccountHandle = "Orgia, exsul, et liberi.",
                authorAvatarUrl = "",
                content = "",
                lastUpdate = Instant.parse("2022-12-17T23:11:43.130Z"),
                edited = true,
                emojis = listOf(),
                attachments = listOf(
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
                ),
                favorites = 34,
                reblogs = 342,
                replies = 16,
                isFavorite = false,
                isRebloged = false,
                isSensitive = false,
                spoilerText = "",
                isSensitiveExpanded = false,
                rebblogedByDisplayNameEmojis = listOf(),
                rebblogedByDisplayName = null
            ),
            onFavoriteClicked = { _, _ -> },
            onUrlClicked = {},
            onReblogClicked = { _, _ -> },
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
                authorDisplayNameEmojis = listOf(),
                authorAccountHandle = "Orgia, exsul, et liberi.",
                authorAvatarUrl = "",
                content = "Hercle, burgus mirabilis!, victrix!",
                lastUpdate = Instant.parse("2022-12-17T23:11:43.130Z"),
                edited = true,
                emojis = listOf(),
                attachments = listOf(),
                favorites = 34,
                reblogs = 342,
                replies = 16,
                isFavorite = false,
                isRebloged = false,
                isSensitive = false,
                spoilerText = "",
                isSensitiveExpanded = false,
                rebblogedByDisplayNameEmojis = listOf(),
                rebblogedByDisplayName = null
            ),
            onFavoriteClicked = { _, _ -> },
            onUrlClicked = {},
            onReblogClicked = { _, _ -> },
            onSensitiveExpandClicked = {}
        )
    }
}
