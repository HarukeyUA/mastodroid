/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.util

import androidx.compose.runtime.Immutable
import java.util.Collections

@Immutable
@Suppress("DataClassPrivateConstructor")
data class StableList<T> private constructor(
    private val internalMap: List<T>
) : List<T> by internalMap {
    companion object {
        operator fun <T> invoke(list: List<T>): StableList<T> =
            StableList(internalMap = Collections.unmodifiableList(list))
    }
}