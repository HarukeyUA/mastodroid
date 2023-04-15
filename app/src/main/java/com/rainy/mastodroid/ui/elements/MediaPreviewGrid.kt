/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.compose.ui.util.fastMap
import com.rainy.mastodroid.ui.theme.MastodroidTheme

private const val MAX_PREVIEWS_NUM = 4

@Composable
fun MediaPreviewGrid(
    modifier: Modifier = Modifier,
    groupHeight: Dp = 350.dp,
    gap: Dp = 4.dp,
    content: @Composable () -> Unit,
) {
    Layout(
        content = content,
        modifier = modifier
    ) { measurables, constraints ->
        var height = if (measurables.size > 1) groupHeight.roundToPx() else constraints.maxHeight
        val placeables = when (measurables.size) {
            1 -> listOf(
                measurables.first()
                    .measure(constraints).also {
                        height = it.height
                    }
            )

            2 -> measurables.fastMap {
                it.measure(
                    Constraints.fixed(
                        (constraints.maxWidth / 2) - (gap.roundToPx() / 2),
                        height
                    )
                )
            }


            3 -> {
                var mediaIndex = 0
                measurables.fastMap { measurable ->
                    val mesaurable = if (mediaIndex == 0) {
                        measurable.measure(
                            Constraints.fixed(
                                (constraints.maxWidth / 2) - (gap.roundToPx() / 2),
                                height
                            )
                        )
                    } else {
                        measurable.measure(
                            Constraints.fixed(
                                (constraints.maxWidth / 2) - (gap.roundToPx() / 2),
                                (height / 2) - (gap.roundToPx() / 2)
                            )
                        )
                    }
                    mediaIndex++
                    mesaurable
                }
            }

            else -> measurables.take(MAX_PREVIEWS_NUM).fastMap {
                it.measure(
                    Constraints.fixed(
                        (constraints.maxWidth / 2) - (gap.roundToPx() / 2),
                        (height / 2) - (gap.roundToPx() / 2)
                    )
                )
            }
        }

        layout(constraints.maxWidth, height) {
            when (placeables.size) {
                1 -> placeables.first().placeRelative(0, 0)
                2 -> {
                    placeables.first().placeRelative(0, 0)
                    placeables[1].placeRelative(placeables.first().width + gap.roundToPx(), 0)
                }

                3 -> {
                    placeables.first().placeRelative(0, 0)
                    placeables[1].placeRelative(placeables.first().width + gap.roundToPx(), 0)
                    placeables[2].placeRelative(
                        placeables.first().width + gap.roundToPx(),
                        placeables[1].height + gap.roundToPx()
                    )
                }

                4 -> {
                    val columnY = Array(2) { 0 }
                    placeables.fastForEachIndexed { index, placeable ->
                        val column = index % 2
                        placeable.placeRelative(
                            x = (column * placeable.width).plus(if (column != 0) gap.roundToPx() else 0),
                            y = columnY[column]
                        )
                        columnY[column] += placeable.height + gap.roundToPx()
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun MediaPreviewGridItemPreview() {
    MastodroidTheme() {
        MediaPreviewGrid(
            modifier = Modifier
                .height(300.dp)
                .width(300.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(115.dp)
                    .background(Color.Black)
            )
        }

    }
}

@Composable
@Preview(showBackground = true)
private fun MediaPreviewGrid2ItemsPreview() {
    MastodroidTheme() {
        MediaPreviewGrid(modifier = Modifier.height(300.dp)) {
            Box(
                modifier = Modifier
                    .size(115.dp)
                    .background(Color.Black)
            )
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.Cyan)
            )

        }

    }
}

@Composable
@Preview(showBackground = true)
private fun MediaPreviewGrid3ItemsPreview() {
    MastodroidTheme() {
        MediaPreviewGrid(modifier = Modifier.height(300.dp)) {
            Box(
                modifier = Modifier
                    .size(115.dp)
                    .background(Color.Black)
            )
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.Cyan)
            )

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.Cyan)
            )

        }

    }
}

@Composable
@Preview(showBackground = true)
private fun MediaPreviewGrid4ItemsPreview() {
    MastodroidTheme() {
        MediaPreviewGrid(modifier = Modifier.height(300.dp)) {
            Box(
                modifier = Modifier
                    .size(115.dp)
                    .background(Color.Black)
            )
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.Cyan)
            )

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.Cyan)
            )

            Box(
                modifier = Modifier
                    .size(120.dp)
                    .background(Color.Cyan)
            )


        }

    }
}
