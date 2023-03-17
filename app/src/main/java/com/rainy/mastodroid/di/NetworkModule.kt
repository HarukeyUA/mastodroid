/*
 * Copyright 2023 HarukeyUA
 * SPDX-License-Identifier: GPL-3.0-only
 */

package com.rainy.mastodroid.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.rainy.mastodroid.BuildConfig
import com.rainy.mastodroid.network.interceptors.AuthenticatedInstanceInterceptor
import com.rainy.mastodroid.network.interceptors.InstanceHostInterceptor
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val UNAUTHENTICATED_CLIENT = named("UNAUTHENTICATED")
val AUTHENTICATED_CLIENT = named("AUTHENTICATED")
val DEFAULT_CLIENT = named("DEFAULT")
private const val LOCALHOST = "https://127.0.0.1/"

@OptIn(ExperimentalSerializationApi::class)
val networkModule = module {
    single(UNAUTHENTICATED_CLIENT) {
        Retrofit.Builder()
            .addConverterFactory(get())
            .client(get(UNAUTHENTICATED_CLIENT))
            .baseUrl(LOCALHOST)
            .build()
    }

    single(AUTHENTICATED_CLIENT) {
        Retrofit.Builder()
            .addConverterFactory(get())
            .client(get(AUTHENTICATED_CLIENT))
            .baseUrl(LOCALHOST)
            .build()
    }

    single(UNAUTHENTICATED_CLIENT) {
        get<OkHttpClient>(DEFAULT_CLIENT)
            .newBuilder()
            .addInterceptor(get<InstanceHostInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single(AUTHENTICATED_CLIENT) {
        get<OkHttpClient>(DEFAULT_CLIENT)
            .newBuilder()
            .addInterceptor(get<AuthenticatedInstanceInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single(DEFAULT_CLIENT) {
        OkHttpClient()
    }

    single {
        HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    single {
        InstanceHostInterceptor()
    }

    single {
        AuthenticatedInstanceInterceptor(get())
    }

    single {
        get<Json>().asConverterFactory("application/json".toMediaType())
    }

}
