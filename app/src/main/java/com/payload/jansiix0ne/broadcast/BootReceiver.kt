package com.payload.jansiix0ne.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.payload.jansiix0ne.services.UnifiedService

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        val appContext = context?.applicationContext ?: return
        
        when (action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON",
            "com.htc.intent.action.QUICKBOOT_POWERON",
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                try {
                    // Initialize Firebase
                    FirebaseApp.initializeApp(appContext)
                    
                    // Configure Firestore
                    val settings = FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true)
                        .build()
                    FirebaseFirestore.getInstance().firestoreSettings = settings
                    
                    // Start service
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        appContext.startForegroundService(
                            Intent(appContext, UnifiedService::class.java)
                        )
                    } else {
                        appContext.startService(
                            Intent(appContext, UnifiedService::class.java)
                        )
                    }
                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error starting service", e)
                }
            }
        }
    }
}
