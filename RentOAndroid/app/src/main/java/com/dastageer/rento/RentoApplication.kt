package com.dastageer.rento

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RentoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RentoApplication)
            modules(/* DI modules added per feature module */)
        }
    }
}
