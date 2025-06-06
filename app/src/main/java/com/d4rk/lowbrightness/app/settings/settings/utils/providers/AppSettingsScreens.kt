package com.d4rk.lowbrightness.app.settings.settings.utils.providers

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import com.d4rk.lowbrightness.app.settings.brightness.ui.BrightnessSettingsList
import com.d4rk.lowbrightness.app.settings.settings.utils.constants.SettingsConstants
import kotlin.to

class AppSettingsScreens {
    val customScreens : Map<String , @Composable (PaddingValues) -> Unit> = mapOf(
        SettingsConstants.KEY_SETTINGS_BRIGHTNESS to { paddingValues -> BrightnessSettingsList(paddingValues) })
}
