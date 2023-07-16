/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.instanceAuth.model

import androidx.compose.runtime.Immutable
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.rainy.mastodroid.util.ErrorModel

@Immutable
@Parcelize
data class LoginUiState(
    val isLoading: Boolean = false,
    val instanceLoginError: ErrorModel? = null
): Parcelable
