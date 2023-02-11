package com.rainy.mastodroid.features.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults.cardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastMap
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rainy.mastodroid.R
import com.rainy.mastodroid.features.home.model.CustomEmojiItemModel
import com.rainy.mastodroid.ui.styledText.annotateMastodonEmojis
import com.rainy.mastodroid.ui.styledText.textInlineCustomEmojis
import com.rainy.mastodroid.ui.theme.MastodroidTheme
import com.rainy.mastodroid.util.ColorSchemePreview
import com.rainy.mastodroid.util.ImmutableWrap
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import java.util.concurrent.TimeUnit

const val YEAR_IN_DAYS = 365

@Composable
fun StatusCard(
    fullAccountName: String,
    accountUserName: String,
    accountAvatarUrl: String,
    updatedTime: ImmutableWrap<Instant>?,
    isEdited: Boolean,
    usernameEmojis: ImmutableWrap<List<CustomEmojiItemModel>>,
    reblogs: Int,
    favorites: Int,
    replies: Int,
    isFavorite: Boolean,
    isRebloged: Boolean,
    rebblogedByAccountUserName: String?,
    rebblogedByUsernameEmojis: ImmutableWrap<List<CustomEmojiItemModel>>,
    onFavoriteClicked: (Boolean) -> Unit,
    onReblogClicked: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    Card(
        shape = MaterialTheme.shapes.medium, colors = cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        ), modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            if (rebblogedByAccountUserName != null) {
                ReblogLabel(
                    rebblogedByAccountUserName = rebblogedByAccountUserName,
                    rebblogedByUsernameEmojis = rebblogedByUsernameEmojis
                )
            }
            StatusHeadInfo(
                accountAvatarUrl = accountAvatarUrl,
                fullAccountName = fullAccountName,
                usernameEmojis = usernameEmojis,
                accountUserName = accountUserName,
                updatedTime = updatedTime,
                isEdited = isEdited
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
        }
    }
}

@Composable
fun StatusHeadInfo(
    accountAvatarUrl: String,
    fullAccountName: String,
    usernameEmojis: ImmutableWrap<List<CustomEmojiItemModel>>,
    accountUserName: String,
    updatedTime: ImmutableWrap<Instant>?,
    isEdited: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(accountAvatarUrl)
                .crossfade(true)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(48.dp)
                .clip(MaterialTheme.shapes.large)
        )
        Column(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .weight(1f)
                .padding(start = 4.dp)
        ) {
            Text(
                text = fullAccountName.annotateMastodonEmojis(emojiShortCodes = usernameEmojis.content.fastMap { it.shortcode }),
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                inlineContent = textInlineCustomEmojis(usernameEmojis)
            )
            Row {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant
                ) {
                    Text(
                        text = stringResource(
                            id = R.string.username_handle,
                            accountUserName
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .alignByBaseline()
                            .weight(1f, fill = false)
                    )

                    updatedTime?.also {
                        Text(
                            "\u2022",
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier
                                .padding(start = 2.dp)
                                .alignByBaseline()
                        )
                        StatusCardTimeCounter(
                            it,
                            modifier = Modifier
                                .padding(start = 2.dp)
                                .alignByBaseline()
                        )
                    }
                    if (isEdited) {
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = stringResource(id = R.string.edited),
                            modifier = Modifier
                                .size(14.dp)
                                .padding(start = 2.dp)
                                .align(Alignment.CenterVertically)
                        )
                    }
                }

            }
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

@Composable
fun ReblogLabel(
    rebblogedByAccountUserName: String,
    rebblogedByUsernameEmojis: ImmutableWrap<List<CustomEmojiItemModel>>,
    modifier: Modifier = Modifier
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = modifier) {
        CompositionLocalProvider(
            LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant,
            LocalTextStyle provides MaterialTheme.typography.bodyMedium
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_repeat),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.CenterVertically)
            )
            Text(
                text = stringResource(
                    id = R.string.reblogged_by,
                    rebblogedByAccountUserName
                ).annotateMastodonEmojis(emojiShortCodes = rebblogedByUsernameEmojis.content.fastMap { it.shortcode }),
                inlineContent = textInlineCustomEmojis(rebblogedByUsernameEmojis),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun StatusCardTimeCounter(lastUpdated: ImmutableWrap<Instant>, modifier: Modifier = Modifier) {
    var timeSinceUpdate by remember {
        mutableStateOf(lastUpdated.content.minus(Clock.System.now()).absoluteValue)
    }
    LaunchedEffect(lastUpdated) {
        while (isActive) {
            timeSinceUpdate = lastUpdated.content.minus(Clock.System.now()).absoluteValue
            delay(
                if (timeSinceUpdate.inWholeMinutes > 0) {
                    TimeUnit.MINUTES.toMillis(1)
                } else {
                    1000L
                }
            )
        }
    }
    Text(
        text = when {
            timeSinceUpdate.inWholeDays > 0L -> {
                if (timeSinceUpdate.inWholeDays >= YEAR_IN_DAYS) {
                    stringResource(
                        id = R.string.years,
                        timeSinceUpdate.inWholeDays.div(YEAR_IN_DAYS)
                    )
                } else {
                    stringResource(id = R.string.days, timeSinceUpdate.inWholeDays)
                }
            }

            timeSinceUpdate.inWholeHours > 0L -> {
                stringResource(id = R.string.hours, timeSinceUpdate.inWholeHours)
            }

            timeSinceUpdate.inWholeMinutes > 0L -> {
                stringResource(id = R.string.minutes, timeSinceUpdate.inWholeMinutes)
            }

            else -> stringResource(id = R.string.seconds, timeSinceUpdate.inWholeSeconds)
        },
        style = MaterialTheme.typography.labelSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
    )

}

@Composable
@ColorSchemePreview
private fun StatusCardPreview() {
    MastodroidTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            StatusCard(
                fullAccountName = "Historias mori!",
                accountUserName = "Ecce, emeritis gabalium!",
                accountAvatarUrl = "",
                updatedTime = ImmutableWrap(Instant.parse("2021-12-17T23:11:43.130Z")),
                isEdited = true,
                content = {},
                usernameEmojis = ImmutableWrap(listOf()),
                reblogs = 302,
                favorites = 9383,
                replies = 2,
                isRebloged = false,
                isFavorite = false,
                onFavoriteClicked = {},
                onReblogClicked = {},
                rebblogedByAccountUserName = "Test",
                rebblogedByUsernameEmojis = ImmutableWrap(listOf())
            )
        }

    }
}
