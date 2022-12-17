package com.rainy.mastodroid.di

import com.rainy.mastodroid.core.domain.interactor.AuthInteractor
import com.rainy.mastodroid.core.domain.interactor.AuthInteractorImpl
import com.rainy.mastodroid.core.domain.interactor.HomeTimelineInteractor
import com.rainy.mastodroid.core.domain.interactor.HomeTimelineInteractorImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val interactorModule = module {
    factoryOf(::AuthInteractorImpl) { bind<AuthInteractor>() }
    factoryOf(::HomeTimelineInteractorImpl) { bind<HomeTimelineInteractor>() }
}
