package com.d4rk.lowbrightness.core.di

import android.content.Context
import com.d4rk.lowbrightness.core.di.modules.adsModule
import com.d4rk.lowbrightness.core.di.modules.appModule
import com.d4rk.lowbrightness.core.di.modules.appToolkitModule
import com.d4rk.lowbrightness.core.di.modules.dispatchersModule
import com.d4rk.lowbrightness.core.di.modules.settingsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

fun initializeKoin(context : Context) {
    startKoin {
        androidContext(androidContext = context)
        modules(modules = listOf(dispatchersModule , appModule , settingsModule , adsModule , appToolkitModule))
    }
}