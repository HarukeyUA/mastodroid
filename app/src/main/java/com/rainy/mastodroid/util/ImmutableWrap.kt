package com.rainy.mastodroid.util

import androidx.compose.runtime.Immutable

@Immutable
data class ImmutableWrap<T>(
    val content: T
)
