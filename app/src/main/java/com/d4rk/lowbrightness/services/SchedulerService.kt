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
        val sharedPreferences = Prefs.get(context)
        if (sharedPreferences.getBoolean(Constants.PREF_LOW_BRIGHTNESS_ENABLED, false) &&
            ServiceController.canDrawOverlay(context)
        ) {
            val cBegin = getCalendarForStart(context)
            val cEnd = getCalendarForEnd(context)
            val calendar = Calendar.getInstance()

            val overlayIntent = Intent(context, OverlayService::class.java)

            if (calendar.timeInMillis > cBegin.timeInMillis && calendar.timeInMillis < cEnd.timeInMillis) {
                Log.d(TAG, "Enabling overlay")
                ContextCompat.startForegroundService(context, overlayIntent)
            } else {
                Log.d(TAG, "Disabling overlay")
                context.stopService(overlayIntent)
            }
        } else {
            val overlayIntent = Intent(context, OverlayService::class.java)
            Log.d(TAG, "Overlay disabled via prefs or permission missing")
            context.stopService(overlayIntent)
        }
    }

    private fun scheduleWork(context: Context) {
        Log.d(TAG, "Scheduling hourly checks with WorkManager")
        val request = PeriodicWorkRequestBuilder<SchedulerWorker>(1, TimeUnit.HOURS)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun cancelWork(context: Context) {
        Log.d(TAG, "Cancelling WorkManager tasks")
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }

    @JvmStatic
    fun getCalendarForStart(context: Context): Calendar {
        val sharedPreferences = Prefs.get(context)
        val scheduleFromHour = sharedPreferences.getInt(Constants.PREF_SCHEDULE_FROM_HOUR, 20)
        val scheduleFromMinute = sharedPreferences.getInt(Constants.PREF_SCHEDULE_FROM_MINUTE, 0)
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, scheduleFromHour)
            set(Calendar.MINUTE, scheduleFromMinute)
            clear(Calendar.SECOND)
        }
    }

    @JvmStatic
    fun getCalendarForEnd(context: Context): Calendar {
        val sharedPreferences = Prefs.get(context)
        val hour = sharedPreferences.getInt(Constants.PREF_SCHEDULE_TO_HOUR, 6)
        val minute = sharedPreferences.getInt(Constants.PREF_SCHEDULE_TO_MINUTE, 0)
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            clear(Calendar.SECOND)
            if (timeInMillis < getCalendarForStart(context).timeInMillis) {
                add(Calendar.DATE, 1)
            }
        }
    }

    @JvmStatic
    fun isEnabled(context: Context): Boolean {
        val prefs = Prefs.get(context)
        return prefs.getBoolean(Constants.PREF_SCHEDULER_ENABLED, false)
    }

    @JvmStatic
    fun enable(context: Context) {
        val prefs = Prefs.get(context)
        if (!prefs.getBoolean(Constants.PREF_SCHEDULER_ENABLED, false)) {
            prefs.edit { putBoolean(Constants.PREF_SCHEDULER_ENABLED, true) }
            scheduleWork(context)
            evaluateSchedule(context)
        }
    }

    @JvmStatic
    fun disable(context: Context) {
        val prefs = Prefs.get(context)
        if (prefs.getBoolean(Constants.PREF_SCHEDULER_ENABLED, false)) {
            prefs.edit { putBoolean(Constants.PREF_SCHEDULER_ENABLED, false) }
            cancelWork(context)
            context.stopService(Intent(context, OverlayService::class.java))
        }
    }
}
