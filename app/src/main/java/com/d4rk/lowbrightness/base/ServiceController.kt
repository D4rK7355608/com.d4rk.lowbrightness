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

        val overlayIntent = Intent(context, OverlayService::class.java)
        val schedulerIntent = Intent(context, SchedulerService::class.java)
        val accessibilityIntent = Intent(context, AccessibilityOverlayService::class.java)

        if (overlayEnabled) {
            if (schedulerEnabled) {
                context.stopService(overlayIntent)
                ContextCompat.startForegroundService(context, schedulerIntent)
            } else {
                context.stopService(schedulerIntent)
                ContextCompat.startForegroundService(context, overlayIntent)
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
