/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.response.status


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreviewCardResponse(
    @SerialName("url")
    val url: String?,
    @SerialName("title")
    val title: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("type")
    val type: PreviewType?,
    @SerialName("author_name")
    val authorName: String?,
    @SerialName("author_url")
    val authorUrl: String?,
    @SerialName("provider_name")
    val providerName: String?,
    @SerialName("provider_url")
    val providerUrl: String?,
    @SerialName("html")
    val html: String?,
    @SerialName("width")
    val width: Int?,
    @SerialName("height")
    val height: Int?,
    @SerialName("image") // Optional
    val thumbnail: String?,
    @SerialName("embed_url")
    val embedUrl: String?,
    @SerialName("blurhash")
    val blurhash: String?
)

@Serializable
enum class PreviewType {
    @SerialName("link")
    LINK,

    @SerialName("photo")
    PHOTO,

    @SerialName("video")
    VIDEO,

    @SerialName("rich")
    RICH
}
