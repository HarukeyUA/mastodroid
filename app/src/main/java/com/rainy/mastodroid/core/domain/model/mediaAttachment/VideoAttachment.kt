package com.rainy.mastodroid.core.domain.model.mediaAttachment

import com.rainy.mastodroid.core.data.model.entity.status.MediaAttachmentEntity
import com.rainy.mastodroid.core.data.model.response.mediaAttachment.VideoAttachmentResponse
import com.rainy.mastodroid.util.loge

data class VideoAttachment(
    val id: String,
    val url: String,
    val previewUrl: String,
    val remoteUrl: String,
    val description: String,
    val blurHash: String,
    val previewAspect: Float?
) : MediaAttachment()

fun VideoAttachmentResponse.toDomain(): VideoAttachment? {
    if (id.isNullOrEmpty()) {
        loge("Illegal video attachment response data")
        return null
    }

    return VideoAttachment(
        id = id,
        url = url ?: "",
        previewUrl = previewUrl ?: "",
        remoteUrl = remoteUrl ?: "",
        description = description ?: "",
        blurHash = blurhash ?: "",
        previewAspect = meta?.small?.aspect
    )
}

fun MediaAttachmentEntity.VideoAttachmentEntity.toDomain(): VideoAttachment {
    return VideoAttachment(
        id = id,
        url = url,
        previewUrl = previewUrl,
        remoteUrl = remoteUrl,
        description = description,
        blurHash = blurHash,
        previewAspect = previewAspect
    )
}
