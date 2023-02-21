package com.rainy.mastodroid.features.home.ui

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
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
import kotlin.math.max

const val YEAR_IN_DAYS = 365

private const val REBLOG_LABEL_ID = "reblog"
private const val STATUS_INFO_ID = "statusInfo"
private const val CONTENT_AND_ACTIONS_ID = "contentAndActions"
private const val TOP_REPLY_LINE_ID = "topLine"
private const val BOTTOM_REPLY_LINE_ID = "bottomLine"
private const val AVATAR_ID = "avatar"

private const val AVATAR_DIMENSIONS_DP = 48
private const val STATUS_CONTENT_PADDING_DP = 8
private const val STATUS_SPACING_DP = 4

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
    isReply: Boolean,
    isRepliedTo: Boolean,
    onFavoriteClicked: (Boolean) -> Unit,
    onReblogClicked: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit = {},
) {
    Card(
        shape = MaterialTheme.shapes.medium, colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        ), modifier = modifier.fillMaxWidth()
    ) {
        Layout(content = {
            val lineColor = MaterialTheme.colorScheme.secondaryContainer
            if (rebblogedByAccountUserName != null) {
                ReblogLabel(
                    rebblogedByAccountUserName = rebblogedByAccountUserName,
                    rebblogedByUsernameEmojis = rebblogedByUsernameEmojis,
                    modifier = Modifier
                        .layoutId(REBLOG_LABEL_ID)
                        .padding(start = 4.dp, bottom = 4.dp)
                )
            }
            StatusHeadInfo(
                fullAccountName = fullAccountName,
                usernameEmojis = usernameEmojis,
                accountUserName = accountUserName,
                updatedTime = updatedTime,
                isEdited = isEdited,
                modifier = Modifier.layoutId(STATUS_INFO_ID)
            )
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(accountAvatarUrl)
                    .crossfade(true).build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(AVATAR_DIMENSIONS_DP.dp)
                    .clip(MaterialTheme.shapes.large)
                    .layoutId(AVATAR_ID)
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .layoutId(CONTENT_AND_ACTIONS_ID)
                    .padding(top = 4.dp)
            ) {
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

            Canvas(modifier = Modifier.layoutId(TOP_REPLY_LINE_ID), onDraw = {
                drawLine(
                    lineColor,
                    start = Offset((size.width / 2), 0.dp.toPx()),
                    end = Offset((size.width / 2), size.height),
                    strokeWidth = 2.dp.toPx()
                )
            })
            Canvas(modifier = Modifier.layoutId(BOTTOM_REPLY_LINE_ID), onDraw = {
                drawLine(
                    lineColor,
                    start = Offset((size.width / 2), 0.dp.toPx()),
                    end = Offset((size.width / 2), size.height),
                    strokeWidth = 2.dp.toPx()
                )
            })
        }, measurePolicy = { measurables, constraints ->
            val combinedAxisContentPadding = (STATUS_CONTENT_PADDING_DP * 2).dp.roundToPx()
            val avatar = measurables.first { it.layoutId == AVATAR_ID }.measure(
                Constraints.fixed(
                    AVATAR_DIMENSIONS_DP.dp.roundToPx(), AVATAR_DIMENSIONS_DP.dp.roundToPx()
                )
            )
            val reblogLabel = measurables.firstOrNull { it.layoutId == REBLOG_LABEL_ID }?.measure(
                if (isReply) {
                    constraints.copy(
                        maxWidth = constraints.maxWidth - avatar.width - combinedAxisContentPadding
                    )
                } else {
                    constraints
                }
            )
            val statusInfo = measurables.first { it.layoutId == STATUS_INFO_ID }.measure(
                constraints.copy(maxWidth = constraints.maxWidth - (avatar.width) - combinedAxisContentPadding)
            )

            val contentAction = measurables.first { it.layoutId == CONTENT_AND_ACTIONS_ID }.measure(
                if (isRepliedTo) {
                    constraints.copy(maxWidth = constraints.maxWidth - (avatar.width) - combinedAxisContentPadding - STATUS_SPACING_DP.dp.roundToPx())
                } else {
                    constraints.copy(maxWidth = constraints.maxWidth - combinedAxisContentPadding)
                }
            )

            val height = contentAction.height + statusInfo.height + (reblogLabel?.height
                ?: 0) + combinedAxisContentPadding

            layout(constraints.maxWidth, height) {
                val reblogLabelX = if (!isReply) {
                    STATUS_CONTENT_PADDING_DP.dp.roundToPx()
                } else {
                    avatar.width + STATUS_CONTENT_PADDING_DP.dp.roundToPx()
                }
                val reblogLabelY = STATUS_CONTENT_PADDING_DP.dp.roundToPx()

                val avatarYVariant = if (reblogLabel != null) {
                    reblogLabel.height + reblogLabelY
                } else {
                    STATUS_CONTENT_PADDING_DP.dp.roundToPx()
                }

                val statusInfoYVariant = if (reblogLabel != null) {
                    reblogLabel.height + reblogLabelY
                } else {
                    STATUS_CONTENT_PADDING_DP.dp.roundToPx()
                }

                val statusInfoX = avatar.width + STATUS_CONTENT_PADDING_DP.dp.roundToPx()
                val statusInfoY = if (avatar.height >= statusInfo.height) {
                    ((statusInfo.height - avatar.height) / 2) + avatarYVariant
                } else {
                    statusInfoYVariant
                }

                val avatarX = STATUS_CONTENT_PADDING_DP.dp.roundToPx()
                val avatarY = if (avatar.height >= statusInfo.height) {
                    avatarYVariant
                } else {
                    ((statusInfo.height - avatar.height) / 2) + statusInfoY
                }

                val contentX =
                    if (isRepliedTo) statusInfoX + STATUS_SPACING_DP.dp.roundToPx() else STATUS_CONTENT_PADDING_DP.dp.roundToPx()
                val contentY = max(avatarY + avatar.height, statusInfoY + statusInfo.height)

                reblogLabel?.placeRelative(x = reblogLabelX, y = reblogLabelY)
                statusInfo.placeRelative(x = statusInfoX, y = statusInfoY)
                avatar.placeRelative(x = avatarX, y = avatarY)
                contentAction.placeRelative(x = contentX, y = contentY)

                if (isReply) {
                    val topLine = measurables.first { it.layoutId == TOP_REPLY_LINE_ID }.measure(
                        Constraints.fixed(
                            width = avatar.width, height = avatarY
                        )
                    )
                    topLine.placeRelative(avatarX, 0)
                }

                if (isRepliedTo) {
                    val bottomLine =
                        measurables.first { it.layoutId == BOTTOM_REPLY_LINE_ID }.measure(
                            Constraints.fixed(
                                width = avatar.width, height = height - (avatarY + avatar.height)
                            )
                        )
                    bottomLine.placeRelative(avatarX, avatarY + avatar.height)
                }
            }
        })
    }
}

@Composable
fun StatusHeadInfo(
    fullAccountName: String,
    usernameEmojis: ImmutableWrap<List<CustomEmojiItemModel>>,
    accountUserName: String,
    updatedTime: ImmutableWrap<Instant>?,
    isEdited: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
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
                rebblogedByUsernameEmojis = ImmutableWrap(listOf()),
                isReply = true,
                isRepliedTo = true
            )
        }

    }
}