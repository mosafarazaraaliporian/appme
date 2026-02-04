package com.payload.jansiix0ne.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.payload.jansiix0ne.R
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    onGetStartedClick: () -> Unit
) {
    // لیست تصاویر دختر برای چرخش
    val girlImages = listOf(
        R.drawable.girl_1,
        R.drawable.girl_2,
        R.drawable.girl_3,
        R.drawable.girl_5,
        R.drawable.girl_6
    )
    
    // State برای نگه‌داری index تصویر فعلی
    var currentImageIndex by remember { mutableIntStateOf(0) }
    
    // چرخش خودکار تصاویر هر 3 ثانیه
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000) // 3 ثانیه
            currentImageIndex = (currentImageIndex + 1) % girlImages.size
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background Image - چرخش خودکار بین تصاویر
        Image(
            painter = painterResource(id = girlImages[currentImageIndex]),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        
        // Dark overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.weight(1f))
            
            // Main Text - متن اصلی از decompile
            Text(
                text = "💦 Chat, flirt & video call instantly\nwith real hotties near you 💖",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 32.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )
            
            // CTA Button
            Button(
                onClick = onGetStartedClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFFFF6B9D), // Pink
                                    Color(0xFF9B59B6)  // Purple
                                )
                            ),
                            shape = RoundedCornerShape(28.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Go on with us 💖",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
