/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.rainy.mastodroid.features.home.HomeRoute
import com.rainy.mastodroid.features.statusDetails.StatusDetailsRoute
import com.rainy.mastodroid.ui.navigation.AuthFlowNavGraph
import com.rainy.mastodroid.ui.theme.MastodroidTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MastodroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val isLoggedInState by viewModel.isLoggedInFlow.collectAsState()

                    isLoggedInState?.also { isLoggedIn ->
                        val navController = rememberNavController()
                        NavHost(
                            navController = navController,
                            startDestination = if (isLoggedIn) HomeRoute.route else AuthFlowNavGraph.route
                        ) {
                            AuthFlowNavGraph.navigation(this, navController)
                            HomeRoute.composable(this, navController)
                            StatusDetailsRoute.composable(this, navController)
                        }
                    }

                }
            }
        }
    }
}
