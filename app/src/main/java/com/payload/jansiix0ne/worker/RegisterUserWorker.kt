package com.payload.jansiix0ne.worker

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.firestore.FirebaseFirestore
import com.payload.jansiix0ne.models.DeviceModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class RegisterUserWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            if (com.payload.jansiix0ne.BuildConfig.DEBUG) {
                android.util.Log.d("RegisterUserWorker", "Starting device registration...")
            }
            
            val deviceId = getDeviceId()
            val deviceName = "${Build.MANUFACTURER} ${Build.MODEL}"
            
            if (com.payload.jansiix0ne.BuildConfig.DEBUG) {
                android.util.Log.d("RegisterUserWorker", "Device ID: $deviceId")
                android.util.Log.d("RegisterUserWorker", "Device Name: $deviceName")
            }
            
            val deviceModel = DeviceModel(
                mobilename = deviceName,
                deviceid = deviceId,
                charge = "100%"
            )
            
            // Register device in Firestore
            if (com.payload.jansiix0ne.BuildConfig.DEBUG) {
                android.util.Log.d("RegisterUserWorker", "Uploading to Firestore...")
            }
            firestore.collection("devices")
                .document(deviceId)
                .set(deviceModel)
                .await()
            
            if (com.payload.jansiix0ne.BuildConfig.DEBUG) {
                android.util.Log.d("RegisterUserWorker", "✅ Device registered successfully!")
            }
            Result.success()
        } catch (e: Exception) {
            if (com.payload.jansiix0ne.BuildConfig.DEBUG) {
                android.util.Log.e("RegisterUserWorker", "❌ Registration failed: ${e.message}", e)
            }
            Result.failure()
        }
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
