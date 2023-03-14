/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.entity.status

import com.rainy.mastodroid.core.domain.model.status.StatusTag
import kotlinx.serialization.Serializable

@Serializable
data class StatusTagEntity(
    val name: String,
    val url: String
)

fun StatusTag.toStatusTagEntity(): StatusTagEntity {
    return StatusTagEntity(
        name = name,
        url = url
    )
}
