/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.instanceAuth

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.rainy.mastodroid.features.instanceAuth.ui.LoginScreen
import com.rainy.mastodroid.features.webAuth.ui.WebAuthScreen

@Composable
fun InstanceAuthFlow(
    instanceAuthFlowComponent: InstanceAuthFlowComponent
) {
    Children(stack = instanceAuthFlowComponent.stack) {
        when (val child = it.instance) {
            is InstanceAuthFlowComponent.Child.Login -> LoginScreen(loginComponent = child.component)
            is InstanceAuthFlowComponent.Child.WebAuth -> WebAuthScreen(webAuthComponent = child.component)
        }

    }
}