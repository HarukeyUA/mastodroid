/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.response.mediaAttachment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("image")
data class ImageAttachmentResponse(
    @SerialName("id")
    val id: String?,
    @SerialName("url")
    val url: String?,
    @SerialName("preview_url")
    val previewUrl: String?,
    @SerialName("remote_url")
    val remoteUrl: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("blurhash")
    val blurHash: String?,
    @SerialName("meta")
    val meta: ImageMetaResponse?
) : MediaAttachmentResponse() {
    @Serializable
    data class ImageMetaResponse(
        @SerialName("original")
        val original: ImageSizeMetaResponse?,
        @SerialName("small")
        val small: ImageSizeMetaResponse?,
        @SerialName("focus")
        val focus: ImageFocusMetaResponse?
    )

    @Serializable
    data class ImageSizeMetaResponse(
        @SerialName("width")
        val width: Int?,
        @SerialName("height")
        val height: Int?,
        @SerialName("size")
        val size: String?,
        @SerialName("aspect")
        val aspect: Float?
    )

    @Serializable
    data class ImageFocusMetaResponse(
        @SerialName("x")
        val x: Float?,
        @SerialName("y")
        val y: Float?
    )
}
