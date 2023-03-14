/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CustomEmojiResponse(
    @SerialName("shortcode")
    val shortcode: String?,
    @SerialName("static_url")
    val staticUrl: String?,
    @SerialName("url")
    val url: String?,
    @SerialName("visible_in_picker")
    val visibleInPicker: Boolean?,
    @SerialName("category")
    val category: String?
)
