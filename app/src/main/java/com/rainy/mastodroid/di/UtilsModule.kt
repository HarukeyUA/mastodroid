package com.rainy.mastodroid.di

import com.rainy.mastodroid.core.navigation.RouteNavigator
import com.rainy.mastodroid.core.navigation.RouteNavigatorImpl
import com.rainy.mastodroid.util.NetworkExceptionIdentifier
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

@OptIn(ExperimentalSerializationApi::class)
val utilsModule = module {
    factoryOf(::RouteNavigatorImpl) { bind<RouteNavigator>() }
    single {
        Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }
    single {
        NetworkExceptionIdentifier(get())
    }
}
