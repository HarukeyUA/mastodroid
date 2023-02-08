package com.rainy.mastodroid.core.data.model.entity.status

import com.rainy.mastodroid.core.data.model.response.status.PreviewType
import com.rainy.mastodroid.core.domain.model.status.UrlPreviewCard

data class UrlPreviewCardEntity(
    val url: String,
    val title: String,
    val description: String,
    val type: PreviewType,
    val authorName: String,
    val authorUrl: String,
    val providerName: String,
    val providerUrl: String,
    val html: String,
    val width: Int,
    val height: Int,
    val thumbnail: String,
    val embedUrl: String,
    val blurhash: String
)

fun UrlPreviewCard.toUrlPreviewCardEntity(): UrlPreviewCardEntity {
    return UrlPreviewCardEntity(
        url = url,
        title = title,
        description = description,
        type = type,
        authorName = authorName,
        authorUrl = authorUrl,
        providerName = providerName,
        providerUrl = providerUrl,
        html = html,
        width = width,
        height = height,
        thumbnail = thumbnail,
        embedUrl = embedUrl,
        blurhash = blurhash
    )
}
