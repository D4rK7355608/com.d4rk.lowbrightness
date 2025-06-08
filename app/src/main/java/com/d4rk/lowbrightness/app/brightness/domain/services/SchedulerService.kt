package com.d4rk.lowbrightness.app.brightness.domain.services

import android.content.Context
import androidx.core.content.edit
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.d4rk.lowbrightness.app.brightness.domain.ext.sharedPreferences
import com.d4rk.lowbrightness.app.brightness.ui.components.closeNightScreen
import com.d4rk.lowbrightness.app.brightness.ui.components.runAsScheduled
import com.d4rk.lowbrightness.app.brightness.ui.components.showNightScreen
import java.util.Calendar
import java.util.concurrent.TimeUnit

object SchedulerService {
    private const val WORK_NAME = "SchedulerWorker"
    private const val PREF_SCHEDULER_ENABLED = "scheduler_enabled"
    private const val PREF_SCHEDULE_FROM_HOUR = "scheduleFromHour"
    private const val PREF_SCHEDULE_FROM_MINUTE = "scheduleFromMinute"
    private const val PREF_SCHEDULE_TO_HOUR = "scheduleToHour"
    private const val PREF_SCHEDULE_TO_MINUTE = "scheduleToMinute"

    fun isEnabled(context: Context): Boolean =
        context.sharedPreferences().getBoolean(PREF_SCHEDULER_ENABLED, false)

    fun enable(context: Context) {
        if (isEnabled(context)) return
        context.sharedPreferences().edit { putBoolean(PREF_SCHEDULER_ENABLED, true) }
        runAsScheduled = true
        scheduleWork(context)
        evaluateSchedule(context)
    }

    fun disable(context: Context) {
        if (!isEnabled(context)) return
        context.sharedPreferences().edit { putBoolean(PREF_SCHEDULER_ENABLED, false) }
        runAsScheduled = false
        WorkManager.getInstance(context.applicationContext).cancelUniqueWork(WORK_NAME)
        closeNightScreen()
    }

    fun setFrom(context: Context, hour: Int, minute: Int) {
        context.sharedPreferences().edit {
            putInt(PREF_SCHEDULE_FROM_HOUR, hour)
            putInt(PREF_SCHEDULE_FROM_MINUTE, minute)
        }
    }

    fun setTo(context: Context, hour: Int, minute: Int) {
        context.sharedPreferences().edit {
            putInt(PREF_SCHEDULE_TO_HOUR, hour)
            putInt(PREF_SCHEDULE_TO_MINUTE, minute)
        }
    }

    fun getCalendarForStart(context: Context): Calendar {
        val prefs = context.sharedPreferences()
        val hour = prefs.getInt(PREF_SCHEDULE_FROM_HOUR, 20)
        val minute = prefs.getInt(PREF_SCHEDULE_FROM_MINUTE, 0)
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            clear(Calendar.SECOND)
        }
    }

    fun getCalendarForEnd(context: Context): Calendar {
        val prefs = context.sharedPreferences()
        val hour = prefs.getInt(PREF_SCHEDULE_TO_HOUR, 6)
        val minute = prefs.getInt(PREF_SCHEDULE_TO_MINUTE, 0)
        return Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            clear(Calendar.SECOND)
            if (timeInMillis < getCalendarForStart(context).timeInMillis) {
                add(Calendar.DATE, 1)
            }
        }
    }

    fun evaluateSchedule(context: Context) {
        if (!runAsScheduled) return
        val start = getCalendarForStart(context)
        val end = getCalendarForEnd(context)
        val now = Calendar.getInstance()
        if (now.timeInMillis in start.timeInMillis..end.timeInMillis) {
            showNightScreen()
        } else {
            closeNightScreen()
        }
    }

    private fun scheduleWork(context: Context) {
        val request = PeriodicWorkRequestBuilder<SchedulerWorker>(1, TimeUnit.HOURS).build()
        WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }
}
