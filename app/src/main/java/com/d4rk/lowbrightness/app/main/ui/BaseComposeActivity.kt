package com.d4rk.lowbrightness.app.main.ui

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.d4rk.lowbrightness.app.brightness.domain.ext.theme.transparentSystemBar

abstract class BaseComposeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.transparentSystemBar(window.decorView.findViewById<ViewGroup>(android.R.id.content))
    }
}