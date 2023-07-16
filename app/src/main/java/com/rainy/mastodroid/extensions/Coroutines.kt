/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.extensions

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

inline fun CoroutineScope.launchLoading(
    loadingStateFlow: MutableStateFlow<Boolean>,
    context: CoroutineContext = EmptyCoroutineContext,
    crossinline block: suspend CoroutineScope.() -> Unit
) {
    launch(context) {
        try {
            loadingStateFlow.compareAndSet(expect = false, update = true)
            block()
        } finally {
            loadingStateFlow.compareAndSet(expect = true, update = false)
        }
    }
}

inline fun coroutineExceptionHandler(crossinline handler: (Throwable) -> Unit) =
    CoroutineExceptionHandler { _, throwable ->
        handler(throwable)
    }