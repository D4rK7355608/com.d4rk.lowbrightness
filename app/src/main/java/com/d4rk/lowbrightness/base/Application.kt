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
        if (overlayEnabled) {
            if (schedulerEnabled) {
                context.stopService(Intent(context , OverlayService::class.java)) // FIXME: This argument is a new instance so `stopService` will not remove anything
                context.startService(Intent(context , SchedulerService::class.java)) // FIXME: This argument is a new instance so `stopService` will not remove anything
            }
            else {
                context.stopService(Intent(context , SchedulerService::class.java))
                context.startService(Intent(context , OverlayService::class.java))
            }
            if (accessibilityEnabled) {
                context.startService(Intent(context , AccessibilityOverlayService::class.java))
            }
        }
        else {
            context.stopService(Intent(context , SchedulerService::class.java)) // FIXME: This argument is a new instance so `stopService` will not remove anything
            context.stopService(Intent(context , OverlayService::class.java)) // FIXME: This argument is a new instance so `stopService` will not remove anything
            context.stopService(Intent(context , AccessibilityOverlayService::class.java)) // FIXME: This argument is a new instance so `stopService` will not remove anything
        }
    }
}