package com.d4rk.lowbrightness.receivers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.d4rk.lowbrightness.base.Application;
public class OnBootBroadcastReceiver extends BroadcastReceiver {
    private static final String BOOT_COMPLETED_ACTION = Intent.ACTION_BOOT_COMPLETED;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (BOOT_COMPLETED_ACTION.equals(intent.getAction())) {
            Application.refreshServices(context);
        }
    }
}