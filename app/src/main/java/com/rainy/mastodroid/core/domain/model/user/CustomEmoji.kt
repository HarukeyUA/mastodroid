/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.model.user

import com.rainy.mastodroid.core.data.model.entity.status.StatusCustomEmojiEntity
import com.rainy.mastodroid.core.data.model.response.CustomEmojiResponse
import com.rainy.mastodroid.util.loge

data class CustomEmoji(
    val shortcode: String,
    val staticUrl: String,
    val url: String,
    val visibleInPicker: Boolean,
    val category: String
)

fun CustomEmojiResponse.toDomain(): CustomEmoji? {
    if (shortcode == null) {
        loge("Emoji shortcode is null")
        return null
    }

    return CustomEmoji(
        shortcode = shortcode,
        staticUrl = staticUrl ?: "",
        url = url ?: "",
        visibleInPicker = visibleInPicker ?: false,
        category = category ?: ""
    )
}

fun StatusCustomEmojiEntity.toDomain(): CustomEmoji {
    return CustomEmoji(
        shortcode = shortcode,
        staticUrl = staticUrl,
        url = url,
        visibleInPicker = visibleInPicker,
        category = category
    )
}
