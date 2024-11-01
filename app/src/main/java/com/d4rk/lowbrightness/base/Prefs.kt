package com.d4rk.lowbrightness.base

import android.content.Context
import android.content.SharedPreferences

object Prefs {
    @JvmStatic
	fun get(c : Context) : SharedPreferences {
        return c.getSharedPreferences("settings" , Context.MODE_PRIVATE)
    }
}
