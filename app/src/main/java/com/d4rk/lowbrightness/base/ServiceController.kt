package com.d4rk.lowbrightness.base

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.content.ContextCompat
import com.d4rk.lowbrightness.services.AccessibilityOverlayService
import com.d4rk.lowbrightness.services.OverlayService
import com.d4rk.lowbrightness.services.SchedulerService

/**
 * Centralized helper to start or stop all overlay related services.
 */
object ServiceController {
    fun canDrawOverlay(context: Context?): Boolean = Settings.canDrawOverlays(context)

    fun refreshServices(context: Context) {
        val appContext = context.applicationContext
        val overlayEnabled = OverlayService.isEnabled(appContext)
        val schedulerEnabled = SchedulerService.isEnabled(appContext)
        val accessibilityEnabled = AccessibilityOverlayService.isEnabled(appContext)
        val canDrawOverlay = canDrawOverlay(appContext)

        val overlayIntent = Intent(appContext, OverlayService::class.java)
        val accessibilityIntent = Intent(appContext, AccessibilityOverlayService::class.java)

        if (overlayEnabled && canDrawOverlay) {
            if (schedulerEnabled) {
                SchedulerService.enable(appContext) // ensures work scheduled
                appContext.stopService(overlayIntent)
            } else {
                SchedulerService.disable(appContext) // ensures work cancelled
                ContextCompat.startForegroundService(appContext, overlayIntent)
            }

            if (accessibilityEnabled) {
                appContext.startService(accessibilityIntent)
            }
        } else {
            SchedulerService.disable(appContext)
            appContext.stopService(overlayIntent)
            appContext.stopService(accessibilityIntent)
        }
    }
}
