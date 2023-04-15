/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.di

import com.rainy.mastodroid.core.domain.interactor.AccountInteractor
import com.rainy.mastodroid.core.domain.interactor.AccountInteractorImpl
import com.rainy.mastodroid.core.domain.interactor.AuthInteractor
import com.rainy.mastodroid.core.domain.interactor.AuthInteractorImpl
import com.rainy.mastodroid.core.domain.interactor.TimelineInteractor
import com.rainy.mastodroid.core.domain.interactor.TimelineInteractorImpl
import com.rainy.mastodroid.core.domain.interactor.StatusInteractor
import com.rainy.mastodroid.core.domain.interactor.StatusInteractorImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val interactorModule = module {
    factoryOf(::AuthInteractorImpl) { bind<AuthInteractor>() }
    factoryOf(::TimelineInteractorImpl) { bind<TimelineInteractor>() }
    factoryOf(::StatusInteractorImpl) { bind<StatusInteractor>() }
    factoryOf(::AccountInteractorImpl) { bind<AccountInteractor>() }
}
