/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.ui.styledText

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.takeOrElse
import androidx.compose.ui.util.fastForEach
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rainy.mastodroid.R
import com.rainy.mastodroid.ui.elements.statusListItem.model.CustomEmojiItemModel
import com.rainy.mastodroid.util.ImmutableWrap

@Composable
fun textInlineCustomEmojis(
    emojis: ImmutableWrap<List<CustomEmojiItemModel>>,
    size: TextUnit = LocalTextStyle.current.fontSize
): Map<String, InlineTextContent> {
    val context = LocalContext.current
    val surfaceColor =
        MaterialTheme.colorScheme.surfaceColorAtElevation(LocalAbsoluteTonalElevation.current + 4.dp)
    val placeholder = remember {
        ContextCompat.getDrawable(context, R.drawable.custom_emoji_placeholder)?.apply {
            setTint(surfaceColor.toArgb())
        }
    }
    return buildMap {
        emojis.content.fastForEach { emoji ->
            put(
                emoji.shortcode, InlineTextContent(
                    placeholder = Placeholder(
                        width = size.takeOrElse { 14.sp },
                        height = size.takeOrElse { 14.sp },
                        PlaceholderVerticalAlign.TextCenter
                    ),
                    children = {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(emoji.url)
                                .placeholder(placeholder)
                                .crossfade(true)
                                .build(),
                            contentDescription = emoji.shortcode,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                ))
        }
    }
}
