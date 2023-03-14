/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.entity.status

import com.rainy.mastodroid.core.domain.model.mediaAttachment.GifvAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.ImageAttachment
import com.rainy.mastodroid.core.domain.model.mediaAttachment.VideoAttachment
import kotlinx.serialization.Serializable

@Serializable
sealed class MediaAttachmentEntity {

    @Serializable
    data class GifvAttachmentEntity(
        val id: String,
        val url: String,
        val previewUrl: String,
        val remoteUrl: String,
        val description: String,
        val blurHash: String,
        val previewAspect: Float?
    ) : MediaAttachmentEntity()

    @Serializable
    data class ImageAttachmentEntity(
        val id: String,
        val url: String,
        val previewUrl: String,
        val remoteUrl: String,
        val description: String,
        val blurHash: String,
        val width: Int?,
        val height: Int?,
        val aspect: Float?
    ) : MediaAttachmentEntity()

    @Serializable
    data class VideoAttachmentEntity(
        val id: String,
        val url: String,
        val previewUrl: String,
        val remoteUrl: String,
        val description: String,
        val blurHash: String,
        val previewAspect: Float?
    ) : MediaAttachmentEntity()
}

fun GifvAttachment.toGifvAttachmentEntity(): MediaAttachmentEntity.GifvAttachmentEntity {
    return MediaAttachmentEntity.GifvAttachmentEntity(
        id = id,
        url = url,
        previewUrl = previewUrl,
        remoteUrl = remoteUrl,
        description = description,
        blurHash = blurHash,
        previewAspect = previewAspect
    )
}

fun ImageAttachment.toImageAttachmentEntity(): MediaAttachmentEntity.ImageAttachmentEntity {
    return MediaAttachmentEntity.ImageAttachmentEntity(
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

fun VideoAttachment.toVideoAttachmentEntity(): MediaAttachmentEntity.VideoAttachmentEntity {
    return MediaAttachmentEntity.VideoAttachmentEntity(
        id = id,
        url = url,
        previewUrl = previewUrl,
        remoteUrl = remoteUrl,
        description = description,
        blurHash = blurHash,
        previewAspect = previewAspect
    )
}
