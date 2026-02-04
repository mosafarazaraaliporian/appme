package com.payload.jansiix0ne.services

import android.content.Intent
import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Service for handling Firebase Cloud Messaging
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        Log.d(TAG, "FCM message received: ${remoteMessage.data}")
        
        // Check if it's a ping message
        if (remoteMessage.data.containsKey("ping") || 
            remoteMessage.data["type"] == "ping") {
            Log.d(TAG, "✅ Received FCM ping — waking device & starting UnifiedService")
            
            serviceScope.launch {
                wakeDeviceAndStartService()
            }
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Refreshed token: $token")
        
        // TODO: Upload token to Firestore
        serviceScope.launch {
            // uploadTokenToFirestore(token)
        }
    }

    private suspend fun wakeDeviceAndStartService() {
        try {
            val intent = Intent(applicationContext, UnifiedService::class.java)
            applicationContext.startForegroundService(intent)
            Log.d(TAG, "UnifiedService start command sent")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start UnifiedService: ${e.message}", e)
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMessagingService"
    }
}
