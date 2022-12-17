package com.rainy.mastodroid.features.webAuth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.web.rememberWebViewState
import com.rainy.mastodroid.core.navigation.NavRoute
import com.rainy.mastodroid.features.webAuth.ui.WebAuthScreen
import org.koin.androidx.compose.getViewModel

object WebAuthRoute : NavRoute<WebAuthViewModel> {
    const val INSTANCE_HOST_ARG = "instance_host"
    const val CLIENT_ID_ARG = "client_id"
    const val CLIENT_SECRET_ARG = "client_secret"

    override val route: String =
        "webAuth/{$INSTANCE_HOST_ARG}/{$CLIENT_ID_ARG}/{$CLIENT_SECRET_ARG}/"

    fun getRoute(instanceHost: String, clientId: String, clientSecret: String) =
        route.replace("{$INSTANCE_HOST_ARG}", instanceHost)
            .replace("{$CLIENT_ID_ARG}", clientId)
            .replace("{$CLIENT_SECRET_ARG}", clientSecret)

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(INSTANCE_HOST_ARG) { type = NavType.StringType },
            navArgument(CLIENT_ID_ARG) { type = NavType.StringType },
            navArgument(CLIENT_SECRET_ARG) { type = NavType.StringType }
        )
    }

    @Composable
    override fun viewModel(): WebAuthViewModel = getViewModel()

    @Composable
    override fun Content(viewModel: WebAuthViewModel) {
        val webViewState = rememberWebViewState(viewModel.getAuthUrl().toString())
        val authErrorState by viewModel.errorFlow.collectAsState()
        val loadingState by viewModel.loadingState.collectAsState()

        WebAuthScreen(
            onCodeAcquired = viewModel::onCodeAcquired,
            onAuthError = viewModel::onAuthError,
            onPageLoadError = viewModel::onWebViewError,
            webViewState = webViewState,
            isLoading = loadingState || webViewState.isLoading,
            onErrorDialogDismiss = viewModel::onErrorDialogDismiss,
            error = authErrorState
        )
    }
}
