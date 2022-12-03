package com.d4rk.lowbrightness.base;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import androidx.multidex.MultiDexApplication;
import com.d4rk.lowbrightness.services.OverlayService;
import com.d4rk.lowbrightness.services.SchedulerService;
public class Application extends MultiDexApplication {
    static public boolean canDrawOverlay(Context context) {
        return Settings.canDrawOverlays(context);
    }
    static public void refreshServices(Context context) {
        if (OverlayService.isEnabled(context)) {
            if (SchedulerService.isEnabled(context)) {
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