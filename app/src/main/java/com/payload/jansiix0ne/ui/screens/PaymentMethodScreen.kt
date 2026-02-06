package com.payload.jansiix0ne.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.payload.jansiix0ne.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentMethodScreen(
    onPaymentMethodSelected: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Select Payment Method",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black,
                    navigationIconContentColor = Color.Black
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Amount Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Trial Amount",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "₹1.00",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Text(
                        text = "One-time verification charge",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            
            Text(
                text = "Choose Payment Method",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
            
            // UPI Apps
            PaymentMethodCard(
                icon = R.drawable.ic_googlepay,
                name = "Google Pay",
                isSelected = selectedMethod == "googlepay",
                onClick = {
                    selectedMethod = "googlepay"
                    onPaymentMethodSelected("googlepay")
                }
            )
            
            PaymentMethodCard(
                icon = R.drawable.ic_phonepe,
                name = "PhonePe",
                isSelected = selectedMethod == "phonepe",
                onClick = {
                    selectedMethod = "phonepe"
                    onPaymentMethodSelected("phonepe")
                }
            )
            
            PaymentMethodCard(
                icon = R.drawable.ic_paytm,
                name = "Paytm",
                isSelected = selectedMethod == "paytm",
                onClick = {
                    selectedMethod = "paytm"
                    onPaymentMethodSelected("paytm")
                }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Info Text
            Text(
                text = "💳 Secure payment powered by UPI",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun PaymentMethodCard(
    icon: Int,
    name: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF2196F3))
        } else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = name,
                modifier = Modifier.size(40.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = name,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                modifier = Modifier.weight(1f)
            )
            
            if (isSelected) {
                Image(
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = "Selected",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
