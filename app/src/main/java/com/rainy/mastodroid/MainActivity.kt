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
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.rainy.mastodroid.features.accountDetails.ui.AccountDetailsScreen
import com.rainy.mastodroid.features.home.ui.HomeScreen
import com.rainy.mastodroid.features.instanceAuth.InstanceAuthFlow
import com.rainy.mastodroid.features.statusDetails.ui.StatusDetailsScreen
import com.rainy.mastodroid.ui.theme.MastodroidTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splash = installSplashScreen()
        super.onCreate(savedInstanceState)

        val rootComponent = RootComponent(
            componentContext = defaultComponentContext()
        )

        rootComponent.stack.subscribe {
            splash.setKeepOnScreenCondition {
                it.active.configuration is RootComponent.Config.Splash
            }
        }

        setContent {
            MastodroidTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
                ) {

                    Children(
                        stack = rootComponent.stack,
                        animation = stackAnimation { child, otherChild, direction ->
                            fade()
                        }
                    ) {
                        when (val child = it.instance) {
                            is RootComponent.Child.Home -> HomeScreen(homeComponent = child.homeComponent)
                            is RootComponent.Child.InstanceAuth -> InstanceAuthFlow(
                                instanceAuthFlowComponent = child.instanceAuthFlowComponent
                            )

                            RootComponent.Child.Splash -> {}
                            is RootComponent.Child.ProfileDetails -> AccountDetailsScreen(component = child.profileDetailsComponent)
                            is RootComponent.Child.StatusContext -> StatusDetailsScreen(
                                component = child.statusContextComponent
                            )
                        }
                    }

                }
            }
        }
    }
}
