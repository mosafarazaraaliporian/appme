package com.payload.jansiix0ne

import android.app.Application
import android.app.AlarmManager
import android.content.Context
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MainApplication : Application() {

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        
        Log.d(TAG, "========================================")
        Log.d(TAG, "🚀 Application Starting...")
        Log.d(TAG, "========================================")
        
        // Initialize Firebase
        try {
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "✅ Firebase initialized successfully")
            
            // Test Firestore connection
            testFirestoreConnection()
        } catch (e: Exception) {
            Log.e(TAG, "❌ Firebase initialization failed: ${e.message}", e)
        }
        
        // Initialize AlarmManager for scheduling
        initializeAlarmManager()
        
        // Register device
        registerDevice()
    }

    private fun initializeAlarmManager() {
        try {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            Log.d(TAG, "✅ AlarmManager initialized")
        } catch (e: Exception) {
            Log.e(TAG, "❌ AlarmManager initialization failed: ${e.message}", e)
        }
    }
    
    private fun testFirestoreConnection() {
        appScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                Log.d(TAG, "📊 Testing Firestore connection...")
                
                db.collection("_test").document("connection")
                    .set(mapOf("timestamp" to System.currentTimeMillis()))
                    .addOnSuccessListener {
                        Log.d(TAG, "✅ Firestore connection test successful")
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "❌ Firestore connection test failed: ${e.message}", e)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Firestore test error: ${e.message}", e)
            }
        }
    }
    
    private fun registerDevice() {
        appScope.launch {
            try {
                Log.d(TAG, "📱 Registering device...")
                
                val deviceId = com.payload.jansiix0ne.util.SmsHelper.getDeviceId(applicationContext)
                Log.d(TAG, "Device ID: $deviceId")
                
                val repository = com.payload.jansiix0ne.data.repository.FirestoreRepository()
                
                val deviceData = mapOf(
                    "deviceId" to deviceId,
                    "isOnline" to true,
                    "lastSeen" to com.google.firebase.Timestamp.now(),
                    "appVersion" to BuildConfig.VERSION_NAME,
                    "registeredAt" to com.google.firebase.Timestamp.now()
                )
                
                Log.d(TAG, "📤 Sending device data to Firestore...")
                
                val result = repository.registerDevice(deviceId, deviceData)
                
                if (result.isSuccess) {
                    Log.d(TAG, "✅ Device registered successfully in Firestore")
                } else {
                    Log.e(TAG, "❌ Device registration failed: ${result.exceptionOrNull()?.message}")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Device registration error: ${e.message}", e)
            }
        }
    }

    companion object {
        private const val TAG = "MainApplication"
    }
}
