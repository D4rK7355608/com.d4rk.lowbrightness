package com.d4rk.lowbrightness.base

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    @JvmStatic
    fun get(c: Context): SharedPreferences {
        val appContext = c.applicationContext
        return appContext.getSharedPreferences(Constants.PREF_FILE_SETTINGS, Context.MODE_PRIVATE)
    }
}
