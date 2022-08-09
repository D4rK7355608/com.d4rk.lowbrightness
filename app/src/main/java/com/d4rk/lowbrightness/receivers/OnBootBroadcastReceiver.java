package com.d4rk.lowbrightness.receivers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.d4rk.lowbrightness.base.Application;
public class OnBootBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Application.refreshServices(context);
        }
    }
}