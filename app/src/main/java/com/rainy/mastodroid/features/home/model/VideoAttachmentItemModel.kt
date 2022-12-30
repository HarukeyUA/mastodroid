package com.rainy.mastodroid.features.home.model

import com.rainy.mastodroid.core.domain.model.mediaAttachment.VideoAttachment

data class VideoAttachmentItemModel(
    val id: String,
    val url: String,
    val previewUrl: String,
    val remoteUrl: String,
    val description: String,
    val blurHash: String,
    val previewAspect: Float?,
    val currentlyPlaying: Boolean = false
): MediaAttachmentItemModel()

fun VideoAttachment.toItemModel(): VideoAttachmentItemModel {
    return VideoAttachmentItemModel(
        id = id,
        url = url,
        previewUrl = previewUrl,
        remoteUrl = remoteUrl,
        description = description,
        blurHash = blurHash,
        previewAspect = previewAspect
    )
}
