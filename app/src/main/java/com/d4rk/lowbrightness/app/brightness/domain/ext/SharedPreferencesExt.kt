package com.d4rk.lowbrightness.app.brightness.domain.ext

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.d4rk.lowbrightness.appContext


fun Context.sharedPreferences(name: String = "App"): SharedPreferences =
    getSharedPreferences(name, Context.MODE_PRIVATE)

fun SharedPreferences.editor(editorBuilder: SharedPreferences.Editor.() -> Unit): Unit =
    edit(commit = true, action = editorBuilder)

fun sharedPreferences(name: String = "App"): SharedPreferences = appContext.sharedPreferences(name)
