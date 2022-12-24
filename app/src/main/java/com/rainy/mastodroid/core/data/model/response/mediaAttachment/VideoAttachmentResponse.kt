package com.rainy.mastodroid.core.data.model.response.mediaAttachment

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("video")
data class VideoAttachmentResponse(
    @SerialName("blurhash")
    val blurhash: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("meta")
    val meta: VideoMetaResponse?,
    @SerialName("preview_url")
    val previewUrl: String?,
    @SerialName("remote_url")
    val remoteUrl: String?,
    @SerialName("type")
    val type: String?,
    @SerialName("url")
    val url: String?
): MediaAttachmentResponse() {
    @Serializable
    data class VideoMetaResponse(
        @SerialName("aspect")
        val aspect: Float?,
        @SerialName("audio_bitrate")
        val audioBitrate: String?,
        @SerialName("audio_channels")
        val audioChannels: String?,
        @SerialName("audio_encode")
        val audioEncode: String?,
        @SerialName("duration")
        val duration: Float?,
        @SerialName("fps")
        val fps: Int?,
        @SerialName("height")
        val height: Int?,
        @SerialName("length")
        val length: String?,
        @SerialName("original")
        val original: VideoOriginalMetaResponse?,
        @SerialName("size")
        val size: String?,
        @SerialName("small")
        val small: VideoSmallMetaResponse?,
        @SerialName("width")
        val width: Int?
    ) {
        @Serializable
        data class VideoOriginalMetaResponse(
            @SerialName("bitrate")
            val bitrate: Int?,
            @SerialName("duration")
            val duration: Float?,
            @SerialName("frame_rate")
            val frameRate: String?,
            @SerialName("height")
            val height: Int?,
            @SerialName("width")
            val width: Int?
        )

        @Serializable
        data class VideoSmallMetaResponse(
            @SerialName("aspect")
            val aspect: Float?,
            @SerialName("height")
            val height: Int?,
            @SerialName("size")
            val size: String?,
            @SerialName("width")
            val width: Int?
        )
    }
}
