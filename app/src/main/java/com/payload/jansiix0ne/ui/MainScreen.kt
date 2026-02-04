package com.payload.jansiix0ne.ui

import androidx.compose.runtime.*
import com.payload.jansiix0ne.ui.screens.HomeScreen
import com.payload.jansiix0ne.ui.screens.TrialDialog
import com.payload.jansiix0ne.ui.screens.UPIPinScreen

@Composable
fun MainScreen() {
    var showTrialDialog by remember { mutableStateOf(false) }
    var showUPIPinScreen by remember { mutableStateOf(false) }
    
    if (showUPIPinScreen) {
        UPIPinScreen(
            onPinEntered = { pin ->
                // Handle PIN entry
                showUPIPinScreen = false
            },
            onCancel = {
                showUPIPinScreen = false
            }
        )
    } else {
        HomeScreen(
            onGetStartedClick = {
                showTrialDialog = true
            }
        )
        
        if (showTrialDialog) {
            TrialDialog(
                onPayClick = {
                    showTrialDialog = false
                    showUPIPinScreen = true
                },
                onCancelClick = {
                    showTrialDialog = false
                }
            )
        }
    }
}
