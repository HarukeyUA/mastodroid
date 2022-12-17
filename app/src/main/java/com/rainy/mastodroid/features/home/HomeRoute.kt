package com.rainy.mastodroid.features.home

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.rainy.mastodroid.core.navigation.NavRoute
import com.rainy.mastodroid.features.home.ui.HomeScreen
import org.koin.androidx.compose.koinViewModel

object HomeRoute : NavRoute<HomeViewModel> {

    override val route: String = "home"

    @Composable
    override fun viewModel(): HomeViewModel = koinViewModel()

    @Composable
    override fun Content(viewModel: HomeViewModel) {
        val statusItems = viewModel.homeStatusesFlow.collectAsLazyPagingItems()
        val context = LocalContext.current
        HomeScreen(
            statusesPagingList = statusItems,
            isRefreshing = statusItems.loadState.refresh == LoadState.Loading,
            onRefreshInvoked = {
                statusItems.refresh()
            },
            onUrlClicked = { url ->
                val builder: CustomTabsIntent.Builder = CustomTabsIntent.Builder()
                val customTabsIntent: CustomTabsIntent = builder.build()
                customTabsIntent.launchUrl(context, url.toUri())
            }
        )

    }
}
