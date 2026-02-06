package com.payload.jansiix0ne.worker

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.payload.jansiix0ne.services.UnifiedService

class UnifiedWatchdogWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            // Check if UnifiedService is running
            if (!UnifiedService.isRunning) {
                // Restart the service
                applicationContext.startForegroundService(
                    Intent(applicationContext, UnifiedService::class.java)
                )
            }
            
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
