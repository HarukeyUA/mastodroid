/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.response.status

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusTagResponse(
    @SerialName("name")
    val name: String?,
    @SerialName("url")
    val url: String?
)
