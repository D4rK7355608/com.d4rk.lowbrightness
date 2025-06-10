package com.d4rk.lowbrightness.app.brightness.ui.components.dialogs

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import com.d4rk.android.libs.apptoolkit.app.settings.general.ui.GeneralSettingsActivity
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.domain.receivers.NightScreenReceiver
import com.d4rk.lowbrightness.app.brightness.domain.services.isAccessibilityServiceRunning
import com.d4rk.lowbrightness.app.brightness.ui.components.alphaRange
import com.d4rk.lowbrightness.app.brightness.ui.components.closeNightScreen
import com.d4rk.lowbrightness.app.brightness.ui.components.screenAlpha
import com.d4rk.lowbrightness.app.main.ui.MainActivity
import com.d4rk.lowbrightness.app.settings.settings.utils.constants.SettingsConstants
import com.d4rk.lowbrightness.appContext
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.Slider
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import java.lang.Float.max
import java.lang.Float.min

var dialogIsShowing = false
    private set
var dialog: AlertDialog? = null
    private set

fun getNightScreenDialog(c: Context? = null): AlertDialog {
    val context = c ?: ContextThemeWrapper(appContext , com.d4rk.android.libs.apptoolkit.R.style.AppTheme)
    val view = LayoutInflater.from(context).inflate(
        R.layout.dialog_night_screen,
        null,
        false
    )

    val dialog = MaterialAlertDialogBuilder(context)
        .setView(view)
        .create().apply {
            window?.attributes?.dimAmount = 0f
        }.apply {
            setOnShowListener {
                dialogIsShowing = true
                dialog = this
            }
            setOnDismissListener {
                dialogIsShowing = false
                dialog = null
            }
        }

    val ivSettings = view.findViewById<Button>(R.id.btn_settings_night_screen_dialog)
    val ivPowerOff = view.findViewById<Button>(R.id.btn_power_off_night_screen_dialog)
    val slider = view.findViewById<Slider>(R.id.slider_night_screen_dialog)

    ivSettings.setOnClickListener {
        GeneralSettingsActivity.start(
            context = context,
            title = context.getString(R.string.settings_brightness_title),
            contentKey = SettingsConstants.KEY_SETTINGS_BRIGHTNESS
        )
        dialog.dismiss()
    }
    ivPowerOff.setOnClickListener {
        closeNightScreen()
        dialog.dismiss()
    }
    slider.valueFrom = 1f - alphaRange.endInclusive
    slider.valueTo = 1f - alphaRange.start
    slider.value = max(slider.valueFrom, min(1f - screenAlpha, slider.valueTo))
    slider.setLabelFormatter {
        String.format("%.1f", it * 100f) + "%" // FIXME: Implicitly using the default locale is a common source of bugs: Use `String.format(Locale, ...)` instead
    }
    slider.addOnChangeListener { _, value, _ ->
        screenAlpha = 1f - value
    }

    return dialog
}

@SuppressLint("LaunchActivityFromNotification")
fun requestAllPermissionsWithAccessibilityAndShow(context: Context) {
    if (XXPermissions.isGranted(context, Permission.SYSTEM_ALERT_WINDOW) &&
        XXPermissions.isGranted(context, Permission.POST_NOTIFICATIONS) &&
        XXPermissions.isGranted(context, Permission.NOTIFICATION_SERVICE) &&
        XXPermissions.isGranted(context, Permission.WRITE_SETTINGS) &&
        isAccessibilityServiceRunning(context)
    ) {
        NightScreenReceiver.sendBroadcast(
            context = context,
            action = NightScreenReceiver.SHOW_DIALOG_AND_NIGHT_SCREEN_ACTION
        )
    } else {
        context.startActivity(
            Intent(context, MainActivity::class.java).apply {
                action = MainActivity.REQUEST_PERMISSION_AND_SHOW_ACTION
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        )
    }
}