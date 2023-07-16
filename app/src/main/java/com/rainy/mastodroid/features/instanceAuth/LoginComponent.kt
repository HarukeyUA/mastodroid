/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.instanceAuth

import com.arkivanov.decompose.ComponentContext
import com.rainy.mastodroid.R
import com.rainy.mastodroid.core.domain.data.remote.AuthRemoteDataSource
import com.rainy.mastodroid.extensions.coroutineExceptionHandler
import com.rainy.mastodroid.extensions.coroutineScope
import com.rainy.mastodroid.extensions.launchLoading
import com.rainy.mastodroid.features.instanceAuth.model.InstanceAuthData
import com.rainy.mastodroid.features.instanceAuth.model.LoginUiState
import com.rainy.mastodroid.util.ErrorModel
import com.rainy.mastodroid.util.toErrorModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import okhttp3.HttpUrl

class LoginComponent(
    componentContext: ComponentContext,
    private val authRemoteDataSource: AuthRemoteDataSource,
    private val onHostAcquired: (InstanceAuthData) -> Unit
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()

    private val exceptionHandler =
        coroutineExceptionHandler { throwable ->
            loginState.update {
                it.copy(instanceLoginError = throwable.toErrorModel())
            }
        }

    private val loginState = MutableStateFlow(LoginUiState())
    private val loadingState = MutableStateFlow(false)
    val loginStateFlow = combine(loginState, loadingState) { loginState, loadingState ->
        loginState.copy(isLoading = loadingState)
    }

    fun onInstanceHostSubmit(instanceHostInput: String) {
        val sanitizedHost = sanitizeHostInput(instanceHostInput)
        if (isHostValid(sanitizedHost)) {
            authenticateApp(sanitizedHost)
        } else {
            showInvalidHostError()
        }

    }

    private fun authenticateApp(sanitizedHost: String) {
        coroutineScope.launchLoading(loadingState, exceptionHandler) {
            val appAuthData = authRemoteDataSource.authenticateApp(sanitizedHost)
            onHostAcquired(
                InstanceAuthData(
                    instanceHost = sanitizedHost,
                    clientId = appAuthData.clientId,
                    clientSecret = appAuthData.clientSecret
                )
            )
        }
    }

    private fun showInvalidHostError() {
        loginState.update {
            it.copy(instanceLoginError = ErrorModel.ResourceError(R.string.invalid_instance_address_error))
        }
    }

    private fun isHostValid(sanitizedHost: String): Boolean {
        return try {
            HttpUrl.Builder().host(sanitizedHost).scheme("https").build()
            true
        } catch (e: IllegalArgumentException) {
            false
        }
    }

    private fun sanitizeHostInput(domain: String): String {
        return domain.trim()
            .removePrefix("http://")
            .removePrefix("https://")
            .takeLastWhile { it != '@' }
    }


}