package com.d4rk.lowbrightness.base

import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.multidex.MultiDexApplication
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
        if (overlayEnabled) {
            if (schedulerEnabled) {
                context.stopService(Intent(context , OverlayService::class.java))
                context.startService(Intent(context , SchedulerService::class.java))
            }
            else {
                context.stopService(Intent(context , SchedulerService::class.java))
                context.startService(Intent(context , OverlayService::class.java))
            }
        }
        else {
            context.stopService(Intent(context , SchedulerService::class.java))
            context.stopService(Intent(context , OverlayService::class.java))
        }
    }
}