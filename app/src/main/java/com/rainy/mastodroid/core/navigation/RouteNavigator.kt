/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.core.navigation

import androidx.navigation.NavHostController
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

typealias NavigationCommand = NavHostController.() -> Unit

interface RouteNavigator {
    val navigationChannel: Channel<NavigationCommand>
    val navigationEvent: Flow<NavigationCommand>
    fun performNavigation(navCommand: NavigationCommand)
}

class RouteNavigatorImpl : RouteNavigator {

    override val navigationChannel: Channel<NavigationCommand> = Channel(Channel.UNLIMITED)
    override val navigationEvent = navigationChannel.receiveAsFlow()

    override fun performNavigation(navCommand: NavigationCommand) {
        navigationChannel.trySend(navCommand)
    }
}
