package com.d4rk.lowbrightness.ext

import android.content.Context
import android.content.SharedPreferences
import com.d4rk.lowbrightness.appContext


fun Context.sharedPreferences(name: String = "App"): SharedPreferences =
    getSharedPreferences(name, Context.MODE_PRIVATE)

fun SharedPreferences.editor(editorBuilder: SharedPreferences.Editor.() -> Unit) =
    edit().apply(editorBuilder).apply()

fun sharedPreferences(name: String = "App"): SharedPreferences = appContext.sharedPreferences(name)
