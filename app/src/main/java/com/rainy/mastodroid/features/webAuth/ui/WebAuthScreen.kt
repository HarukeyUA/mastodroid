package com.rainy.mastodroid.features.webAuth.ui

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.google.accompanist.web.WebView
import com.google.accompanist.web.WebViewState
import com.rainy.mastodroid.R
import com.rainy.mastodroid.features.webAuth.OauthWebClient
import com.rainy.mastodroid.util.ErrorModel

@Composable
fun WebAuthScreen(
    onCodeAcquired: (String) -> Unit,
    onAuthError: (String) -> Unit,
    onPageLoadError: () -> Unit,
    onErrorDialogDismiss: () -> Unit,
    webViewState: WebViewState,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    error: ErrorModel? = null
) {
    val webClient = remember {
        OauthWebClient(
            onCodeAcquired = onCodeAcquired,
            onAuthError = onAuthError,
            onLoadError = onPageLoadError
        )
    }

    if (error != null) {
        WebAuthErrorDialog(onErrorDialogDismiss, error.resolveText())
    }

    Box(modifier = modifier.fillMaxSize()) {
        AuthWebView(webViewState, webClient)
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.fillMaxWidth()
        ) {
            LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun AuthWebView(
    webViewState: WebViewState,
    webClient: OauthWebClient
) {
    WebView(
        webViewState,
        onCreated = {
            it.settings.apply {
                javaScriptEnabled = true
                allowContentAccess = false
                displayZoomControls = false
                allowFileAccess = false
            }
        },
        client = webClient,
        modifier = Modifier
            .fillMaxSize()
    )
}

@Composable
fun WebAuthErrorDialog(
    onErrorDialogDismiss: () -> Unit,
    errorText: String
) {
    AlertDialog(
        onDismissRequest = onErrorDialogDismiss,
        title = {
            Text(stringResource(id = R.string.instance_auth_error_dialog_title))
        },
        text = {
            Text(errorText)
        },
        confirmButton = {
            TextButton(onClick = onErrorDialogDismiss) {
                Text(stringResource(R.string.ok))
            }
        }
    )
}
