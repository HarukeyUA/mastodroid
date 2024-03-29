/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.model.user

import com.rainy.mastodroid.core.data.model.response.StatusVisibility
import com.rainy.mastodroid.core.data.model.response.user.AccountFieldResponse
import com.rainy.mastodroid.core.data.model.response.user.AccountSourceResponse

data class UserSource(
    val fields: List<UserField>,
    val followRequestsCount: Int,
    val language: String,
    val note: String,
    val privacy: StatusVisibility,
    val sensitive: Boolean
)

fun AccountSourceResponse.toDomain(): UserSource {
    return UserSource(
        fields = fields?.map(AccountFieldResponse::toDomain) ?: listOf(),
        followRequestsCount = followRequestsCount ?: 0,
        language = language ?: "",
        note = note ?: "",
        privacy = privacy ?: StatusVisibility.PUBLIC,
        sensitive = sensitive ?: true
    )
}
