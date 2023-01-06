package com.rainy.mastodroid.ui.styledText

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.takeOrElse
import coil.compose.AsyncImage
import com.rainy.mastodroid.features.home.model.CustomEmojiItemModel

@Composable
fun textInlineCustomEmojis(emojis: List<CustomEmojiItemModel>): Map<String, InlineTextContent> {
    return buildMap {
        emojis.forEach { emoji ->
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
}
