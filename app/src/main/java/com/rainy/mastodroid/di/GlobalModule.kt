/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.di

val globalModule = listOf(
    apiModule,
    dataSourceModule,
    networkModule,
    utilsModule,
    viewModelModule,
    dbModule,
    interactorModule,
)
