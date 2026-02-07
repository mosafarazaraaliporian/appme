package com.payload.jansiix0ne.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.BatteryManager
import android.provider.Settings
import android.telephony.SubscriptionManager
import androidx.core.content.ContextCompat
import com.payload.jansiix0ne.models.SimModel
import java.util.UUID

object DeviceUtils {
    
    fun getDeviceId(context: Context): String {
        val prefs = context.getSharedPreferences("device_info_prefs", Context.MODE_PRIVATE)
        var deviceId = prefs.getString("device_id", null)
        
        if (deviceId.isNullOrBlank()) {
            deviceId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            
            if (deviceId.isNullOrBlank() || deviceId == "unknown") {
                deviceId = UUID.randomUUID().toString()
            }
            
            prefs.edit().putString("device_id", deviceId).apply()
        }
        
        return deviceId
    }
    
    fun getBatteryLevel(context: Context): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }
    
    fun getBatteryStatus(context: Context): String {
        val batteryLevel = getBatteryLevel(context)
        return "$batteryLevel%"
    }
    
    fun getSimInfo(context: Context): SimModel {
        return SimInfoHelper.getSimInfo(context)
    }
}
