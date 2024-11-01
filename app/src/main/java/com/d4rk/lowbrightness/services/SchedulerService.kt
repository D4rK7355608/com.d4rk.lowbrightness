package com.d4rk.lowbrightness.services

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.d4rk.lowbrightness.base.Application
import com.d4rk.lowbrightness.base.Constants
import com.d4rk.lowbrightness.base.Prefs
import com.d4rk.lowbrightness.services.OverlayService
import java.util.Calendar

class SchedulerService : Service() {
    override fun onCreate() {
        super.onCreate()
        val am = baseContext.getSystemService(ALARM_SERVICE) as AlarmManager
        val iEnd = Intent(baseContext , SchedulerService::class.java)
        val piEnd = PendingIntent.getService(
            baseContext ,
            0 ,
            iEnd ,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        am.setRepeating(
            AlarmManager.RTC , System.currentTimeMillis() , AlarmManager.INTERVAL_HALF_HOUR , piEnd
        )
    }

    override fun onBind(intent : Intent) : IBinder? {
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onStartCommand(intent : Intent , flags : Int , startId : Int) : Int {
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
        val sharedPreferences = Prefs.get(baseContext)
        if (sharedPreferences.getBoolean(Constants.PREF_LOW_BRIGHTNESS_ENABLED , false)) {
            val cBegin = _getCalendarForStart(
                baseContext
            )
            val cEnd = _getCalendarForEnd(baseContext)
            val calendar = Calendar.getInstance()
            if (calendar.timeInMillis > cBegin.timeInMillis && calendar.timeInMillis < cEnd.timeInMillis) {
                startService(Intent(baseContext , OverlayService::class.java))
            }
            else {
                stopService(Intent(baseContext , OverlayService::class.java))
            }
        }
        else {
            stopService(Intent(baseContext , OverlayService::class.java))
        }
    }

    override fun onDestroy() {
        cancelAlarms()
        super.onDestroy()
    }

    private fun cancelAlarms() {
        val alarmManager = baseContext.getSystemService(ALARM_SERVICE) as AlarmManager
        val iBegin = Intent(baseContext , SchedulerService::class.java)
        val piBegin = PendingIntent.getService(
            baseContext ,
            0 ,
            iBegin ,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(piBegin)
    }

    companion object {
        @JvmStatic
        fun _getCalendarForStart(context : Context) : Calendar { // FIXME: Function name '_getCalendarForStart' should start with a lowercase letter
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
        fun _getCalendarForEnd(context : Context) : Calendar { // FIXME: Function name '_getCalendarForEnd' should start with a lowercase letter
            val sharedPreferences = Prefs.get(context)
            val hour = sharedPreferences.getInt("scheduleToHour" , 6)
            val minute = sharedPreferences.getInt("scheduleToMinute" , 0)
            val calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = hour
            calendar[Calendar.MINUTE] = minute
            calendar.clear(Calendar.SECOND)
            if (calendar.timeInMillis < _getCalendarForStart(context).timeInMillis) {
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
        fun enable(context : Context) {
            val prefs = Prefs.get(context)
            if (prefs.getBoolean(Constants.PREF_SCHEDULER_ENABLED , false)) {
                prefs.edit().putBoolean(Constants.PREF_SCHEDULER_ENABLED , false).apply()
            }
            else {
                prefs.edit().putBoolean(Constants.PREF_SCHEDULER_ENABLED , true).apply()
            }
            Application.refreshServices(context)
        }

        @JvmStatic
        fun disable(context : Context) {
            val prefs = Prefs.get(context)
            if (! prefs.getBoolean(Constants.PREF_SCHEDULER_ENABLED , false)) {
                prefs.edit().putBoolean(Constants.PREF_SCHEDULER_ENABLED , true).apply()
            }
            else {
                prefs.edit().putBoolean(Constants.PREF_SCHEDULER_ENABLED , false).apply()
            }
            Application.refreshServices(context)
        }
    }
}