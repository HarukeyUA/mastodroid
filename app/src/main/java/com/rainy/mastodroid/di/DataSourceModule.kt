package com.rainy.mastodroid.di

import com.rainy.mastodroid.core.data.local.LocalUserLocalDataSourceImpl
import com.rainy.mastodroid.core.data.remote.AuthRemoteDataSourceImpl
import com.rainy.mastodroid.core.data.remote.TimelineRemoteDataSourceImpl
import com.rainy.mastodroid.core.domain.data.local.LocalUserLocalDataSource
import com.rainy.mastodroid.core.domain.data.remote.AuthRemoteDataSource
import com.rainy.mastodroid.core.domain.data.remote.TimelineRemoteDataSource
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val dataSourceModule = module {
    factoryOf(::AuthRemoteDataSourceImpl) { bind<AuthRemoteDataSource>() }
    factoryOf(::LocalUserLocalDataSourceImpl) { bind<LocalUserLocalDataSource>() }
    factoryOf(::TimelineRemoteDataSourceImpl) { bind<TimelineRemoteDataSource>() }
}
