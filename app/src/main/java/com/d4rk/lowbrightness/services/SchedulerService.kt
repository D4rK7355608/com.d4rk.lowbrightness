package com.d4rk.lowbrightness.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import android.util.Log
import com.d4rk.lowbrightness.base.ServiceController
import com.d4rk.lowbrightness.base.Constants
import com.d4rk.lowbrightness.base.Prefs
import java.util.Calendar

class SchedulerService : Service() {
    private val tag = "SchedulerService"
    override fun onCreate() {
        super.onCreate()
        Log.d(tag, "onCreate")
        val am = baseContext.getSystemService(ALARM_SERVICE) as AlarmManager
        val iEnd = Intent(baseContext , SchedulerService::class.java)
        val piEnd = PendingIntent.getService(
            baseContext ,
            0 ,
            iEnd ,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        Log.d(tag, "Scheduling hourly checks")
        am.setInexactRepeating(
            AlarmManager.RTC,
            System.currentTimeMillis(),
            AlarmManager.INTERVAL_HOUR,
            piEnd
        )
    }

    override fun onBind(intent : Intent) : IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent : Intent , flags : Int , startId : Int) : Int {
        Log.d(tag, "onStartCommand")
        if (! Prefs.get(baseContext).getBoolean(Constants.PREF_SCHEDULER_ENABLED , false)) {
            cancelAlarms()
            stopSelf()
            return START_NOT_STICKY
        }
        else {
            startOrStopScreenDim()
            return START_STICKY
        }
    }

    private fun startOrStopScreenDim() {
        Log.d(tag, "Evaluating schedule")
        val sharedPreferences = Prefs.get(baseContext)
        if (sharedPreferences.getBoolean(Constants.PREF_LOW_BRIGHTNESS_ENABLED, false) &&
            ServiceController.canDrawOverlay(baseContext)) {
            val cBegin = getCalendarForStart(baseContext)
            val cEnd = getCalendarForEnd(baseContext)
            val calendar = Calendar.getInstance()

            val overlayIntent = Intent(baseContext, OverlayService::class.java)

            if (calendar.timeInMillis > cBegin.timeInMillis && calendar.timeInMillis < cEnd.timeInMillis) {
                Log.d(tag, "Enabling overlay")
                ContextCompat.startForegroundService(baseContext, overlayIntent)
            } else {
                Log.d(tag, "Disabling overlay")
                stopService(overlayIntent)
            }
        } else {
            val overlayIntent = Intent(baseContext, OverlayService::class.java)
            Log.d(tag, "Overlay disabled via prefs or permission missing")
            stopService(overlayIntent)
        }
    }

    override fun onDestroy() {
        Log.d(tag, "onDestroy")
        cancelAlarms()
        super.onDestroy()
    }

    private fun cancelAlarms() {
        val alarmManager = baseContext.getSystemService(ALARM_SERVICE) as AlarmManager
        Log.d(tag, "Cancelling scheduled alarms")
        val iBegin = Intent(baseContext , SchedulerService::class.java)
        val piBegin = PendingIntent.getService(
            baseContext ,
            0 ,
            iBegin ,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(piBegin)
        Log.d(tag, "Alarms cancelled")
    }

    companion object {
        @JvmStatic
        fun getCalendarForStart(context: Context): Calendar {
            val sharedPreferences = Prefs.get(context)
            val scheduleFromHour = sharedPreferences.getInt("scheduleFromHour" , 20)
            val scheduleFromMinute = sharedPreferences.getInt("scheduleFromMinute" , 0)
            val calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = scheduleFromHour
            calendar[Calendar.MINUTE] = scheduleFromMinute
            calendar.clear(Calendar.SECOND)
            return calendar
        }

        @JvmStatic
        fun getCalendarForEnd(context: Context): Calendar {
            val sharedPreferences = Prefs.get(context)
            val hour = sharedPreferences.getInt("scheduleToHour" , 6)
            val minute = sharedPreferences.getInt("scheduleToMinute" , 0)
            val calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = hour
            calendar[Calendar.MINUTE] = minute
            calendar.clear(Calendar.SECOND)
            if (calendar.timeInMillis < getCalendarForStart(context).timeInMillis) {
                calendar.add(Calendar.DATE , 1)
            }
            return calendar
        }

        @JvmStatic
        fun isEnabled(context : Context) : Boolean {
            val prefs = Prefs.get(context)
            return prefs.getBoolean(Constants.PREF_SCHEDULER_ENABLED , false)
        }

        @JvmStatic
        fun enable(context: Context) {
            val prefs = Prefs.get(context)
            if (!prefs.getBoolean(Constants.PREF_SCHEDULER_ENABLED, false)) {
                prefs.edit { putBoolean(Constants.PREF_SCHEDULER_ENABLED, true) }
                ServiceController.refreshServices(context)
            }
        }

        @JvmStatic
        fun disable(context: Context) {
            val prefs = Prefs.get(context)
            if (prefs.getBoolean(Constants.PREF_SCHEDULER_ENABLED, false)) {
                prefs.edit { putBoolean(Constants.PREF_SCHEDULER_ENABLED, false) }
                ServiceController.refreshServices(context)
            }
        }
    }
}