package com.d4rk.lowbrightness

import android.app.Application
import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
        }
        appContext = this
    }
}

lateinit var appContext: Context
