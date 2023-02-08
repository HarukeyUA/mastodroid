package com.rainy.mastodroid.core.data.model.entity.status

import com.rainy.mastodroid.core.domain.model.status.StatusApplication

data class StatusApplicationEntity(
    val name: String,
    val website: String
)

fun StatusApplication.toStatusApplicationEntity(): StatusApplicationEntity {
    return StatusApplicationEntity(
        name = name,
        website = website
    )
}
