package com.d4rk.lowbrightness.app.brightness.ui.components

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.view.WindowManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.d4rk.lowbrightness.app.brightness.domain.ext.editor
import com.d4rk.lowbrightness.app.brightness.domain.ext.sharedPreferences
import com.d4rk.lowbrightness.app.brightness.domain.receivers.NightScreenReceiver
import com.d4rk.lowbrightness.app.brightness.domain.services.NightScreenService
import com.d4rk.lowbrightness.app.brightness.ui.components.dialogs.dialog
import com.d4rk.lowbrightness.app.brightness.ui.components.dialogs.dialogIsShowing
import com.d4rk.lowbrightness.app.brightness.ui.components.dialogs.getNightScreenDialog
import com.d4rk.lowbrightness.appContext
import com.d4rk.lowbrightness.app.brightness.domain.ext.isSystemAlertWindowGranted

fun showDialogAndNightScreen() {
    if (dialogIsShowing) return
    if (appContext.isSystemAlertWindowGranted()) {
        showNightScreen()
        getNightScreenDialog().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                window?.setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            } else {
                @Suppress("DEPRECATION")
                window?.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
            }
        }.show()
    }
}

fun closeDialog() {
    dialog?.dismiss()
}

fun showNightScreen() {
    if (showNightScreenLayer) return
    if (appContext.isSystemAlertWindowGranted()) {
        showNightScreenLayer = true
        layerView.visible()
        if (getLowestScreenBrightness() && !getActualLowestScreenBrightness()) {
            applyScreenBrightness(true)
        }
        layerView.keepScreenOn = keepScreenOn
        layerView.updateColor(calculatedColor)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            appContext.sendBroadcast(
                Intent(NightScreenService.ACTION_ACTIVE_TILE)
                    .setPackage(appContext.packageName)
            )
        }
        NightScreenReceiver.sendBroadcast(action = NightScreenReceiver.SHOW_NOTIFICATION_ACTION)
    }
}

fun closeNightScreen() {
    if (!showNightScreenLayer) return
    showNightScreenLayer = false
    dialog?.dismiss()
    layerView.gone()
    applyScreenBrightness(false)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        appContext.sendBroadcast(
            Intent(NightScreenService.ACTION_INACTIVE_TILE)
                .setPackage(appContext.packageName)
        )
    }
    NightScreenReceiver.sendBroadcast(action = NightScreenReceiver.CLOSE_NOTIFICATION_ACTION)
}

var showNightScreenLayer: Boolean = false
    private set

var screenAlpha: Float = sharedPreferences().getFloat("screenAlpha", 0.5f)
    set(value) {
        field = value
        layerView.updateColor(calculatedColor)
        sharedPreferences().editor { putFloat("screenAlpha", value) }
    }

var screenColor: Int = sharedPreferences().getInt("screenColor", Color.Black.toArgb())
    set(value) {
        field = value
        layerView.updateColor(calculatedColor)
        sharedPreferences().editor { putInt("screenColor", value) }
    }

val calculatedColor: Int
    get() = (screenColor and 0xFFFFFF) or ((0xFF * screenAlpha).toInt() shl 24)

val alphaRange = 0f..0.9f

var keepScreenOn = sharedPreferences().getBoolean("keepScreenOn", false)
    set(value) {
        field = value
        layerView.keepScreenOn = value
        sharedPreferences().editor { putBoolean("keepScreenOn", value) }
    }

private var originBrightness: Int = Settings.System.getInt(
    appContext.contentResolver,
    Settings.System.SCREEN_BRIGHTNESS
)

private var originBrightnessMode: Int = Settings.System.getInt(
    appContext.contentResolver,
    Settings.System.SCREEN_BRIGHTNESS_MODE
)

var runAsScheduled = sharedPreferences().getBoolean("runAsScheduled", false)
    set(value) {
        field = value
        sharedPreferences().editor { putBoolean("runAsScheduled", value) }
    }

fun setLowestScreenBrightness(value: Boolean) {
    val old = sharedPreferences().getBoolean("lowestScreenBrightness", false)
    if (value == old) return
    sharedPreferences().editor { putBoolean("lowestScreenBrightness", value) }
}

fun applyScreenBrightness(value: Boolean) {
    if (value && !showNightScreenLayer || !Settings.System.canWrite(appContext)) {
        return
    }
    if (value) {
        originBrightness = Settings.System.getInt(
            appContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS
        )
        originBrightnessMode = Settings.System.getInt(
            appContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE
        )
        Settings.System.putInt(
            appContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS,
            0,
        )
        Settings.System.putInt(
            appContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE,
            Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL,
        )
    } else {
        if (Settings.System.getInt(
                appContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS
            ) == 0
        ) {
            Settings.System.putInt(
                appContext.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS,
                originBrightness,
            )
        }
        if (Settings.System.getInt(
                appContext.contentResolver, Settings.System.SCREEN_BRIGHTNESS_MODE
            ) == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL
        ) {
            Settings.System.putInt(
                appContext.contentResolver,
                Settings.System.SCREEN_BRIGHTNESS_MODE,
                originBrightnessMode,
            )
        }
    }
}

fun getLowestScreenBrightness(): Boolean =
    sharedPreferences().getBoolean("lowestScreenBrightness", false)

fun getActualLowestScreenBrightness(): Boolean = getLowestScreenBrightness() &&
        Settings.System.getInt(
            appContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS
        ) == 0 &&
        Settings.System.getInt(
            appContext.contentResolver,
            Settings.System.SCREEN_BRIGHTNESS_MODE
        ) == Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL

val layerView by lazy { LayerView(appContext) }