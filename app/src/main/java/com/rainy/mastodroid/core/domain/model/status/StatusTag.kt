package com.rainy.mastodroid.core.domain.model.status

import com.rainy.mastodroid.core.data.model.entity.status.StatusTagEntity
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

fun StatusTagEntity.toDomain(): StatusTag {
    return StatusTag(
        name = name,
        url = url
    )
}