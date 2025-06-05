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
        val overlayEnabled = OverlayService.isEnabled(context)
        val schedulerEnabled = SchedulerService.isEnabled(context)
        val accessibilityEnabled = AccessibilityOverlayService.isEnabled(context)
        val canDrawOverlay = canDrawOverlay(context)

        val overlayIntent = Intent(context, OverlayService::class.java)
        val accessibilityIntent = Intent(context, AccessibilityOverlayService::class.java)

        if (overlayEnabled && canDrawOverlay) {
            if (schedulerEnabled) {
                SchedulerService.enable(context) // ensures work scheduled
                context.stopService(overlayIntent)
            } else {
                SchedulerService.disable(context) // ensures work cancelled
                ContextCompat.startForegroundService(context, overlayIntent)
            }

            if (accessibilityEnabled) {
                context.startService(accessibilityIntent)
            }
        } else {
            SchedulerService.disable(context)
            context.stopService(overlayIntent)
            context.stopService(accessibilityIntent)
        }
    }
}
