package com.d4rk.lowbrightness.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import com.d4rk.lowbrightness.services.BootWorker

class OnBootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (BOOT_COMPLETED_ACTION == intent.action) {
            val work = OneTimeWorkRequestBuilder<BootWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
            WorkManager.getInstance(context).enqueue(work)
        }
    }

    companion object {
        private const val BOOT_COMPLETED_ACTION = Intent.ACTION_BOOT_COMPLETED
    }
}