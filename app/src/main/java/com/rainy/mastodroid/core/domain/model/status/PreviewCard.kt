package com.rainy.mastodroid.core.domain.model.status

import com.rainy.mastodroid.core.data.model.response.status.PreviewCardResponse
import com.rainy.mastodroid.core.data.model.response.status.PreviewType

data class PreviewCard(
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

fun PreviewCardResponse.toDomain(): PreviewCard {
    return PreviewCard(
        url = url ?: "",
        title = title ?: "",
        description = description ?: "",
        type = type ?: PreviewType.LINK,
        authorName = authorName ?: "",
        authorUrl = authorUrl ?: "",
        providerName = providerName ?: "",
        providerUrl = providerUrl ?: "",
        html = html ?: "",
        width = width ?: 0,
        height = height ?: 0,
        thumbnail = thumbnail ?: "",
        embedUrl = embedUrl ?: "",
        blurhash = blurhash ?: ""
    )
}
