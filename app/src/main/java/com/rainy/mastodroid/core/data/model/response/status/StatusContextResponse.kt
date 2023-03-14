/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.response.status

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatusContextResponse(
    @SerialName("ancestors")
    val ancestors: List<StatusResponse>?,
    @SerialName("descendants")
    val descendants: List<StatusResponse>?
)
