/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.model.status

import com.rainy.mastodroid.core.data.model.response.status.StatusContextResponse
import com.rainy.mastodroid.core.data.model.response.status.StatusResponse

data class StatusContext(
    val ancestors: List<Status>,
    val descendants: List<Status>
)

fun StatusContextResponse.toDomain(): StatusContext {
    return StatusContext(
        ancestors = ancestors?.mapNotNull(StatusResponse::toDomain) ?: listOf(),
        descendants = descendants?.mapNotNull(StatusResponse::toDomain) ?: listOf()
    )
}
