/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.webAuth

import com.arkivanov.decompose.ComponentContext
import com.rainy.mastodroid.R
import com.rainy.mastodroid.core.data.remote.AuthRemoteDataSourceImpl
import com.rainy.mastodroid.core.domain.interactor.AuthInteractor
import com.rainy.mastodroid.extensions.coroutineExceptionHandler
import com.rainy.mastodroid.extensions.coroutineScope
import com.rainy.mastodroid.extensions.launchLoading
import com.rainy.mastodroid.features.instanceAuth.model.InstanceAuthData
import com.rainy.mastodroid.util.ErrorModel
import com.rainy.mastodroid.util.toErrorModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.HttpUrl

class WebAuthComponent(
    componentContext: ComponentContext,
    private val authInteractor: AuthInteractor,
    private val instanceAuthData: InstanceAuthData,
    private val onAuthCompleted: () -> Unit,
    private val onAuthError: () -> Unit
) : ComponentContext by componentContext {

    private val coroutineScope = coroutineScope()
    private val defaultExceptionHandler = coroutineExceptionHandler {
        _errorState.value = it.toErrorModel()
    }

    private val _errorState = MutableStateFlow<ErrorModel?>(null)
    val errorState = _errorState.asStateFlow()
    private val _loadingState = MutableStateFlow(false)
    val loadingState = _loadingState.asStateFlow()

    fun getAuthUrl(): HttpUrl {
        val instanceHost = instanceAuthData.instanceHost
        val clientId = instanceAuthData.clientId

        return HttpUrl.Builder()
            .scheme("https")
            .host(instanceHost)
            .addPathSegments("oauth/authorize")
            .addQueryParameter("client_id", clientId)
            .addQueryParameter("redirect_uri", AuthRemoteDataSourceImpl.OAUTH_REDIRECT_URI)
            .addQueryParameter("response_type", "code")
            .addQueryParameter("scope", AuthRemoteDataSourceImpl.OAUTH_SCOPES)
            .build()
    }

    fun onCodeAcquired(code: String) {
        coroutineScope.launchLoading(_loadingState, defaultExceptionHandler) {
            authInteractor.authenticateUser(
                instanceAuthData.instanceHost,
                instanceAuthData.clientId,
                instanceAuthData.clientSecret,
                code
            )
            onAuthCompleted()
        }
    }

    fun onAuthError(error: String) {
        _errorState.value = if (error.isNotEmpty()) {
            ErrorModel.StringError(error)
        } else {
            ErrorModel.ResourceError(R.string.instance_auth_error)
        }
    }

    fun onWebViewError() {
        _errorState.value = ErrorModel.ResourceError(R.string.instance_auth_page_load_error)
    }

    fun onErrorDialogDismiss() {
        onAuthError()
    }
}