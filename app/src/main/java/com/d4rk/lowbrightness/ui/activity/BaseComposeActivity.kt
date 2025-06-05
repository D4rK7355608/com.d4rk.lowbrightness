package com.d4rk.lowbrightness.ui.activity

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.d4rk.lowbrightness.ext.theme.transparentSystemBar

abstract class BaseComposeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 透明状态栏
        window.transparentSystemBar(window.decorView.findViewById<ViewGroup>(android.R.id.content))
    }
}