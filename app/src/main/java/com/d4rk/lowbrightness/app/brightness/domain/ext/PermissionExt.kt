package com.d4rk.lowbrightness.app.brightness.domain.ext

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.app.brightness.domain.receivers.NightScreenReceiver
import com.d4rk.lowbrightness.appContext
import com.d4rk.lowbrightness.ui.component.showToast

private const val NOTIFICATION_SERVICE_PERMISSION = "android.permission.NOTIFICATION_SERVICE"

fun Context.isSystemAlertWindowGranted(): Boolean = Settings.canDrawOverlays(this)

fun Context.isPostNotificationsGranted(): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

fun Context.isWriteSettingsGranted(): Boolean = Settings.System.canWrite(this)

fun Context.isNotificationServiceEnabled(): Boolean =
    NotificationManagerCompat.from(this).areNotificationsEnabled()

fun Activity.requestSystemAlertWindowPermission(
    onDenied: (never: Boolean) -> Unit = { getString(R.string.no_permission_can_not_run).showToast() },
    onGranted: () -> Unit = {}
) {
    if (isSystemAlertWindowGranted()) {
        onGranted()
    } else {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
        onDenied(false)
    }
}

private val onDeniedCallback: (permissions: MutableList<String>?, never: Boolean) -> Unit =
    { permissions, _ ->
        val permissionList = mutableSetOf<String>()
        if (permissions?.contains(Manifest.permission.SYSTEM_ALERT_WINDOW) == true) {
            permissionList.add(appContext.getString(R.string.alert_window_permission))
        }
        if (permissions?.contains(Manifest.permission.POST_NOTIFICATIONS) == true) {
            permissionList.add(appContext.getString(R.string.post_notification_permission))
        }
        if (permissions?.contains(NOTIFICATION_SERVICE_PERMISSION) == true) {
            permissionList.add(appContext.getString(R.string.post_notification_permission))
        }
        appContext.getString(
            R.string.request_permission_failed,
            if (permissionList.size > 1) permissionList.joinToString() else permissionList.firstOrNull().orEmpty()
        ).showToast()
    }

private val onGrantedCallback: (permissions: MutableList<String>?, all: Boolean) -> Unit =
    { _, all ->
        if (all) appContext.getString(R.string.request_permissions_success).showToast()
    }

fun Activity.requestAllPermissions(
    onDenied: (permissions: MutableList<String>?, never: Boolean) -> Unit = onDeniedCallback,
    onGranted: (permissions: MutableList<String>?, all: Boolean) -> Unit = onGrantedCallback,
) {
    val requested = mutableListOf<String>()
    var allGranted = true

    if (!isSystemAlertWindowGranted()) {
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
        requested.add(Manifest.permission.SYSTEM_ALERT_WINDOW)
        allGranted = false
    }

    if (!isPostNotificationsGranted()) {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        requested.add(Manifest.permission.POST_NOTIFICATIONS)
        allGranted = false
    }

    if (!isNotificationServiceEnabled()) {
        val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        startActivity(intent)
        requested.add(NOTIFICATION_SERVICE_PERMISSION)
        allGranted = false
    }

    if (!isWriteSettingsGranted()) {
        val intent = Intent(
            Settings.ACTION_MANAGE_WRITE_SETTINGS,
            Uri.parse("package:$packageName")
        )
        startActivity(intent)
        requested.add(Manifest.permission.WRITE_SETTINGS)
        allGranted = false
    }

    if (allGranted) {
        onGranted(requested, true)
    } else {
        onDenied(requested, false)
    }
}

fun Activity.requestAllPermissionsAndShow(
    onDenied: (permissions: MutableList<String>?, never: Boolean) -> Unit = onDeniedCallback,
    onGranted: (permissions: MutableList<String>?, all: Boolean) -> Unit = { permissions, all ->
        onGrantedCallback(permissions, all)
        if (isPostNotificationsGranted() && isNotificationServiceEnabled() && isWriteSettingsGranted()) {
            NightScreenReceiver.sendBroadcast(action = NightScreenReceiver.SHOW_DIALOG_AND_NIGHT_SCREEN_ACTION)
            NightScreenReceiver.sendBroadcast(action = NightScreenReceiver.SHOW_NOTIFICATION_ACTION)
        }
    },
) {
    requestAllPermissions(onDenied = onDenied, onGranted = onGranted)
}
