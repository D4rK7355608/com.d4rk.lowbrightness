package com.d4rk.lowbrightness.services

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.d4rk.lowbrightness.base.Constants
import com.d4rk.lowbrightness.base.Prefs
import com.d4rk.lowbrightness.base.ServiceController
import java.util.Calendar
import java.util.concurrent.TimeUnit

object SchedulerService {
    private const val WORK_NAME = "SchedulerWorker"
    private const val TAG = "SchedulerService"

    fun evaluateSchedule(context: Context) {
        Log.d(TAG, "Evaluating schedule")
        val appContext = context.applicationContext
        val sharedPreferences = Prefs.get(appContext)
        if (sharedPreferences.getBoolean(Constants.PREF_LOW_BRIGHTNESS_ENABLED, false) &&
            ServiceController.canDrawOverlay(appContext)
        ) {
            val cBegin = getCalendarForStart(appContext)
            val cEnd = getCalendarForEnd(appContext)
            val calendar = Calendar.getInstance()

            val overlayIntent = Intent(appContext, OverlayService::class.java)

            if (calendar.timeInMillis > cBegin.timeInMillis && calendar.timeInMillis < cEnd.timeInMillis) {
                Log.d(TAG, "Enabling overlay")
                ContextCompat.startForegroundService(appContext, overlayIntent)
            } else {
                Log.d(TAG, "Disabling overlay")
                appContext.stopService(overlayIntent)
            }
        } else {
            val overlayIntent = Intent(appContext, OverlayService::class.java)
            Log.d(TAG, "Overlay disabled via prefs or permission missing")
            appContext.stopService(overlayIntent)
        }
    }

    private fun scheduleWork(context: Context) {
        Log.d(TAG, "Scheduling hourly checks with WorkManager")
        val appContext = context.applicationContext
        val request = PeriodicWorkRequestBuilder<SchedulerWorker>(1, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(appContext).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun cancelWork(context: Context) {
        Log.d(TAG, "Cancelling WorkManager tasks")
        val appContext = context.applicationContext
        WorkManager.getInstance(appContext).cancelUniqueWork(WORK_NAME)
    }

    @JvmStatic
    fun getCalendarForStart(context: Context): Calendar {
        val appContext = context.applicationContext
        val sharedPreferences = Prefs.get(appContext)
        val scheduleFromHour = sharedPreferences.getInt("scheduleFromHour", 20)
        val scheduleFromMinute = sharedPreferences.getInt("scheduleFromMinute", 0)
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, scheduleFromHour)
            set(Calendar.MINUTE, scheduleFromMinute)
            clear(Calendar.SECOND)
        }
    }

    @JvmStatic
    fun getCalendarForEnd(context: Context): Calendar {
        val appContext = context.applicationContext
        val sharedPreferences = Prefs.get(appContext)
        val hour = sharedPreferences.getInt("scheduleToHour", 6)
        val minute = sharedPreferences.getInt("scheduleToMinute", 0)
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            clear(Calendar.SECOND)
            if (timeInMillis < getCalendarForStart(appContext).timeInMillis) {
                add(Calendar.DATE, 1)
            }
        }
    }

    @JvmStatic
    fun isEnabled(context: Context): Boolean {
        val prefs = Prefs.get(context.applicationContext)
        return prefs.getBoolean(Constants.PREF_SCHEDULER_ENABLED, false)
    }

    @JvmStatic
    fun enable(context: Context) {
        val appContext = context.applicationContext
        val prefs = Prefs.get(appContext)
        if (!prefs.getBoolean(Constants.PREF_SCHEDULER_ENABLED, false)) {
            prefs.edit { putBoolean(Constants.PREF_SCHEDULER_ENABLED, true) }
            scheduleWork(appContext)
            evaluateSchedule(appContext)
        }
    }

    @JvmStatic
    fun disable(context: Context) {
        val appContext = context.applicationContext
        val prefs = Prefs.get(appContext)
        if (prefs.getBoolean(Constants.PREF_SCHEDULER_ENABLED, false)) {
            prefs.edit { putBoolean(Constants.PREF_SCHEDULER_ENABLED, false) }
            cancelWork(appContext)
            appContext.stopService(Intent(appContext, OverlayService::class.java))
        }
    }
}
