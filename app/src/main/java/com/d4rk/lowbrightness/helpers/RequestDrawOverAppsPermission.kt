package com.d4rk.lowbrightness.helpers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import com.d4rk.lowbrightness.base.Application.canDrawOverlay

class RequestDrawOverAppsPermission(private val activity : Activity) {

    fun canDrawOverlays() : Boolean {
        return canDrawOverlay(activity)
    }

    fun requestPermissionDrawOverOtherApps(launcher: ActivityResultLauncher<Intent>) {
        if (!Settings.canDrawOverlays(activity)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + activity.packageName)
            )
            launcher.launch(intent)
        }
    }
}