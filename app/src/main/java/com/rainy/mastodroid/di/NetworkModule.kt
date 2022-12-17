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

    single(UNAUTHENTICATED_CLIENT) {
        OkHttpClient.Builder()
            .addInterceptor(get<InstanceHostInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
    }

    single(AUTHENTICATED_CLIENT) {
        Retrofit.Builder()
            .addConverterFactory(get())
            .client(get(AUTHENTICATED_CLIENT))
            .baseUrl(LOCALHOST)
            .build()
    }

    single(AUTHENTICATED_CLIENT) {
        OkHttpClient.Builder()
            .addInterceptor(get<AuthenticatedInstanceInterceptor>())
            .addInterceptor(get<HttpLoggingInterceptor>())
            .build()
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
