package com.d4rk.lowbrightness.base

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
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
        android.util.Log.d("Application", "refreshServices")
        val overlayEnabled = OverlayService.isEnabled(context)
        val schedulerEnabled = SchedulerService.isEnabled(context)
        val accessibilityEnabled = AccessibilityOverlayService.isEnabled(context)

        val overlayIntent = Intent(context, OverlayService::class.java)
        val schedulerIntent = Intent(context, SchedulerService::class.java)
        val accessibilityIntent = Intent(context, AccessibilityOverlayService::class.java)

        if (overlayEnabled) {
            if (schedulerEnabled) {
                android.util.Log.d("Application", "Start scheduler service")
                context.stopService(overlayIntent)
                ContextCompat.startForegroundService(context, schedulerIntent)
            } else {
                android.util.Log.d("Application", "Start overlay service")
                context.stopService(schedulerIntent)
                ContextCompat.startForegroundService(context, overlayIntent)
            }

            if (accessibilityEnabled) {
                android.util.Log.d("Application", "Start accessibility service")
                context.startService(accessibilityIntent)
            }
        } else {
            android.util.Log.d("Application", "Stopping all services")
            context.stopService(schedulerIntent)
            context.stopService(overlayIntent)
            context.stopService(accessibilityIntent)
        }
    }
}