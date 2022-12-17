package com.rainy.mastodroid.util

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.rainy.mastodroid.R

sealed class ErrorModel {
    sealed class RemoteError(@StringRes val genericMessage: Int) : ErrorModel() {
        class HttpError(
            val serverError: String?,
            val serverErrorDescription: String?,
            @StringRes userMessage: Int
        ) : RemoteError(userMessage)

        class ResponseParseError(@StringRes userMessage: Int = R.string.parse_error) :
            RemoteError(userMessage)

        class ServerCommunicationError(@StringRes userMessage: Int = R.string.communication_error) :
            RemoteError(userMessage)

        class UnknownError(@StringRes userMessage: Int = R.string.unknown_error) :
            RemoteError(userMessage)
    }

    class ResourceError(val userMessage: Int) : ErrorModel()
    class StringError(val userMessage: String) : ErrorModel()

    fun resolveText(context: Context): String {
        return when (this) {
            is ResourceError -> context.getString(userMessage)
            is StringError -> userMessage
            is RemoteError.HttpError -> serverErrorDescription ?: context.getString(genericMessage)
            is RemoteError -> context.getString(genericMessage)

        }
    }

    @Composable
    fun resolveText(): String {
        val context = LocalContext.current
        return resolveText(context)
    }
}
