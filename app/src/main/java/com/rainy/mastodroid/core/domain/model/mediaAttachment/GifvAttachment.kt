package com.rainy.mastodroid.core.domain.model.mediaAttachment

import com.rainy.mastodroid.core.data.model.response.mediaAttachment.GifvAttachmentResponse
import com.rainy.mastodroid.util.loge

data class GifvAttachment(
    val id: String,
    val url: String,
    val previewUrl: String,
    val remoteUrl: String,
    val description: String,
    val blurHash: String,
    val previewAspect: Float?
) : MediaAttachment()

fun GifvAttachmentResponse.toDomain(): GifvAttachment? {
    if (id.isNullOrEmpty()) {
        loge("Illegal gifv attachment response data")
        return null
    }

    return GifvAttachment(
        id = id,
        url = url ?: "",
        previewUrl = previewUrl ?: "",
        remoteUrl = remoteUrl ?: "",
        description = description ?: "",
        blurHash = blurhash ?: "",
        previewAspect = meta?.small?.aspect
    )
}
