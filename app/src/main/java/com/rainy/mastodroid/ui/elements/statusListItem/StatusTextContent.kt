/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.ui.elements.statusListItem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.util.fastForEach
import com.rainy.mastodroid.ui.elements.ClickableText
import com.rainy.mastodroid.ui.elements.statusListItem.model.CustomEmojiItemModel
import com.rainy.mastodroid.ui.styledText.MastodonContentTag
import com.rainy.mastodroid.ui.styledText.textInlineCustomEmojis
import com.rainy.mastodroid.util.ImmutableWrap
import com.rainy.mastodroid.util.StableMap

@Composable
fun StatusTextContent(
    text: AnnotatedString,
    customEmoji: ImmutableWrap<List<CustomEmojiItemModel>>,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyMedium,
    onUrlClicked: (url: String) -> Unit = {},
    pointerInput: (suspend PointerInputScope.() -> Unit)? = null
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val styledText = remember(text, primaryColor) {
        styleTextByAnnotations(text, primaryColor)
    }
    ClickableText(
        text = styledText,
        style = style,
        modifier = modifier,
        onClick = { annotation ->
            onUrlClicked(annotation.item)
        },
        inlineContent = StableMap(textInlineCustomEmojis(emojis = customEmoji)),
        pointerInput = pointerInput
    )
}


private fun styleTextByAnnotations(text: AnnotatedString, primaryColor: Color) =
    buildAnnotatedString {
        append(text)
        val annotations =
            text.getStringAnnotations(0, text.length)
        annotations.fastForEach {
            when (it.tag) {
                MastodonContentTag.URL.toString() -> addStyle(
                    SpanStyle(
                        color = primaryColor,
                        textDecoration = TextDecoration.Underline
                    ),
                    it.start,
                    it.end
                )

                MastodonContentTag.MENTION.toString(), MastodonContentTag.HASHTAG.toString() ->
                    addStyle(
                        SpanStyle(
                            color = primaryColor
                        ),
                        it.start,
                        it.end
                    )
            }
        }
    }
