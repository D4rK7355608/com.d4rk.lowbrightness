package com.d4rk.lowbrightness.base

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.multidex.MultiDexApplication
import com.d4rk.lowbrightness.services.AccessibilityOverlayService
import com.d4rk.lowbrightness.services.OverlayService
import com.d4rk.lowbrightness.services.SchedulerService

object Application : MultiDexApplication() {
    @JvmStatic
    fun canDrawOverlay(context : Context?) : Boolean {
        return Settings.canDrawOverlays(context)
    }

    @JvmStatic
    fun refreshServices(context : Context) {
        val overlayEnabled = OverlayService.isEnabled(context)
        val schedulerEnabled = SchedulerService.isEnabled(context)
        val accessibilityEnabled = AccessibilityOverlayService.isEnabled(context)

        val overlayIntent = Intent(context, OverlayService::class.java)
        val schedulerIntent = Intent(context, SchedulerService::class.java)
        val accessibilityIntent = Intent(context, AccessibilityOverlayService::class.java)

        if (overlayEnabled) {
            if (schedulerEnabled) {
                context.stopService(overlayIntent)
                context.startService(schedulerIntent)
            } else {
                context.stopService(schedulerIntent)
                context.startService(overlayIntent)
            }

            if (accessibilityEnabled) {
                context.startService(accessibilityIntent)
            }
        } else {
            context.stopService(schedulerIntent)
            context.stopService(overlayIntent)
            context.stopService(accessibilityIntent)
        }
    }
}