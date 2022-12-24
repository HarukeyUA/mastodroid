package com.rainy.mastodroid.core.data.model.response.mediaAttachment


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("gifv")
data class GifvAttachmentResponse(
    @SerialName("blurhash")
    val blurhash: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("id")
    val id: String?,
    @SerialName("meta")
    val meta: GifvMetaResponse?,
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
    data class GifvMetaResponse(
        @SerialName("aspect")
        val aspect: Float?,
        @SerialName("duration")
        val duration: Float?,
        @SerialName("fps")
        val fps: Int?,
        @SerialName("height")
        val height: Int?,
        @SerialName("length")
        val length: String?,
        @SerialName("original")
        val original: GifvOriginalMetaResponse?,
        @SerialName("size")
        val size: String?,
        @SerialName("small")
        val small: GivbSmallMetaResponse?,
        @SerialName("width")
        val width: Int?
    ) {
        @Serializable
        data class GifvOriginalMetaResponse(
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
        data class GivbSmallMetaResponse(
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
