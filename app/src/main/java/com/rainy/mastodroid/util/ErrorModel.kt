/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.rainy.mastodroid.R
import com.rainy.mastodroid.network.errorHandling.RemoteHttpException
import com.rainy.mastodroid.network.errorHandling.RemoteIOException

@Parcelize
sealed class ErrorModel : Parcelable {

    class HttpError(
        val serverError: String?,
        val serverErrorDescription: String?,
        @StringRes val userMessage: Int
    ) : ErrorModel()

    class ResourceError(val userMessage: Int) : ErrorModel()
    class StringError(val userMessage: String) : ErrorModel()

    fun resolveText(context: Context): String {
        return when (this) {
            is ResourceError -> context.getString(userMessage)
            is StringError -> userMessage
            is HttpError -> if (!serverErrorDescription.isNullOrEmpty()) {
                context.getString(
                    R.string.http_error,
                    context.getString(userMessage),
                    serverErrorDescription
                )
            } else {
                context.getString(userMessage)
            }
        }
    }

    @Composable
    fun resolveText(): String {
        val context = LocalContext.current
        return resolveText(context)
    }
}

fun RemoteHttpException.toErrorModel(): ErrorModel {
    return ErrorModel.HttpError(
        serverError = remoteMessage,
        serverErrorDescription = remoteDescription,
        userMessage = userMessage
    )
}

fun RemoteIOException.toErrorModel(): ErrorModel {
    return ErrorModel.ResourceError(
        userMessage = userMessage
    )
}

fun Throwable.toErrorModel(): ErrorModel {
    return when (this) {
        is RemoteHttpException -> toErrorModel()
        is RemoteIOException -> toErrorModel()
        else -> localizedMessage?.let {
            ErrorModel.StringError(it)
        } ?: ErrorModel.ResourceError(R.string.unknown_error)

    }
}
