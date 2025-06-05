package com.d4rk.lowbrightness.app.brightness.domain.ext.theme

import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat

fun Window.transparentSystemBar(
    root: View,
    darkFont: Boolean? = context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK != Configuration.UI_MODE_NIGHT_YES
) {
    WindowCompat.setDecorFitsSystemWindows(this, false)
    statusBarColor = Color.TRANSPARENT
    navigationBarColor = Color.TRANSPARENT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        navigationBarDividerColor = Color.TRANSPARENT
    }

    darkFont?.let {
        WindowInsetsControllerCompat(this, root).let { controller ->
            controller.isAppearanceLightStatusBars = it
            controller.isAppearanceLightNavigationBars = it
        }
    }
}