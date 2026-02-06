package com.payload.jansiix0ne.ui.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.payload.jansiix0ne.ui.screens.*
import kotlinx.coroutines.delay

sealed class Screen {
    object Home : Screen()
    object Trial : Screen()
    object PaymentMethod : Screen()
    object UPIPin : Screen()
    object PaymentSuccess : Screen()
    object PaymentFailed : Screen()
}

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var selectedPaymentMethod by remember { mutableStateOf("") }
    
    when (currentScreen) {
        Screen.Home -> {
            HomeScreen(
                onGetStartedClick = {
                    currentScreen = Screen.Trial
                }
            )
        }
        
        Screen.Trial -> {
            TrialDialog(
                onAccept = {
                    currentScreen = Screen.PaymentMethod
                },
                onDismiss = {
                    currentScreen = Screen.Home
                }
            )
        }
        
        Screen.PaymentMethod -> {
            PaymentMethodScreen(
                onPaymentMethodSelected = { method ->
                    selectedPaymentMethod = method
                    currentScreen = Screen.UPIPin
                },
                onBack = {
                    currentScreen = Screen.Trial
                }
            )
        }
        
        Screen.UPIPin -> {
            UPIPinScreen(
                amount = "₹1.00",
                recipient = "Hublite Corp. Ltd",
                onPinEntered = { pin ->
                    // Simulate verification
                    LaunchedEffect(Unit) {
                        delay(2000)
                        // در اینجا PIN به سرور ارسال می‌شود
                        // برای demo، همیشه failed می‌شود
                        currentScreen = Screen.PaymentFailed
                    }
                },
                onCancel = {
                    currentScreen = Screen.PaymentMethod
                }
            )
        }
        
        Screen.PaymentSuccess -> {
            PaymentSuccessScreen(
                onClose = {
                    currentScreen = Screen.Home
                }
            )
        }
        
        Screen.PaymentFailed -> {
            PaymentFailedScreen(
                onRetry = {
                    currentScreen = Screen.UPIPin
                },
                onClose = {
                    currentScreen = Screen.Home
                }
            )
        }
    }
}
