/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

abstract class BaseViewModel : ViewModel() {
    protected val loadingMutableState = MutableStateFlow(false)
    val loadingState = loadingMutableState.asStateFlow()

    protected suspend fun loadingTask(
        onStart: () -> Unit = {
            loadingMutableState.compareAndSet(
                expect = false,
                update = true
            )
        },
        onStop: () -> Unit = {
            loadingMutableState.compareAndSet(
                expect = true,
                update = false
            )
        },
        block: suspend () -> Unit,
    ) {
        try {
            onStart()
            block()
        } finally {
            onStop()
        }
    }

    fun <T> Flow<T>.stateIn(default: T) = stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000L),
        default
    )
}
