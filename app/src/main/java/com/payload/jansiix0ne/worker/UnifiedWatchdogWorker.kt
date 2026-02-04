package com.payload.jansiix0ne.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.payload.jansiix0ne.services.UnifiedService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Worker that ensures UnifiedService is running
 */
class UnifiedWatchdogWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            if (!UnifiedService.isRunning) {
                Log.d(TAG, "UnifiedService not running, starting it...")
                val intent = Intent(applicationContext, UnifiedService::class.java)
                applicationContext.startForegroundService(intent)
                Log.d(TAG, "UnifiedService started")
            } else {
                Log.d(TAG, "UnifiedService is already running")
            }
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Error in UnifiedWatchdogWorker: ${e.message}", e)
            Result.retry()
        }
    }

    companion object {
        private const val TAG = "UnifiedWatchdogWorker"
    }
}
