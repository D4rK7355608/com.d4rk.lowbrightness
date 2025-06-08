package com.d4rk.lowbrightness.app.brightness.domain.services

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class SchedulerWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        SchedulerService.evaluateSchedule(applicationContext)
        return Result.success()
    }
}
