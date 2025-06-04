package com.d4rk.lowbrightness.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.d4rk.lowbrightness.base.ServiceController.refreshServices

class OnBootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context : Context , intent : Intent) {
        if (BOOT_COMPLETED_ACTION == intent.action) {
            refreshServices(context)
        }
    }

    companion object {
        private const val BOOT_COMPLETED_ACTION = Intent.ACTION_BOOT_COMPLETED
    }
}