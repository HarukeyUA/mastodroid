package com.rainy.mastodroid.features.home.model

import androidx.compose.runtime.Stable
import com.rainy.mastodroid.core.domain.model.user.CustomEmoji

@Stable
data class CustomEmojiItemModel(
    val shortcode: String,
    val url: String
)

fun CustomEmoji.toItemModel(): CustomEmojiItemModel {
    return CustomEmojiItemModel(
        shortcode, url
    )
}
