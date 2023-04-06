/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.di

import com.rainy.mastodroid.MainViewModel
import com.rainy.mastodroid.features.accountDetails.AccountDetailsViewModel
import com.rainy.mastodroid.features.home.HomeViewModel
import com.rainy.mastodroid.features.login.LoginViewModel
import com.rainy.mastodroid.features.statusDetails.StatusDetailsViewModel
import com.rainy.mastodroid.features.webAuth.WebAuthViewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule = module {
    viewModelOf(::LoginViewModel)
    viewModelOf(::WebAuthViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::MainViewModel)
    viewModelOf(::StatusDetailsViewModel)
    viewModelOf(::AccountDetailsViewModel)
}
