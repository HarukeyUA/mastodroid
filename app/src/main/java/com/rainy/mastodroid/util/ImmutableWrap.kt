/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.util

import androidx.compose.runtime.Immutable

@Immutable
data class ImmutableWrap<T>(
    val content: T
)
