/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.model.user

import com.rainy.mastodroid.core.data.model.entity.status.StatusAccountUserFieldEntity
import com.rainy.mastodroid.core.data.model.response.user.AccountFieldResponse
import kotlinx.datetime.Instant

data class UserField(
    val name: String,
    val value: String,
    val verifiedAt: Instant?
)

fun AccountFieldResponse.toDomain(): UserField {
    return UserField(
        name = name ?: "",
        value = value ?: "",
        verifiedAt = verifiedAt
    )
}

fun StatusAccountUserFieldEntity.toDomain(): UserField {
    return UserField(
        name = name,
        value = value,
        verifiedAt = verifiedAt
    )
}
