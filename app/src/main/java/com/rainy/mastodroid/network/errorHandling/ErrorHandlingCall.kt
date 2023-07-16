/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.network.errorHandling

import com.rainy.mastodroid.R
import com.rainy.mastodroid.core.data.model.response.ErrorResponse
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.HttpURLConnection

class ErrorHandlingCall(
    private val delegate: Call<Any>,
    private val json: Json
) : Call<Any> by delegate {

    override fun enqueue(callback: Callback<Any>) {
        delegate.enqueue(object : Callback<Any> {
            override fun onResponse(call: Call<Any>, response: Response<Any>) {
                if (response.isSuccessful) {
                    callback.onResponse(call, response)
                } else {
                    callback.onFailure(
                        call,
                        identifyException(HttpException(response))
                    )
                }
            }

            override fun onFailure(call: Call<Any>, t: Throwable) {
                callback.onFailure(call, identifyException(t))
            }

        })
    }

    override fun clone(): Call<Any> {
        return ErrorHandlingCall(delegate.clone(), json)
    }

    fun identifyException(throwable: Throwable): Throwable {
        return when (throwable) {
            is HttpException -> {
                parseHttpException(throwable)
            }

            is IOException -> {
                RemoteIOException(
                    cause = throwable,
                    userMessage = R.string.communication_error
                )
            }

            is SerializationException -> {
                RemoteIOException(
                    cause = throwable,
                    userMessage = R.string.parse_error
                )
            }

            else -> {
                RemoteIOException(
                    cause = throwable,
                    userMessage = R.string.unknown_error
                )
            }
        }
    }

    private fun parseHttpException(throwable: HttpException): RemoteHttpException {
        val errorBody = throwable.response()?.errorBody()?.string()?.let {
            runCatching { json.decodeFromString<ErrorResponse>(it) }.getOrNull()
        }
        return when (throwable.code()) {
            HttpURLConnection.HTTP_FORBIDDEN -> {
                RemoteHttpException(
                    cause = throwable,
                    code = throwable.code(),
                    remoteMessage = errorBody?.message,
                    remoteDescription = errorBody?.messageDescription,
                    userMessage = R.string.forbidden
                )
            }

            HttpURLConnection.HTTP_NOT_FOUND -> {
                RemoteHttpException(
                    cause = throwable,
                    code = throwable.code(),
                    remoteMessage = errorBody?.message,
                    remoteDescription = errorBody?.messageDescription,
                    userMessage = R.string.not_found
                )
            }

            HTTP_RATE_LIMIT -> {
                RemoteHttpException(
                    cause = throwable,
                    code = throwable.code(),
                    remoteMessage = errorBody?.message,
                    remoteDescription = errorBody?.messageDescription,
                    userMessage = R.string.rate_limit
                )
            }

            HttpURLConnection.HTTP_UNAUTHORIZED -> {
                RemoteHttpException(
                    cause = throwable,
                    code = throwable.code(),
                    remoteMessage = errorBody?.message,
                    remoteDescription = errorBody?.messageDescription,
                    userMessage = R.string.unauthorized_error
                )
            }

            in 400..499 -> {
                RemoteHttpException(
                    cause = throwable,
                    code = throwable.code(),
                    remoteMessage = errorBody?.message,
                    remoteDescription = errorBody?.messageDescription,
                    userMessage = R.string.bad_request
                )
            }

            else -> {
                RemoteHttpException(
                    cause = throwable,
                    code = throwable.code(),
                    remoteMessage = errorBody?.message,
                    remoteDescription = errorBody?.messageDescription,
                    userMessage = R.string.server_error
                )
            }
        }
    }

    companion object {
        private const val HTTP_RATE_LIMIT = 429
    }
}