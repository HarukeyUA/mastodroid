package com.rainy.mastodroid.features.home.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.takeOrElse
import coil.compose.AsyncImage
import com.rainy.mastodroid.features.home.model.CustomEmojiItemModel
import com.rainy.mastodroid.ui.elements.ClickableText
import com.rainy.mastodroid.ui.styledText.MastodonContentTag

@Composable
fun StatusTextContent(
    text: AnnotatedString,
    customEmoji: List<CustomEmojiItemModel>,
    onUrlClicked: (url: String) -> Unit,
) {
    ClickableText(
        text = styleTextByAnnotations(text),
        style = MaterialTheme.typography.bodyMedium,
        modifier = Modifier.padding(8.dp),
        onClick = { offset ->
            text.getStringAnnotations(offset, offset).firstOrNull()
                ?.also { annotation ->
                    onUrlClicked(annotation.item)
                }
        },
        inlineContent = buildMap {
            customEmoji.forEach { emoji ->
                put(
                    emoji.shortcode, InlineTextContent(
                        placeholder = Placeholder(
                            width = LocalTextStyle.current.fontSize.takeOrElse { 14.sp },
                            height = LocalTextStyle.current.fontSize.takeOrElse { 14.sp },
                            PlaceholderVerticalAlign.TextCenter
                        ),
                        children = {
                            AsyncImage(
                                model = emoji.url,
                                contentDescription = emoji.shortcode,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    ))
            }
        }
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
