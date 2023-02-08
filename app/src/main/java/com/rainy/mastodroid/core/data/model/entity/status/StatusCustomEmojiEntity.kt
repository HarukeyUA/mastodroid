package com.rainy.mastodroid.core.data.model.entity.status

import com.rainy.mastodroid.core.domain.model.user.CustomEmoji
import kotlinx.serialization.Serializable

@Serializable
data class StatusCustomEmojiEntity(
    val shortcode: String,
    val staticUrl: String,
    val url: String,
    val visibleInPicker: Boolean,
    val category: String
)

fun CustomEmoji.toStatusCustomEmojiEntity(): StatusCustomEmojiEntity {
    return StatusCustomEmojiEntity(
        shortcode = shortcode,
        staticUrl = staticUrl,
        url = url,
        visibleInPicker = visibleInPicker,
        category = category
    )
}