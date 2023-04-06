/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.statusDetails.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rainy.mastodroid.R
import com.rainy.mastodroid.ui.elements.statusListItem.model.CustomEmojiItemModel
import com.rainy.mastodroid.ui.elements.statusListItem.StatusQuickActions
import com.rainy.mastodroid.ui.styledText.annotateMastodonEmojis
import com.rainy.mastodroid.ui.styledText.textInlineCustomEmojis
import com.rainy.mastodroid.ui.theme.MastodroidTheme
import com.rainy.mastodroid.util.ColorSchemePreviews
import com.rainy.mastodroid.util.ImmutableWrap
import kotlinx.datetime.Instant

@Composable
fun FocusedStatus(
    accountAvatarUrl: String,
    fullAccountName: String,
    accountUserName: String,
    usernameEmojis: ImmutableWrap<List<CustomEmojiItemModel>>,
    favorites: Int,
    isFavorite: Boolean,
    reblogs: Int,
    isRebloged: Boolean,
    replies: Int,
    statusAppText: String?,
    createdAt: ImmutableWrap<Instant>?,
    editedAt: ImmutableWrap<Instant>?,
    modifier: Modifier = Modifier,
    onFavoriteClicked: (Boolean) -> Unit = {},
    onReblogClicked: (Boolean) -> Unit = {},
    onAccountClick: () -> Unit = {},
    content: @Composable () -> Unit = {},
) {
    ElevatedCard(elevation = CardDefaults.elevatedCardElevation(
        defaultElevation = 2.dp
    ), modifier = modifier) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FocusedStatusHeader(
                accountAvatarUrl = accountAvatarUrl,
                fullAccountName = fullAccountName,
                usernameEmojis = usernameEmojis,
                accountUserName = accountUserName,
                onAccountClick = onAccountClick
            )
            content()
            StatusQuickActions(
                favorites = favorites,
                isFavorite = isFavorite,
                onFavoriteClicked = onFavoriteClicked,
                reblogs = reblogs,
                isRebloged = isRebloged,
                onReblogClicked = onReblogClicked,
                replies = replies,
                onReplyClicked = { /*TODO*/ },
                modifier = Modifier.fillMaxWidth()
            )
            Divider()
            if (!statusAppText.isNullOrEmpty()) {
                createdAt?.also {
                    Text(
                        text = stringResource(
                            id = R.string.posted_on_app,
                            createdAt.content.toEpochMilliseconds(),
                            statusAppText
                        ), style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

            } else {
                createdAt?.also {
                    Text(
                        text = stringResource(
                            id = R.string.posted_on,
                            createdAt.content.toEpochMilliseconds()
                        ), style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            if (editedAt != null) {
                Text(
                    text = stringResource(
                        id = R.string.edited_on,
                        editedAt.content.toEpochMilliseconds()
                    ), style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun FocusedStatusHeader(
    accountAvatarUrl: String,
    fullAccountName: String,
    usernameEmojis: ImmutableWrap<List<CustomEmojiItemModel>>,
    accountUserName: String,
    modifier: Modifier = Modifier,
    onAccountClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(accountAvatarUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clickable(onClick = onAccountClick)
                .clip(MaterialTheme.shapes.large)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = fullAccountName.annotateMastodonEmojis(emojiShortCodes = usernameEmojis.content.fastMap { it.shortcode }),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                inlineContent = textInlineCustomEmojis(usernameEmojis)
            )
            Text(
                text = stringResource(
                    id = R.string.username_handle,
                    accountUserName
                ),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        IconButton(onClick = { /*TODO*/ }) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                contentDescription = null,
                tint = LocalContentColor.current.copy(alpha = 0.5f)
            )
        }
    }
}

@ColorSchemePreviews
@Composable
private fun FocusedStatusPreview() {
    MastodroidTheme {
        Surface {
            FocusedStatus(
                accountAvatarUrl = "",
                fullAccountName = "Test",
                accountUserName = "Test",
                usernameEmojis = ImmutableWrap(listOf()),
                modifier = Modifier.fillMaxWidth(),
                replies = 4,
                reblogs = 4,
                favorites = 4,
                isFavorite = false,
                isRebloged = false,
                statusAppText = "Test app",
                createdAt = ImmutableWrap(Instant.parse("2023-03-01T12:09:45Z")),
                editedAt = ImmutableWrap(Instant.parse("2023-03-01T12:09:45Z"))
            )
        }
    }
}