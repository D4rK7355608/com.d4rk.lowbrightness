package com.d4rk.lowbrightness.app.settings.brightness.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import com.d4rk.android.libs.apptoolkit.core.ui.components.preferences.PreferenceCategoryItem
import com.d4rk.android.libs.apptoolkit.core.ui.components.preferences.SwitchPreferenceItem
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.ExtraTinyVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.ui.components.spacers.SmallVerticalSpacer
import com.d4rk.android.libs.apptoolkit.core.utils.constants.ui.SizeConstants
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
import com.d4rk.lowbrightness.ui.component.ColorSettingsItem

@Composable
fun BrightnessSettingsList(paddingValues : PaddingValues) {
    //var color by remember { mutableIntStateOf(screenColor) }
    //var alphaColor by remember { mutableIntStateOf(calculatedColor) }
    var lowestScreenChecked by remember { mutableStateOf(getLowestScreenBrightness()) }
    var keepScreenOnChecked by remember { mutableStateOf(keepScreenOn) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = paddingValues
    ) {
        /*item {
            PreferenceCategoryItem(title = stringResource(id = R.string.settings_screen_color_settings))
            SmallVerticalSpacer()

            Column(
                modifier = Modifier
                    .padding(horizontal = SizeConstants.LargeSize)
                    .clip(shape = RoundedCornerShape(size = SizeConstants.LargeSize))
            ) {
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
        }*/
        item {
            PreferenceCategoryItem(title = stringResource(id = R.string.settings_screen_screen_settings))
            SmallVerticalSpacer()
            Column(
                modifier = Modifier
                    .padding(horizontal = SizeConstants.LargeSize)
                    .clip(shape = RoundedCornerShape(size = SizeConstants.LargeSize))
            ) {
                SwitchPreferenceItem(
                    title = stringResource(id = R.string.settings_screen_keep_screen_on) ,
                    summary = stringResource(id = R.string.settings_screen_keep_screen_on_description) ,
                    checked = keepScreenOnChecked ,
                ) { isChecked ->
                    keepScreenOnChecked = isChecked
                    keepScreenOn = isChecked
                }

                ExtraTinyVerticalSpacer()

                SwitchPreferenceItem(
                    title = stringResource(id = R.string.settings_screen_lowest_screen_brightness) ,
                    summary = stringResource(id = R.string.settings_screen_lowest_screen_brightness_description) ,
                    checked = lowestScreenChecked ,
                ) { isChecked ->
                    lowestScreenChecked = isChecked
                    setLowestScreenBrightness(isChecked)
                    applyScreenBrightness(isChecked)
                }
            }
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