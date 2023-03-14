/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

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
