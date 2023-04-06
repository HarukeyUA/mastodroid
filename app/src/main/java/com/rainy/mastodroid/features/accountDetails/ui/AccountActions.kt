/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.accountDetails.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastSumBy
import com.rainy.mastodroid.R
import com.rainy.mastodroid.ui.theme.MastodroidTheme
import com.rainy.mastodroid.util.ColorSchemePreviews
import com.rainy.mastodroid.util.ImmutableWrap
import com.rainy.mastodroid.util.abbreviateCount

@Stable
data class AccountActionItem(
    val count: Long,
    @StringRes val label: Int,
    val action: () -> Unit
)

@Composable
fun AccountActions(
    statusesCount: Long,
    followingCount: Long,
    followersCount: Long,
    mainAction: @Composable () -> Unit,
    onStatusesClicked: () -> Unit,
    onFollowingClicked: () -> Unit,
    onFollowersClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.End)
        ) {
            mainAction()
        }
        Spacer(modifier = Modifier.size(8.dp))
        OutlinedCard(
            elevation = CardDefaults.outlinedCardElevation(
                defaultElevation = 1.dp
            ),
            modifier = Modifier
                .height(IntrinsicSize.Min)
        ) {
            AccountActionsRow(
                items = ImmutableWrap(
                    listOf(
                        AccountActionItem(
                            count = statusesCount,
                            label = R.string.posts,
                            action = onStatusesClicked
                        ),
                        AccountActionItem(
                            count = followingCount,
                            label = R.string.following,
                            action = onFollowingClicked
                        ),
                        AccountActionItem(
                            count = followersCount,
                            label = R.string.followers,
                            action = onFollowersClicked
                        )
                    )
                )
            )
        }
    }
}

private const val SEPARATOR_ID = "SEPARATOR"

@Composable
fun AccountActionsRow(
    items: ImmutableWrap<List<AccountActionItem>>,
    modifier: Modifier = Modifier
) {
    require(items.content.isNotEmpty())
    Layout(
        modifier = modifier,
        content = {
            items.content.fastForEachIndexed { index, accountActionItem ->
                AccountActionRowItem(
                    metrics = accountActionItem.count,
                    label = stringResource(id = accountActionItem.label),
                    modifier = Modifier
                        .clickable(onClick = accountActionItem.action)
                        .padding(vertical = 4.dp, horizontal = 4.dp)

                )
                if (index != items.content.size - 1) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight(0.3f)
                            .width(1.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                            .layoutId(SEPARATOR_ID)
                    )
                }

            }
        }) { measurables, constraints ->

        val groupedMeasurables = measurables.groupBy { it.layoutId == SEPARATOR_ID }

        val metricsElementFixedWidthConstraint =
            groupedMeasurables[false]!!.maxOfOrNull { it.maxIntrinsicWidth(constraints.maxHeight) }!!

        val metricsElementPlaceables = groupedMeasurables[false]!!.map {
            it.measure(
                constraints.copy(
                    minWidth = metricsElementFixedWidthConstraint,
                    maxWidth = metricsElementFixedWidthConstraint
                )
            )
        }
        val separatorsPlaceables = groupedMeasurables[true]!!.map { it.measure(constraints) }

        val totalHeight = metricsElementPlaceables.maxBy { it.height }.height
        val totalWidth =
            separatorsPlaceables.fastSumBy { it.width } + metricsElementPlaceables.fastSumBy { it.width }


        layout(totalWidth, totalHeight) {
            var lastX = 0
            val separatorIterator = separatorsPlaceables.iterator()
            metricsElementPlaceables.fastForEachIndexed { i, placeable ->
                placeable.placeRelative(
                    x = lastX,
                    y = 0,
                )
                lastX += placeable.width
                if (i != metricsElementPlaceables.size - 1) {
                    separatorIterator.next().apply {
                        placeRelative(
                            x = lastX,
                            y = (totalHeight / 2) - (height / 2)
                        )
                        lastX += width
                    }
                }
            }
        }

    }
}

@Composable
private fun AccountActionRowItem(
    metrics: Long,
    label: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = metrics.abbreviateCount(), style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            maxLines = 1
        )
    }
}

@ColorSchemePreviews
@Composable
private fun AccountActionsPreview() {
    MastodroidTheme {
        Surface {
            AccountActions(
                mainAction = {
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Follow")
                    }
                },
                statusesCount = 0,
                followingCount = 0,
                followersCount = 0,
                onStatusesClicked = {},
                onFollowingClicked = {},
                onFollowersClicked = {}
            )
        }
    }
}