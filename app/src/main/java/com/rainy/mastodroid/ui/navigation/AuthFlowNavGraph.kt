package com.rainy.mastodroid.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.rainy.mastodroid.core.navigation.NavNestedGraph
import com.rainy.mastodroid.features.login.LoginRoute
import com.rainy.mastodroid.features.webAuth.WebAuthRoute

object AuthFlowNavGraph : NavNestedGraph {
    override val route: String = "auth"
    override val startRoute: String = LoginRoute.route

    override fun graph(builder: NavGraphBuilder, navHostController: NavHostController) {
        LoginRoute.composable(builder, navHostController)
        WebAuthRoute.composable(builder, navHostController)
    }
}
