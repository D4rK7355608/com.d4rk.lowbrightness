package com.d4rk.lowbrightness.services;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.WindowManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import com.d4rk.lowbrightness.MainActivity;
import com.d4rk.lowbrightness.R;
import com.d4rk.lowbrightness.base.Application;
import com.d4rk.lowbrightness.base.Constants;
import com.d4rk.lowbrightness.base.Prefs;
import com.d4rk.lowbrightness.ui.views.OverlayView;
@SuppressWarnings("unused")
public class OverlayService extends Service {
    private OverlayView mView;
    private static final String NOTIFICATION_CHANNEL_ID = "overlay_service";
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final SharedPreferences sp = Prefs.get(getBaseContext());
        if (!OverlayService.isEnabled(getBaseContext())) {
            stopSelf();
            return START_NOT_STICKY;
        }
        if (!Application.canDrawOverlay(getBaseContext())) {
            stopSelf();
            return START_NOT_STICKY;
        }
        int opacityPercent = sp.getInt(Constants.PREF_DIM_LEVEL, 20);
        int color = sp.getInt(Constants.PREF_OVERLAY_COLOR, Color.BLACK);
        if (mView == null) {
            mView = new OverlayView(this);
            mView.setOpacityPercent(opacityPercent);
            mView.setColor(color);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, PixelFormat.TRANSLUCENT);
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            params.horizontalMargin = 0;
            params.verticalMargin = 0;
            WindowManager wm = (WindowManager) getSystemService(WINDOW_SERVICE);
            wm.addView(mView, params);
        } else {
            mView.setOpacityPercent(opacityPercent);
            mView.setColor(color);
            mView.redraw();
        }
        showNotification();
        return START_STICKY;
    }
    private void createNotificationChannel() {
        CharSequence name = "Allow putting screen overlay";
        String description = "";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
    private void showNotification() {
        this.createNotificationChannel();
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID).setSmallIcon(R.drawable.ic_eye).setContentTitle("Blue light filter active").setContentText("Tap to edit settings or disable");
        mBuilder.setOngoing(true);
        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        mBuilder.setContentIntent(resultPendingIntent);
        startForeground(1000, mBuilder.build());
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mView != null) {
            ((WindowManager) getSystemService(WINDOW_SERVICE)).removeView(mView);
            mView = null;
        }
        stopForeground(true);
    }
    static public boolean isEnabled(Context context) {
        SharedPreferences prefs = Prefs.get(context);
        return prefs.getBoolean(Constants.PREF_LOW_BRIGHTNESS_ENABLED, false);
    }
    static public boolean enable(Context context) {
        SharedPreferences prefs = Prefs.get(context);
        boolean wasEnabledNow = true;
        if (prefs.getBoolean(Constants.PREF_LOW_BRIGHTNESS_ENABLED, false)) {
            wasEnabledNow = false;
        } else {
            prefs.edit().putBoolean(Constants.PREF_LOW_BRIGHTNESS_ENABLED, true).apply();
        }
        Application.refreshServices(context);
        return wasEnabledNow;
    }
    static public boolean disable(Context context) {
        SharedPreferences prefs = Prefs.get(context);
        boolean wasDisabledNow = true;
        if (!prefs.getBoolean(Constants.PREF_LOW_BRIGHTNESS_ENABLED, false)) {
            wasDisabledNow = false;
        } else {
            prefs.edit().putBoolean(Constants.PREF_LOW_BRIGHTNESS_ENABLED, false).apply();
        }
        Application.refreshServices(context);
        return wasDisabledNow;
    }
}