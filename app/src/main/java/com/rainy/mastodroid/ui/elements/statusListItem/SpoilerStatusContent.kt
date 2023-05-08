/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.ui.elements.statusListItem

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rainy.mastodroid.R
import com.rainy.mastodroid.ui.elements.statusListItem.model.CustomEmojiItemModel
import com.rainy.mastodroid.ui.styledText.textInlineCustomEmojis
import com.rainy.mastodroid.ui.theme.MastodroidTheme
import com.rainy.mastodroid.util.ColorSchemePreviews
import com.rainy.mastodroid.util.ImmutableWrap

fun Modifier.dashedBorder(
    color: Color,
    strokeWidth: Dp,
    cornerRadiusDp: Dp,
    dashLength: Dp,
    dashGap: Dp
) =
    composed(
        factory = {
            val density = LocalDensity.current
            val strokeWidthPx = density.run { strokeWidth.toPx() }
            val cornerRadiusPx = density.run { cornerRadiusDp.toPx() }
            val dashLengthPx = density.run { dashLength.toPx() }
            val dashGapPx = density.run { dashGap.toPx() }

            then(
                Modifier.drawWithCache {
                    onDrawBehind {
                        val stroke = Stroke(
                            width = strokeWidthPx,
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(
                                    dashLengthPx,
                                    dashGapPx
                                ), 0f
                            )
                        )

                        drawRoundRect(
                            color = color,
                            style = stroke,
                            cornerRadius = CornerRadius(cornerRadiusPx)
                        )
                    }
                }
            )
        }
    )

@Composable
fun SpoilerStatusContent(
    text: AnnotatedString,
    emojis: ImmutableWrap<List<CustomEmojiItemModel>>,
    isExpanded: Boolean,
    onExpandClicked: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Crossfade(
        targetState = isExpanded,
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize(),
        label = ""
    ) { expanded ->
        if (expanded) {
            content()
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.large)
                    .dashedBorder(MaterialTheme.colorScheme.primary, 2.dp, 16.dp, 8.dp, 4.dp)
                    .clickable(onClick = onExpandClicked)
                    .padding(4.dp)
            ) {
                Text(
                    text = text.ifEmpty { AnnotatedString(stringResource(R.string.sensitive_content_warning)) },
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    inlineContent = textInlineCustomEmojis(
                        emojis = emojis,
                        size = MaterialTheme.typography.bodyLarge.fontSize
                    )
                )
                Text(
                    text = stringResource(R.string.tap_to_reveal),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

    }
}

@ColorSchemePreviews
@Composable
private fun SpoilerStatusContentPreview() {
    MastodroidTheme {
        ElevatedCard {
            SpoilerStatusContent(
                text = AnnotatedString("Text spoiler warning"),
                isExpanded = false,
                onExpandClicked = {},
                content = {},
                emojis = ImmutableWrap(listOf())
            )
        }
    }
}