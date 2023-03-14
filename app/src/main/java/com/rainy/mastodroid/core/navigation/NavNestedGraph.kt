/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.navigation

interface NavNestedGraph {

    val route: String

    val startRoute: String

    fun graph(
        builder: NavGraphBuilder,
        navHostController: NavHostController
    )

    fun navigation(
        builder: NavGraphBuilder,
        navHostController: NavHostController
    ) {
        builder.navigation(
            startDestination = startRoute,
            route = route
        ) {
            graph(
                this,
                navHostController
            )
        }
    }

}
