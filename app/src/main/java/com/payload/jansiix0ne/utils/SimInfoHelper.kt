package com.payload.jansiix0ne.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.content.ContextCompat
import com.payload.jansiix0ne.models.SimModel

object SimInfoHelper {
    private const val TAG = "SimInfoHelper"

    fun getSimInfo(context: Context): SimModel {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
            != PackageManager.PERMISSION_GRANTED) {
            Log.w(TAG, "READ_PHONE_STATE permission not granted")
            return SimModel("", "")
        }
        
        val hasReadPhoneNumbers: Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_NUMBERS) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }

        try {
            val subManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as? SubscriptionManager
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

            if (subManager == null || telephonyManager == null) {
                Log.w(TAG, "TelephonyManager or SubscriptionManager not available")
                return SimModel("", "")
            }

            val sims = subManager.activeSubscriptionInfoList

            if (sims.isNullOrEmpty()) {
                Log.d(TAG, "No active SIM cards found")
                return SimModel("", "")
            }

            var sim1Number = ""
            var sim2Number = ""

            sims.forEachIndexed { index, info ->
                var phoneNumber = info.number ?: ""
                
                // Try to get phone number using TelephonyManager if empty
                if (phoneNumber.isBlank() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && hasReadPhoneNumbers) {
                    try {
                        val tm = telephonyManager.createForSubscriptionId(info.subscriptionId)
                        val line1Number = tm.line1Number
                        if (!line1Number.isNullOrBlank()) {
                            phoneNumber = line1Number
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "Failed to get line1Number for SIM ${info.simSlotIndex}: ${e.message}")
                    }
                }
                
                // Assign to sim1 or sim2 based on slot index
                when (info.simSlotIndex) {
                    0 -> sim1Number = phoneNumber
                    1 -> sim2Number = phoneNumber
                    else -> {
                        // If slot index is not 0 or 1, use order
                        if (index == 0) sim1Number = phoneNumber
                        else if (index == 1) sim2Number = phoneNumber
                    }
                }
                
                Log.d(TAG, "SIM ${info.simSlotIndex}: Carrier=${info.carrierName}, Number=${if (phoneNumber.isNotEmpty()) "***" else "empty"}")
            }
            
            return SimModel(sim1Number, sim2Number)
        } catch (e: Exception) {
            Log.e(TAG, "SIM Info error: ${e.message}", e)
            return SimModel("", "")
        }
    }
}
