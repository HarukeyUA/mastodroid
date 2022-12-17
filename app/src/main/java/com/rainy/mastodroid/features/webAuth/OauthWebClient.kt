package com.rainy.mastodroid.features.webAuth

import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import androidx.core.net.toUri
import com.google.accompanist.web.AccompanistWebViewClient
import com.rainy.mastodroid.core.data.remote.AuthRemoteDataSourceImpl

class OauthWebClient(
    private val onCodeAcquired: (String) -> Unit,
    private val onAuthError: (String) -> Unit,
    private val onLoadError: () -> Unit,
) : AccompanistWebViewClient() {

    private val oauthRedirectUri = AuthRemoteDataSourceImpl.OAUTH_REDIRECT_URI.toUri()

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?
    ) {
        super.onReceivedError(view, request, error)
        onLoadError()
    }

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        return if (
            request?.url?.host == oauthRedirectUri.host
            && request?.url?.scheme == oauthRedirectUri.scheme
        ) {
            val error = request?.url?.getQueryParameter(ERROR_QUERY_PARAM)
            if (!error.isNullOrEmpty()) {
                onAuthError(error)
            } else {
                val authCode = request?.url?.getQueryParameter(CODE_QUERY_PARAM) ?: ""
                onCodeAcquired(authCode)
            }
            true
        } else {
            return super.shouldOverrideUrlLoading(view, request)
        }
    }

    private companion object {
        const val CODE_QUERY_PARAM = "code"
        const val ERROR_QUERY_PARAM = "error"
    }
}
