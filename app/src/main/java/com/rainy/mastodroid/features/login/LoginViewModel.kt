/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.login

import androidx.lifecycle.viewModelScope
import com.rainy.mastodroid.R
import com.rainy.mastodroid.core.base.BaseViewModel
import com.rainy.mastodroid.core.data.remote.AuthRemoteDataSourceImpl
import com.rainy.mastodroid.core.domain.model.auth.AppAuthCredentials
import com.rainy.mastodroid.core.navigation.RouteNavigator
import com.rainy.mastodroid.features.login.model.LoginUiState
import com.rainy.mastodroid.features.webAuth.WebAuthRoute
import com.rainy.mastodroid.util.ErrorModel
import com.rainy.mastodroid.util.NetworkExceptionIdentifier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.HttpUrl

class LoginViewModel(
    private val authRemoteDataSourceImpl: AuthRemoteDataSourceImpl,
    errorIdentifier: NetworkExceptionIdentifier,
    routeNavigator: RouteNavigator
) : BaseViewModel(), RouteNavigator by routeNavigator {

    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            val error = errorIdentifier.identifyException(throwable)
            loginState.update {
                it.copy(instanceLoginError = error)
            }
        }

    private val loginState = MutableStateFlow(LoginUiState())
    val loginStateFlow = combine(loginState, loadingState) { loginState, loadingState ->
        loginState.copy(isLoading = loadingState)
    }.stateIn(LoginUiState())

    fun onInstanceHostSubmit(instanceHostInput: String) {
        val sanitizedHost = sanitizeHostInput(instanceHostInput)
        if (isHostValid(sanitizedHost)) {
            authenticateApp(sanitizedHost)
        } else {
            showInvalidHostError()
        }

    }

    private fun showInvalidHostError() {
        loginState.update {
            it.copy(instanceLoginError = ErrorModel.ResourceError(R.string.invalid_instance_address_error))
        }
    }

    private fun authenticateApp(sanitizedHost: String) {
        viewModelScope.launch(exceptionHandler) {
            loadingTask {
                val appAuthData = authRemoteDataSourceImpl.authenticateApp(sanitizedHost)
                navigateToWebAuth(sanitizedHost, appAuthData)
            }
        }
    }

    private fun navigateToWebAuth(
        sanitizedHost: String,
        appAuthData: AppAuthCredentials
    ) {
        performNavigation {
            navigate(
                WebAuthRoute.getRoute(
                    sanitizedHost,
                    appAuthData.clientId,
                    appAuthData.clientSecret
                )
            )
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
