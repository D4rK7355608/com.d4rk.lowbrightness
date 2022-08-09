package com.d4rk.lowbrightness.helpers;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import com.d4rk.lowbrightness.base.Application;
public class RequestDrawOverAppsPermission {
    private final Activity activity;
    public RequestDrawOverAppsPermission(Activity activity) {
        this.activity = activity;
    }
    private final static int REQUEST_CODE = 5463;
    public boolean requestCodeMatches(int requestCode) {
        return REQUEST_CODE == requestCode;
    }
    public boolean canDrawOverlays() {
        return Application.canDrawOverlay(activity.getBaseContext());
    }
    public void requestPermissionDrawOverOtherApps() {
        final Context context = activity.getBaseContext();
        if (!Settings.canDrawOverlays(context)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
            activity.startActivityForResult(intent, REQUEST_CODE);
        }
    }
}