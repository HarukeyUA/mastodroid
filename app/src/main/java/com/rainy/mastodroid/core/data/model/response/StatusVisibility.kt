/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.data.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class StatusVisibility {
    @SerialName("public")
    PUBLIC,
    @SerialName("unlisted")
    UNLISTED,
    @SerialName("private")
    PRIVATE,
    @SerialName("direct")
    DIRECT
}
