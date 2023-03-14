/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.webAuth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.navOptions
import com.rainy.mastodroid.R
import com.rainy.mastodroid.core.base.BaseViewModel
import com.rainy.mastodroid.core.data.remote.AuthRemoteDataSourceImpl.Companion.OAUTH_REDIRECT_URI
import com.rainy.mastodroid.core.data.remote.AuthRemoteDataSourceImpl.Companion.OAUTH_SCOPES
import com.rainy.mastodroid.core.domain.interactor.AuthInteractor
import com.rainy.mastodroid.core.navigation.RouteNavigator
import com.rainy.mastodroid.core.navigation.getOrThrow
import com.rainy.mastodroid.features.home.HomeRoute
import com.rainy.mastodroid.features.webAuth.WebAuthRoute.CLIENT_ID_ARG
import com.rainy.mastodroid.features.webAuth.WebAuthRoute.CLIENT_SECRET_ARG
import com.rainy.mastodroid.features.webAuth.WebAuthRoute.INSTANCE_HOST_ARG
import com.rainy.mastodroid.util.ErrorModel
import com.rainy.mastodroid.util.NetworkExceptionIdentifier
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.HttpUrl

class WebAuthViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val authInteractor: AuthInteractor,
    routeNavigator: RouteNavigator,
    errorIdentifier: NetworkExceptionIdentifier
) : BaseViewModel(), RouteNavigator by routeNavigator {

    private val errorState = MutableStateFlow<ErrorModel?>(null)
    val errorFlow = errorState.asStateFlow()

    private val defaultExceptionHandler: CoroutineExceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            errorState.value = errorIdentifier.identifyException(throwable)
        }

    fun getAuthUrl(): HttpUrl {
        val instanceHost = savedStateHandle.getOrThrow<String>(INSTANCE_HOST_ARG)
        val clientId = savedStateHandle.getOrThrow<String>(CLIENT_ID_ARG)

        return HttpUrl.Builder()
            .scheme("https")
            .host(instanceHost)
            .addPathSegments("oauth/authorize")
            .addQueryParameter("client_id", clientId)
            .addQueryParameter("redirect_uri", OAUTH_REDIRECT_URI)
            .addQueryParameter("response_type", "code")
            .addQueryParameter("scope", OAUTH_SCOPES)
            .build()
    }

    fun onCodeAcquired(code: String) {
        viewModelScope.launch(defaultExceptionHandler) {
            loadingTask {
                authInteractor.authenticateUser(
                    savedStateHandle.getOrThrow(INSTANCE_HOST_ARG),
                    savedStateHandle.getOrThrow(CLIENT_ID_ARG),
                    savedStateHandle.getOrThrow(CLIENT_SECRET_ARG),
                    code
                )
                navigateHome()
            }
        }

    }

    fun onAuthError(error: String) {
        errorState.value = if (error.isNotEmpty()) {
            ErrorModel.StringError(error)
        } else {
            ErrorModel.ResourceError(R.string.instance_auth_error)
        }
    }

    fun onWebViewError() {
        errorState.value = ErrorModel.ResourceError(R.string.instance_auth_page_load_error)
    }

    fun onErrorDialogDismiss() {
        performNavigation {
            navigateUp()
        }
    }

    private fun navigateHome() {
        performNavigation {
            navigate(
                HomeRoute.route,
                navOptions {
                    popUpTo(
                        this@performNavigation.graph.startDestinationId
                    ) {
                        inclusive = true
                    }
                }
            )
        }
    }
}
