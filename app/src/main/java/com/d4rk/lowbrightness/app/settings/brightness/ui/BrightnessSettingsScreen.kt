package com.d4rk.lowbrightness.app.settings.brightness.ui

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness5
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.ui.components.applyScreenBrightness
import com.d4rk.lowbrightness.app.brightness.ui.components.calculatedColor
import com.d4rk.lowbrightness.app.brightness.ui.components.dialogs.AlphaDialog
import com.d4rk.lowbrightness.app.brightness.ui.components.dialogs.ColorDialog
import com.d4rk.lowbrightness.app.brightness.ui.components.getLowestScreenBrightness
import com.d4rk.lowbrightness.app.brightness.ui.components.keepScreenOn
import com.d4rk.lowbrightness.app.brightness.ui.components.screenAlpha
import com.d4rk.lowbrightness.app.brightness.ui.components.screenColor
import com.d4rk.lowbrightness.app.brightness.ui.components.setLowestScreenBrightness
import com.d4rk.lowbrightness.ui.component.CategorySettingsItem
import com.d4rk.lowbrightness.ui.component.ColorSettingsItem
import com.d4rk.lowbrightness.ui.component.SwitchSettingsItem

@Composable
fun BrightnessSettingsScreen(paddingValues : PaddingValues) {
    var color by remember { mutableIntStateOf(screenColor) }
    var alphaColor by remember { mutableIntStateOf(calculatedColor) }
    var lowestScreenChecked by remember { mutableStateOf(getLowestScreenBrightness()) }
    var keepScreenOnChecked by remember { mutableStateOf(keepScreenOn) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = paddingValues
    ) {
        item {
            CategorySettingsItem(
                text = stringResource(id = R.string.settings_screen_color_settings)
            )
        }
        item {
            var openColorDialog by remember { mutableStateOf(false) }
            ColorSettingsItem(
                icon = Icons.Default.ColorLens,
                text = stringResource(id = R.string.settings_screen_screen_color),
                description = stringResource(id = R.string.settings_screen_choice_a_color),
                initColor = Color(color),
                onClick = { openColorDialog = true },
            )
            if (openColorDialog) {
                ColorDialog(
                    initColor = Color(screenColor),
                    onDismissRequest = { openColorDialog = false },
                    onColorSelected = {
                        screenColor = it.toArgb()
                        color = screenColor
                        alphaColor = calculatedColor
                    }
                )
            }
        }
        item {
            var openAlphaDialog by remember { mutableStateOf(false) }
            ColorSettingsItem(
                icon = Icons.Default.Opacity,
                text = stringResource(id = R.string.settings_screen_screen_alpha),
                description = stringResource(id = R.string.settings_screen_choice_a_alpha),
                initColor = Color(alphaColor),
                onClick = { openAlphaDialog = true },
            )
            if (openAlphaDialog) {
                AlphaDialog(
                    initColor = Color(calculatedColor),
                    initAlpha = screenAlpha,
                    onDismissRequest = { openAlphaDialog = false },
                    onAlphaSelected = {
                        screenAlpha = it
                        alphaColor = calculatedColor
                    }
                )
            }
        }
        item {
            CategorySettingsItem(
                text = stringResource(id = R.string.settings_screen_screen_settings)
            )
        }
        item {
            SwitchSettingsItem(
                icon = Icons.Default.Brightness7,
                text = stringResource(id = R.string.settings_screen_keep_screen_on),
                description = stringResource(id = R.string.settings_screen_keep_screen_on_description),
                checked = keepScreenOnChecked,
                onCheckedChange = {
                    keepScreenOnChecked = it
                    keepScreenOn = it
                },
            )
        }
        item {
            SwitchSettingsItem(
                icon = Icons.Default.Brightness5,
                text = stringResource(id = R.string.settings_screen_lowest_screen_brightness),
                description = stringResource(id = R.string.settings_screen_lowest_screen_brightness_description),
                checked = lowestScreenChecked,
                onCheckedChange = {
                    lowestScreenChecked = it
                    setLowestScreenBrightness(it)
                    applyScreenBrightness(it)
                },
            )
        }
//            item {
//                CategorySettingsItem(
//                    text = stringResource(R.string.settings_screen_scheduled_task_settings)
//                )
//            }
//            item {
//                SwitchSettingsItem(
//                    icon = Icons.Default.Bedtime,
//                    text = stringResource(id = R.string.settings_screen_schedule_task_auto_run),
//                    description = null,
//                    checked = runAsScheduledChecked,
//                    onCheckedChange = {
//                        runAsScheduledChecked = it
//                        runAsScheduled = it
//                    },
//                )
//            }
//            item {
//                BaseSettingsItem(
//                    icon = rememberVectorPainter(image = Icons.Default.Alarm),
//                    text = stringResource(R.string.settings_screen_schedule_task_start_time),
//                    descriptionText = "",
//                    onClick = { showTimePicker = true },
//                )
//            }
//            item {
//                BaseSettingsItem(
//                    icon = rememberVectorPainter(image = Icons.Default.Snooze),
//                    text = stringResource(R.string.settings_screen_schedule_task_stop_time),
//                    descriptionText = "",
//                )
//            }
    }
}