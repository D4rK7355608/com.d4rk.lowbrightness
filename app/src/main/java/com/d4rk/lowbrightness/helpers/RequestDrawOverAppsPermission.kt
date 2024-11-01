package com.d4rk.lowbrightness.helpers

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.d4rk.lowbrightness.base.Application.canDrawOverlay

class RequestDrawOverAppsPermission(private val activity : Activity) {
    fun requestCodeMatches(requestCode : Int) : Boolean {
        return REQUEST_CODE == requestCode
    }

    fun canDrawOverlays() : Boolean {
        return canDrawOverlay(activity)
    }

    fun requestPermissionDrawOverOtherApps() {
        if (! Settings.canDrawOverlays(activity)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION ,
                Uri.parse("package:" + activity.packageName)
            )
            activity.startActivityForResult(intent , REQUEST_CODE)
        }
    }

    companion object {
        private const val REQUEST_CODE = 5463
    }
}