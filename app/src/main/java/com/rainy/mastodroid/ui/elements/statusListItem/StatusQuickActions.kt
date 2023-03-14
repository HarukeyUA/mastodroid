/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.ui.elements.statusListItem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.rainy.mastodroid.R
import com.rainy.mastodroid.ui.elements.ResizableIconButton
import com.rainy.mastodroid.ui.elements.ResizableIconToggleButton
import com.rainy.mastodroid.ui.theme.MastodroidTheme
import com.rainy.mastodroid.util.ColorSchemePreviews
import java.text.DecimalFormat

@Composable
fun StatusQuickActions(
    favorites: Int,
    isFavorite: Boolean,
    onFavoriteClicked: (Boolean) -> Unit,
    reblogs: Int,
    isRebloged: Boolean,
    onReblogClicked: (Boolean) -> Unit,
    replies: Int,
    onReplyClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
        Box(contentAlignment = Alignment.CenterEnd, modifier = modifier) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                ResizableIconButton(
                    onClick = onReplyClicked,
                    modifier = Modifier
                        .height(48.dp)
                        .widthIn(min = 48.dp)
                ) {
                    Row {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_reply),
                            contentDescription = null
                        )
                        Text(
                            text = abbreviateCount(replies),
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            maxLines = 1,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                ResizableIconToggleButton(
                    checked = isRebloged,
                    onCheckedChange = onReblogClicked,
                    modifier = Modifier
                        .height(48.dp)
                        .widthIn(min = 48.dp)
                ) {
                    Row {
                        if (isRebloged) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_repeat_emphasis),
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_repeat),
                                contentDescription = null
                            )
                        }
                        Text(
                            text = abbreviateCount(reblogs),
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            maxLines = 1,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                ResizableIconToggleButton(
                    checked = isFavorite,
                    onCheckedChange = onFavoriteClicked,
                    modifier = Modifier
                        .height(48.dp)
                        .widthIn(min = 48.dp)
                ) {
                    Row {
                        if (isFavorite) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_star_filled),
                                contentDescription = null
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_star),
                                contentDescription = null
                            )
                        }
                        Text(
                            text = abbreviateCount(favorites),
                            modifier = Modifier
                                .align(Alignment.CenterVertically),
                            maxLines = 1,
                            textAlign = TextAlign.Start,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }
    }

}

private const val MILLION = 1_000_000
private const val THOUSAND = 1_000

@Composable
private fun abbreviateCount(favorites: Int) = when {
    favorites >= MILLION -> {
        stringResource(
            id = R.string.count_millions,
            DecimalFormat("#.#").format(favorites / MILLION.toFloat())
        )
    }

    favorites >= THOUSAND -> {
        stringResource(
            id = R.string.count_thousands,
            DecimalFormat("#.#").format(favorites / THOUSAND.toFloat())
        )
    }

    else -> favorites.toString()
}

@ColorSchemePreviews
@Composable
private fun StatusQuickActionsPreview() {
    MastodroidTheme {
        ElevatedCard {
            StatusQuickActions(
                favorites = 3743,
                isFavorite = false,
                onFavoriteClicked = {},
                reblogs = 0,
                isRebloged = false,
                onReblogClicked = {},
                replies = 1264,
                onReplyClicked = {}
            )
        }
    }
}

@ColorSchemePreviews
@Composable
private fun StatusQuickActionsPreviewActive() {
    MastodroidTheme {
        ElevatedCard {
            StatusQuickActions(
                favorites = 0,
                isFavorite = true,
                onFavoriteClicked = {},
                reblogs = 0,
                isRebloged = true,
                onReblogClicked = {},
                replies = 0,
                onReplyClicked = {}
            )
        }
    }
}