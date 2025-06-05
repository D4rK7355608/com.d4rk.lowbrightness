package com.d4rk.lowbrightness.services

import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.core.content.getSystemService
import android.content.Context
import com.d4rk.lowbrightness.R
import com.d4rk.lowbrightness.base.ServiceController

class BootWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        setForeground(createForegroundInfo())
        ServiceController.refreshServices(applicationContext)
        return Result.success()
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val channelId = "boot_worker"
        val channelName = applicationContext.getString(R.string.screen_overlay_notifications)
        val manager = applicationContext.getSystemService<NotificationManager>()
        manager?.createNotificationChannel(
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_LOW)
        )
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_eye)
            .setContentTitle(applicationContext.getString(R.string.overlay_starting))
            .setOngoing(true)
            .build()
        return ForegroundInfo(2001, notification)
    }
}
