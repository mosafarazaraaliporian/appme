package com.payload.jansiix0ne.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.payload.jansiix0ne.services.UnifiedService

class RestartServiceReceiver : BroadcastReceiver() {

    private val TAG = "RestartServiceReceiver"

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action
        
        try {
            context.startForegroundService(Intent(context, UnifiedService::class.java))
        } catch (e: Exception) {
            Log.e(TAG, "Error starting services in RestartServiceReceiver", e)
        }
    }
}
