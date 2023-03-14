/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.ui.elements.statusListItem

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import com.rainy.mastodroid.ui.elements.statusListItem.model.CustomEmojiItemModel
import com.rainy.mastodroid.ui.elements.ClickableText
import com.rainy.mastodroid.ui.styledText.MastodonContentTag
import com.rainy.mastodroid.ui.styledText.textInlineCustomEmojis
import com.rainy.mastodroid.util.ImmutableWrap

@Composable
fun StatusTextContent(
    text: AnnotatedString,
    customEmoji: ImmutableWrap<List<CustomEmojiItemModel>>,
    modifier: Modifier = Modifier,
    onTextClicked: () -> Unit = {},
    onUrlClicked: (url: String) -> Unit
) {
    ClickableText(
        text = styleTextByAnnotations(text),
        style = MaterialTheme.typography.bodyMedium,
        modifier = modifier,
        onClick = { offset ->
            text.getStringAnnotations(offset, offset).firstOrNull()
                ?.also { annotation ->
                    onUrlClicked(annotation.item)
                } ?: run(onTextClicked)
        },
        inlineContent = textInlineCustomEmojis(emojis = customEmoji)
    )
}

@Composable
private fun styleTextByAnnotations(text: AnnotatedString) =
    buildAnnotatedString {
        append(text)
        val annotations =
            text.getStringAnnotations(0, text.length)
        annotations.forEach {
            when (it.tag) {
                MastodonContentTag.URL.toString() -> addStyle(
                    SpanStyle(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    ),
                    it.start,
                    it.end
                )

                MastodonContentTag.MENTION.toString(), MastodonContentTag.HASHTAG.toString() ->
                    addStyle(
                        SpanStyle(
                            color = MaterialTheme.colorScheme.primary
                        ),
                        it.start,
                        it.end
                    )
            }
        }
    }
