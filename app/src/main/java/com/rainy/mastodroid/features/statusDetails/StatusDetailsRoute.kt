package com.rainy.mastodroid.features.statusDetails

import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.rainy.mastodroid.core.navigation.NavRoute
import com.rainy.mastodroid.extensions.observeWithLifecycle
import com.rainy.mastodroid.features.statusDetails.ui.StatusDetailsScreen
import org.koin.androidx.compose.koinViewModel

object StatusDetailsRoute : NavRoute<StatusDetailsViewModel> {
    const val STATUS_ID_ARG = "status_id"

    override val route: String = "statusDetails/{$STATUS_ID_ARG}/"

    fun getRoute(statusId: String): String {
        return route.replace("{$STATUS_ID_ARG}", statusId)
    }

    override fun getArguments(): List<NamedNavArgument> {
        return listOf(
            navArgument(STATUS_ID_ARG) { type = NavType.StringType }
        )
    }

    @Composable
    override fun viewModel(): StatusDetailsViewModel = koinViewModel()

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content(viewModel: StatusDetailsViewModel) {
        val statusDetailsState = viewModel.statusDetailsState.collectAsStateWithLifecycle().value
        val loadingState by viewModel.loadingState.collectAsStateWithLifecycle()
        val context = LocalContext.current
        viewModel.errorEventFlow.observeWithLifecycle {
            Toast.makeText(context, it.resolveText(context), Toast.LENGTH_SHORT).show()
        }

        val pullRefreshState = rememberPullRefreshState(
            refreshing = loadingState, onRefresh = viewModel::loadStatus
        )

        StatusDetailsScreen(
            pullRefreshState = pullRefreshState,
            statusDetailsState = statusDetailsState,
            onFavoriteClicked = viewModel::onFavoriteClicked,
            onReblogClicked = viewModel::onReblogClicked,
            onSensitiveExpandClicked = viewModel::onSensitiveExpandClicked,
            onUrlClicked = { url ->
                val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
                val customTabsIntent: CustomTabsIntent = builder.build()
                customTabsIntent.launchUrl(context, url.toUri())
            },
            loadingState = loadingState,
            onStatusClicked = viewModel::onStatusClicked
        )
    }
}