/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.rainy.mastodroid.core.domain.interactor.AuthInteractor
import com.rainy.mastodroid.extensions.coroutineScope
import com.rainy.mastodroid.features.accountDetails.AccountDetailsComponent
import com.rainy.mastodroid.features.home.HomeComponent
import com.rainy.mastodroid.features.instanceAuth.InstanceAuthFlowComponent
import com.rainy.mastodroid.features.statusDetails.StatusContextComponent
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.component.inject
import java.util.UUID

class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, KoinComponent {
    private val coroutineScope = coroutineScope()
    private val authInteractor: AuthInteractor by inject()

    private val navigation = StackNavigation<Config>()

    val stack: Value<ChildStack<*, Child>> =
        childStack(
            source = navigation,
            initialConfiguration = Config.Splash,
            childFactory = ::child,
            handleBackButton = true
        )

    init {
        if (stack.value.active.instance is Child.Splash) {
            coroutineScope.launch {
                if (authInteractor.isLoggedIn()) {
                    navigation.replaceAll(Config.Home)
                } else {
                    navigation.replaceAll(Config.InstanceAuth)
                }
            }
        }
    }

    private fun child(config: Config, componentContext: ComponentContext): Child =
        when (config) {
            is Config.Splash -> Child.Splash
            Config.InstanceAuth -> Child.InstanceAuth(
                instanceAuthFlowComponent = InstanceAuthFlowComponent(
                    componentContext = componentContext,
                    onAuthCompleted = {
                        navigation.replaceAll(Config.Home)
                    }
                )
            )

            Config.Home -> Child.Home(
                HomeComponent(
                    componentContext = componentContext,
                    timelineInteractor = get(),
                    statusInteractor = get(),
                    navigateToAccount = { id ->
                        navigation.push(
                            Config.ProfileDetails(id)
                        )
                    },
                    navigateToStatusAttachments = { statusId, attachmentIndex ->
                        // TODO
                    },
                    navigateToStatusContext = { id ->
                        navigation.push(
                            Config.StatusContext(id)
                        )
                    }
                )
            )

            is Config.ProfileDetails -> Child.ProfileDetails(
                profileDetailsComponent = AccountDetailsComponent(
                    componentContext = componentContext,
                    accountInteractor = get(),
                    timelineInteractor = get(),
                    statusInteractor = get(),
                    accountId = config.id
                )
            )

            is Config.StatusContext -> Child.StatusContext(
                statusContextComponent = StatusContextComponent(
                    componentContext = componentContext,
                    statusInteractor = get(),
                    statusId = config.id,
                    navigateToStatusContext = { id ->
                        navigation.push(
                            Config.StatusContext(id)
                        )
                    },
                    navigateToAccount = { id ->
                        navigation.push(
                            Config.ProfileDetails(id)
                        )
                    },
                    navigateToStatusAttachments = { statusId, attachmentIndex ->
                        // TODO
                    }
                )
            )
        }

    sealed class Child {
        object Splash : Child()
        class InstanceAuth(val instanceAuthFlowComponent: InstanceAuthFlowComponent) : Child()
        class Home(val homeComponent: HomeComponent) : Child()
        class StatusContext(val statusContextComponent: StatusContextComponent) : Child()
        class ProfileDetails(val profileDetailsComponent: AccountDetailsComponent) : Child()
    }

    @Parcelize
    sealed class Config : Parcelable {
        object Splash : Config()
        object InstanceAuth : Config()
        object Home : Config()
        data class StatusContext(
            val id: String,
            val uuid: Long = UUID.randomUUID().mostSignificantBits
        ) : Config()

        data class ProfileDetails(
            val id: String,
            val uuid: Long = UUID.randomUUID().mostSignificantBits
        ) : Config()
    }


}