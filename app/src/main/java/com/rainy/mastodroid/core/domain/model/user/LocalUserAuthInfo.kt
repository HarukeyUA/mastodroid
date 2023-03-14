/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.domain.model.user

data class LocalUserAuthInfo(
    val authToken: String,
    val instanceHost: String
)
