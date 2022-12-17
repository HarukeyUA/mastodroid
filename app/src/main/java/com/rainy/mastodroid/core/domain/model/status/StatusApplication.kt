package com.rainy.mastodroid.core.domain.model.status

import com.rainy.mastodroid.core.data.model.response.status.StatusApplicationResponse

data class StatusApplication(
    val name: String,
    val website: String
)

fun StatusApplicationResponse.toDomain(): StatusApplication {
    return StatusApplication(
        name = name ?: "",
        website = website ?: ""
    )
}
