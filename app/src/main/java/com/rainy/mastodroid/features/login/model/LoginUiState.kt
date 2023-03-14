/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.login.model

import androidx.compose.runtime.Immutable
import com.rainy.mastodroid.util.ErrorModel

@Immutable
data class LoginUiState(
    val isLoading: Boolean = false,
    val instanceLoginError: ErrorModel? = null
)
