package com.rainy.mastodroid.core.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.rainy.mastodroid.extensions.observeWithLifecycle

/**
 * A route the app can navigate to.
 */
interface NavRoute<T : RouteNavigator> {

    val route: String

    /**
     * Returns the screen's content.
     */
    @Composable
    fun Content(viewModel: T)

    /**
     * Returns the screen's ViewModel.
     */
    @Composable
    fun viewModel(): T

    /**
     * Override when this page uses arguments.
     *
     * We do it here and not in the [NavigationComponent to keep it centralized]
     */
    fun getArguments(): List<NamedNavArgument> = listOf()

    /**
     * Generates the composable for this route.
     */
    fun composable(
        builder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        builder.composable(route, getArguments()) {
            val viewModel = viewModel()

            viewModel.navigationEvent.observeWithLifecycle { navEvent ->
                navEvent.invoke(navHostController)
            }

            Content(viewModel)
        }
    }
}

fun <T> SavedStateHandle.getOrThrow(key: String): T =
    get<T>(key) ?: throw IllegalArgumentException(
        "Mandatory argument $key missing in arguments."
    )
