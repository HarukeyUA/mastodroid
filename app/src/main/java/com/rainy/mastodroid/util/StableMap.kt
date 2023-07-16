/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.util

import androidx.compose.runtime.Immutable
import java.util.Collections

@Immutable
@Suppress("DataClassPrivateConstructor")
data class StableMap<K, out V> private constructor(
    private val internalMap: Map<K, V>
) : Map<K, V> by internalMap {
    companion object {
        operator fun <K, V> invoke(map: Map<K, V>): StableMap<K, V> =
            StableMap(internalMap = Collections.unmodifiableMap(map))
    }
}