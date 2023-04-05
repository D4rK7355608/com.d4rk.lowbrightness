package com.d4rk.lowbrightness.helpers;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import com.d4rk.lowbrightness.base.Application;
public class RequestDrawOverAppsPermission {
    private static final int REQUEST_CODE = 5463;
    private final Activity activity;
    public RequestDrawOverAppsPermission(Activity activity) {
        this.activity = activity;
    }
    public boolean requestCodeMatches(int requestCode) {
        return REQUEST_CODE == requestCode;
    }
    public boolean canDrawOverlays() {
        return Application.canDrawOverlay(activity);
    }
    public void requestPermissionDrawOverOtherApps() {
        if (!Settings.canDrawOverlays(activity)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, REQUEST_CODE);
        }
    }
}