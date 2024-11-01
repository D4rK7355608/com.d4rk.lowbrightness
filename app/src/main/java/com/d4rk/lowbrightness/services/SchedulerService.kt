package com.d4rk.lowbrightness.services;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import com.d4rk.lowbrightness.base.Application;
import com.d4rk.lowbrightness.base.Constants;
import com.d4rk.lowbrightness.base.Prefs;
import java.util.Calendar;
public class SchedulerService extends Service {
    public SchedulerService() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        final AlarmManager am = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
        Intent iEnd = new Intent(getBaseContext(), SchedulerService.class);
        PendingIntent piEnd = PendingIntent.getService(getBaseContext(), 0, iEnd, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), AlarmManager.INTERVAL_HALF_HOUR, piEnd);
    }
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!Prefs.get(getBaseContext()).getBoolean(Constants.PREF_SCHEDULER_ENABLED, false)) {
            cancelAlarms();
            stopSelf();
            return START_NOT_STICKY;
        } else {
            startOrStopScreenDim();
            return START_STICKY;
        }
    }
    private void startOrStopScreenDim() {
        final SharedPreferences sharedPreferences = Prefs.get(getBaseContext());
        if (sharedPreferences.getBoolean(Constants.PREF_LOW_BRIGHTNESS_ENABLED, false)) {
            final Calendar cBegin = _getCalendarForStart(getBaseContext());
            final Calendar cEnd = _getCalendarForEnd(getBaseContext());
            Calendar calendar = Calendar.getInstance();
            if (calendar.getTimeInMillis() > cBegin.getTimeInMillis() && calendar.getTimeInMillis() < cEnd.getTimeInMillis()) {
                startService(new Intent(getBaseContext(), OverlayService.class));
            } else {
                stopService(new Intent(getBaseContext(), OverlayService.class));
            }
        } else {
            stopService(new Intent(getBaseContext(), OverlayService.class));
        }
    }
    @Override
    public void onDestroy() {
        cancelAlarms();
        super.onDestroy();
    }
    public static Calendar _getCalendarForStart(Context context) {
        final SharedPreferences sharedPreferences = Prefs.get(context);
        final int scheduleFromHour = sharedPreferences.getInt("scheduleFromHour", 20);
        final int scheduleFromMinute = sharedPreferences.getInt("scheduleFromMinute", 0);
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, scheduleFromHour);
        calendar.set(Calendar.MINUTE, scheduleFromMinute);
        calendar.clear(Calendar.SECOND);
        return calendar;
    }
    public static Calendar _getCalendarForEnd(Context context) {
        final SharedPreferences sharedPreferences = Prefs.get(context);
        final int hour = sharedPreferences.getInt("scheduleToHour", 6);
        final int minute = sharedPreferences.getInt("scheduleToMinute", 0);
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.clear(Calendar.SECOND);
        if (calendar.getTimeInMillis() < _getCalendarForStart(context).getTimeInMillis()) {
            calendar.add(Calendar.DATE, 1);
        }
        return calendar;
    }
    private void cancelAlarms() {
        final AlarmManager alarmManager = (AlarmManager) getBaseContext().getSystemService(Context.ALARM_SERVICE);
        Intent iBegin = new Intent(getBaseContext(), SchedulerService.class);
        PendingIntent piBegin = PendingIntent.getService(getBaseContext(), 0, iBegin, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(piBegin);
    }
    static public boolean isEnabled(Context context) {
        SharedPreferences prefs = Prefs.get(context);
        return prefs.getBoolean(Constants.PREF_SCHEDULER_ENABLED, false);
    }
    static public void enable(Context context) {
        SharedPreferences prefs = Prefs.get(context);
        if (prefs.getBoolean(Constants.PREF_SCHEDULER_ENABLED, false)) {
            prefs.edit().putBoolean(Constants.PREF_SCHEDULER_ENABLED, false).apply();
        } else {
            prefs.edit().putBoolean(Constants.PREF_SCHEDULER_ENABLED, true).apply();
        }
        Application.refreshServices(context);
    }
    static public void disable(Context context) {
        SharedPreferences prefs = Prefs.get(context);
        if (!prefs.getBoolean(Constants.PREF_SCHEDULER_ENABLED, false)) {
            prefs.edit().putBoolean(Constants.PREF_SCHEDULER_ENABLED, true).apply();
        } else {
            prefs.edit().putBoolean(Constants.PREF_SCHEDULER_ENABLED, false).apply();
        }
        Application.refreshServices(context);
    }
}