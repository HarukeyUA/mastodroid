/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.util

import com.rainy.mastodroid.R
import com.rainy.mastodroid.core.data.model.response.ErrorResponse
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import java.net.HttpURLConnection

class NetworkExceptionIdentifier(
    private val json: Json
) {

    fun identifyException(throwable: Throwable): ErrorModel {
        loge("Encountered exception: $throwable", throwable = throwable)
        return when (throwable) {
            is HttpException -> {
                parseHttpException(throwable)
            }

            is IOException -> {
                ErrorModel.RemoteError.ServerCommunicationError()
            }

            is IllegalArgumentException -> {
                ErrorModel.RemoteError.ResponseParseError()
            }

            else -> {
                ErrorModel.RemoteError.UnknownError()
            }
        }
    }

    private fun parseHttpException(throwable: HttpException): ErrorModel {
        val errorBody = throwable.response()?.errorBody()?.string()?.let {
            runCatching { json.decodeFromString<ErrorResponse>(it) }.getOrNull()
        }
        return when (throwable.code()) {
            HttpURLConnection.HTTP_FORBIDDEN -> {
                ErrorModel.RemoteError.HttpError(
                    errorBody?.message,
                    errorBody?.messageDescription,
                    R.string.forbidden
                )
            }

            HttpURLConnection.HTTP_NOT_FOUND -> {
                ErrorModel.RemoteError.HttpError(
                    errorBody?.message,
                    errorBody?.messageDescription,
                    R.string.not_found
                )
            }

            HTTP_RATE_LIMIT -> {
                ErrorModel.RemoteError.HttpError(
                    errorBody?.message,
                    errorBody?.messageDescription,
                    R.string.rate_limit
                )
            }

            HttpURLConnection.HTTP_UNAUTHORIZED -> {
                ErrorModel.RemoteError.HttpError(
                    errorBody?.message,
                    errorBody?.messageDescription,
                    R.string.unauthorized_error
                )
            }

            in 400..499 -> {
                ErrorModel.RemoteError.HttpError(
                    errorBody?.message,
                    errorBody?.messageDescription,
                    R.string.bad_request
                )
            }

            else -> {
                ErrorModel.RemoteError.HttpError(
                    errorBody?.message,
                    errorBody?.messageDescription,
                    R.string.server_error
                )
            }
        }
    }

    companion object {
        private const val HTTP_RATE_LIMIT = 429
    }
}
