package com.d4rk.lowbrightness.helpers

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import com.d4rk.lowbrightness.services.AccessibilityOverlayService

class RequestAccessibilityPermission(private val activity: Activity) {
    fun isAccessibilityEnabled(): Boolean {
        val expectedComponent = ComponentName(activity, AccessibilityOverlayService::class.java)
        val settingValue = Settings.Secure.getString(
            activity.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        val splitter = TextUtils.SimpleStringSplitter(':')
        splitter.setString(settingValue)
        splitter.forEach {
            if (it.equals(expectedComponent.flattenToString(), ignoreCase = true)) {
                return true
            }
        }
        return false
    }

    fun requestAccessibilityPermission() {
        activity.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }
}
