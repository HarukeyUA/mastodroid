package com.rainy.mastodroid.core.data.model.response.mediaAttachment


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("audio")
data class AudioAttachmentResponse(
    @SerialName("description")
    val description: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("meta")
    val meta: AudioMetaResponse?,
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
    data class AudioMetaResponse(
        @SerialName("audio_bitrate")
        val audioBitrate: String?,
        @SerialName("audio_channels")
        val audioChannels: String?,
        @SerialName("audio_encode")
        val audioEncode: String?,
        @SerialName("duration")
        val duration: Float?,
        @SerialName("length")
        val length: String?,
        @SerialName("original")
        val original: AudioOriginalMetaResponse?
    ) {
        @Serializable
        data class AudioOriginalMetaResponse(
            @SerialName("bitrate")
            val bitrate: Int?,
            @SerialName("duration")
            val duration: Float?
        )
    }
}
