package com.rainy.mastodroid.di

import androidx.room.Room
import com.rainy.mastodroid.database.MainDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dbModule = module {
    single {
        Room.databaseBuilder(androidApplication(), MainDatabase::class.java, "mastodroid.db")
            .build()
    }
    single {
        get<MainDatabase>().getUserDao()
    }
    single {
        get<MainDatabase>().getTimelineDao()
    }
}
