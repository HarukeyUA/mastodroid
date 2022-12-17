package com.rainy.mastodroid

import android.app.Application
import com.rainy.mastodroid.di.globalModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MastodroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@MastodroidApplication)
            androidLogger(level = Level.ERROR)
            modules(
                globalModule
            )
        }
    }
}
