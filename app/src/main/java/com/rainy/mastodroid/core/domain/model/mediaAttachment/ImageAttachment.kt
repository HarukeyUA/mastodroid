/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.model.mediaAttachment

import com.rainy.mastodroid.core.data.model.entity.status.MediaAttachmentEntity
import com.rainy.mastodroid.core.data.model.response.mediaAttachment.ImageAttachmentResponse
import com.rainy.mastodroid.util.loge

data class ImageAttachment(
    val id: String,
    val url: String,
    val previewUrl: String,
    val remoteUrl: String,
    val description: String,
    val blurHash: String,
    val width: Int?,
    val height: Int?,
    val aspect: Float?
): MediaAttachment()

fun ImageAttachmentResponse.toDomain(): ImageAttachment? {
    if (id.isNullOrEmpty()) {
        loge("Illegal image attachment response data")
        return null
    }

    return ImageAttachment(
        id = id,
        url = url ?: "",
        previewUrl = previewUrl ?: "",
        remoteUrl = remoteUrl ?: "",
        description = description ?: "",
        blurHash = blurHash ?: "",
        width = meta?.original?.width,
        height = meta?.original?.height,
        aspect = meta?.original?.aspect
    )
}

fun MediaAttachmentEntity.ImageAttachmentEntity.toDomain(): ImageAttachment {
    return ImageAttachment(
        id = id,
        url = url,
        previewUrl = previewUrl,
        remoteUrl = remoteUrl,
        description = description,
        blurHash = blurHash,
        width = width,
        height = height,
        aspect = aspect
    )
}
