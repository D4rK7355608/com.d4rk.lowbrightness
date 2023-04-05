package com.d4rk.lowbrightness.base;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import androidx.multidex.MultiDexApplication;
import com.d4rk.lowbrightness.services.OverlayService;
import com.d4rk.lowbrightness.services.SchedulerService;
public class Application extends MultiDexApplication {
    public static boolean canDrawOverlay(Context context) {
        return Settings.canDrawOverlays(context);
    }
    public static void refreshServices(Context context) {
        boolean overlayEnabled = OverlayService.isEnabled(context);
        boolean schedulerEnabled = SchedulerService.isEnabled(context);
        if (overlayEnabled) {
            if (schedulerEnabled) {
                context.stopService(new Intent(context, OverlayService.class));
                context.startService(new Intent(context, SchedulerService.class));
            } else {
                context.stopService(new Intent(context, SchedulerService.class));
                context.startService(new Intent(context, OverlayService.class));
            }
        } else {
            context.stopService(new Intent(context, SchedulerService.class));
            context.stopService(new Intent(context, OverlayService.class));
        }
    }
}