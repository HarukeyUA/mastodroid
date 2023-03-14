/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.response.status


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusApplicationResponse(
    @SerialName("name")
    val name: String?,
    @SerialName("website") // Optional
    val website: String?
)
