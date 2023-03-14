/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.model.status

import com.rainy.mastodroid.core.data.model.entity.status.StatusApplicationEntity
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

fun StatusApplicationEntity.toDomain(): StatusApplication {
    return StatusApplication(
        name = name,
        website = website
    )
}
