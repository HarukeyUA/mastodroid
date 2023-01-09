package com.rainy.mastodroid.extensions

import androidx.compose.ui.Modifier

inline fun <T : Any> Modifier.ifNotNull(value: T?, builder: Modifier.(T) -> Modifier): Modifier =
    then(if (value != null) builder(value) else Modifier)

inline fun Modifier.ifTrue(value: Boolean, builder: Modifier.() -> Modifier): Modifier =
    then(if (value) builder() else Modifier)
