/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.network.errorHandling

import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.CallAdapter

class ErrorHandlingCallAdapter(
    private val delegateAdapter: CallAdapter<Any, Call<*>>,
    private val json: Json
) : CallAdapter<Any, Call<*>> by delegateAdapter {

    override fun adapt(call: Call<Any>): Call<*> {
        return delegateAdapter.adapt(ErrorHandlingCall(call, json))
    }
}