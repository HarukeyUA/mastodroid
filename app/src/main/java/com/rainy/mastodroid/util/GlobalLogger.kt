/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.util

import android.util.Log
import com.rainy.mastodroid.BuildConfig

val LOGGING_ENABLED = BuildConfig.DEBUG

fun Any.loge(msg: String?) {
    if (LOGGING_ENABLED) {
        Log.e(TAG, msg ?: "null")
    }
}

fun Any.loge(msg: String?, throwable: Throwable?) {
    if (LOGGING_ENABLED) {
        Log.e(TAG, msg ?: "null", throwable ?: Throwable())
    }
}

fun Any.logd(msg: String?) {
    if (LOGGING_ENABLED) {
        Log.d(TAG, msg ?: "null")
    }
}

fun Any.logw(msg: String?) {
    if (LOGGING_ENABLED) {
        Log.w(TAG, msg ?: "null")
    }
}

fun Any.logi(msg: String?) {
    if (LOGGING_ENABLED) {
        Log.i(TAG, msg ?: "null")
    }
}

fun Any.logv(msg: String?) {
    if (LOGGING_ENABLED) {
        Log.v(TAG, msg ?: "null")
    }
}

fun Any.logwtf(msg: String?) {
    if (LOGGING_ENABLED) {
        Log.wtf(TAG, msg ?: "null")
    }
}

val <T : Any> T.TAG: String
    get() = this::class.java.simpleName.take(15)
