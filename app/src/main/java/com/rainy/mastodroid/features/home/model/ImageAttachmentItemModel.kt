package com.rainy.mastodroid.features.home.model

import androidx.compose.runtime.Stable
import com.rainy.mastodroid.core.domain.model.mediaAttachment.ImageAttachment

@Stable
data class ImageAttachmentItemModel(
    val id: String,
    val url: String,
    val previewUrl: String,
    val remoteUrl: String,
    val description: String,
    val blurHash: String,
    val width: Int?,
    val height: Int?,
    val aspect: Float?
): MediaAttachmentItemModel()

fun ImageAttachment.toItemModel(): ImageAttachmentItemModel {
    return ImageAttachmentItemModel(
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
