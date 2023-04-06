/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.model.user

import com.rainy.mastodroid.core.data.model.response.FeaturedTagResponse
import kotlinx.datetime.LocalDate
import java.util.UUID

data class FeaturedTag(
    val id: String,
    val name: String,
    val url: String,
    val statusesCount: Long,
    val lastAt: LocalDate?
)

fun FeaturedTagResponse.toDomain(): FeaturedTag {
    return FeaturedTag(
        id = id ?: UUID.randomUUID().mostSignificantBits.toString(),
        name = name ?: "",
        url = url ?: "",
        statusesCount = statusesCount ?: 0,
        lastAt = lastAt
    )
}
