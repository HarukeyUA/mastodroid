/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.di

import com.rainy.mastodroid.core.domain.interactor.AuthInteractor
import com.rainy.mastodroid.core.domain.interactor.AuthInteractorImpl
import com.rainy.mastodroid.core.domain.interactor.HomeTimelineInteractor
import com.rainy.mastodroid.core.domain.interactor.HomeTimelineInteractorImpl
import com.rainy.mastodroid.core.domain.interactor.StatusInteractor
import com.rainy.mastodroid.core.domain.interactor.StatusInteractorImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val interactorModule = module {
    factoryOf(::AuthInteractorImpl) { bind<AuthInteractor>() }
    factoryOf(::HomeTimelineInteractorImpl) { bind<HomeTimelineInteractor>() }
    factoryOf(::StatusInteractorImpl) { bind<StatusInteractor>() }
}
