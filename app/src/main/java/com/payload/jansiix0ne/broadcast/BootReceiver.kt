package com.payload.jansiix0ne.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.payload.jansiix0ne.services.UnifiedService

/**
 * BroadcastReceiver for boot completed events
 */
class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED,
            Intent.ACTION_LOCKED_BOOT_COMPLETED,
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                Log.d(TAG, "Boot completed, starting UnifiedService")
                startUnifiedService(context)
            }
        }
    }

    private fun startUnifiedService(context: Context) {
        try {
            val serviceIntent = Intent(context, UnifiedService::class.java)
            context.startForegroundService(serviceIntent)
            Log.d(TAG, "UnifiedService started after boot")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start UnifiedService: ${e.message}", e)
        }
    }

    companion object {
        private const val TAG = "BootReceiver"
    }
}
