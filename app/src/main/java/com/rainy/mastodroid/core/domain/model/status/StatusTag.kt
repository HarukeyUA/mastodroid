package com.rainy.mastodroid.core.domain.model.status

import com.rainy.mastodroid.core.data.model.response.status.StatusTagResponse

data class StatusTag(
    val name: String,
    val url: String
)

fun StatusTagResponse.toDomain(): StatusTag {
    return StatusTag(
        name = name ?: "",
        url = url ?: ""
    )
}
