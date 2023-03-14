/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.di

import com.rainy.mastodroid.network.MastodonApi
import com.rainy.mastodroid.network.MastodonPublicApi
import org.koin.dsl.module
import retrofit2.Retrofit

val apiModule = module {
    single {
        get<Retrofit>(UNAUTHENTICATED_CLIENT).create(MastodonPublicApi::class.java)
    }
    single {
        get<Retrofit>(AUTHENTICATED_CLIENT).create(MastodonApi::class.java)
    }
}
