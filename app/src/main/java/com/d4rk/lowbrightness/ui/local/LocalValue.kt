package com.d4rk.lowbrightness.ui.local

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

val LocalNavController = compositionLocalOf<NavController> {
    error("LocalNavController not initialized!")
}