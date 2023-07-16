/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.features.instanceAuth

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.rainy.mastodroid.features.instanceAuth.model.InstanceAuthData
import com.rainy.mastodroid.features.webAuth.WebAuthComponent
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class InstanceAuthFlowComponent(
    componentContext: ComponentContext,
    private val onAuthCompleted: () -> Unit
) : ComponentContext by componentContext, KoinComponent {

    private val navigation = StackNavigation<Config>()

    val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            initialConfiguration = Config.Login,
            childFactory = ::child,
            handleBackButton = true
        )

    private fun child(
        config: Config,
        componentContext: ComponentContext
    ): Child {
        return when (config) {
            Config.Login -> Child.Login(
                LoginComponent(
                    componentContext = componentContext,
                    authRemoteDataSource = get(),
                    onHostAcquired = { instanceAuthData ->
                        navigation.push(Config.WebAuth(instanceAuthData))
                    }
                )
            )

            is Config.WebAuth -> Child.WebAuth(
                WebAuthComponent(
                    componentContext = componentContext,
                    authInteractor = get(),
                    instanceAuthData = config.instanceAuthData,
                    onAuthCompleted = {
                        onAuthCompleted()
                    },
                    onAuthError = {

                    }
                )
            )
        }
    }

    sealed class Child {
        class Login(val component: LoginComponent) : Child()
        class WebAuth(val component: WebAuthComponent) : Child()
    }

    @Parcelize
    sealed class Config : Parcelable {
        object Login : Config()
        data class WebAuth(val instanceAuthData: InstanceAuthData) : Config()
    }
}