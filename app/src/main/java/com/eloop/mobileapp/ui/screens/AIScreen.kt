package com.eloop.mobileapp.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*
import com.eloop.mobileapp.data.FirebaseRepository
import com.eloop.mobileapp.LocalAppLanguage
import android.util.Log
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIScreen(
    navController: NavController,
    category: String = "Smartphone",
    priceRange: String = "EGP 8,000 – 25,000",
    co2Saved: Float = 4.2f,
    ecoPoints: Int = 820
) {
    val currentAppLanguage = LocalAppLanguage.current
    val isArabic = currentAppLanguage == "AR"
    val context = LocalContext.current

    // Save to scan history on first composition
    LaunchedEffect(Unit) {
        if (FirebaseRepository.isUserLoggedIn()) {
            try {
                FirebaseRepository.saveScanResult(
                    deviceName = category,
                    price = priceRange,
                    category = category
                )
            } catch (e: Exception) {
                Log.e("AIScreen", "Failed to save scan result: ${e.message}")
            }
        }
    }

    // ── Labels (bilingual) ─────────────────────────────────────────────────
    val labelValuationTitle  = if (isArabic) "تم تقييم الجهاز\nبنجاح! ⚡"   else "AI Valuation\nComplete! ⚡"
    val labelDeviceType      = if (isArabic) "نوع الجهاز"                   else "Device Type"
    val labelEstimatedValue  = if (isArabic) "السعر التقديري النهائي 💰"        else "Estimated Final Price 💰"
    val labelCo2             = if (isArabic) "CO₂ الموفّر"                  else "CO₂ Saved"
    val labelEcoPoints       = if (isArabic) "النقاط البيئية"               else "Eco Points"
    val labelEarnPoints      = if (isArabic) "اربح "                        else "Earn "
    val labelGreenPoints     = if (isArabic) " نقطة خضراء! ♻️"              else " Green Points! ♻️"
    val labelSimilarDevices  = if (isArabic) "أجهزة مشابهة تم بيعها مؤخراً" else "Similar Devices Sold Recently"
    val btnSellNow           = if (isArabic) "بيع الآن"                     else "Sell Now"
    val btnCancel            = if (isArabic) "إلغاء"                        else "Cancel"

    val similarDevices = listOf(
        SimilarDevice("iPhone 12",     "EGP 12,200", "2 days ago"),
        SimilarDevice("iPhone 12 Pro", "EGP 18,800", "5 days ago"),
        SimilarDevice("iPhone 11",     "EGP 9,900",  "1 week ago")
    )

    // ── UI ─────────────────────────────────────────────────────────────────
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    Brush.linearGradient(
                        colors = listOf(PrimaryDark, MaterialTheme.colorScheme.primary)
                    )
                )
                .padding(24.dp)
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            Column(modifier = Modifier.align(Alignment.CenterStart)) {
                Text(
                    labelValuationTitle,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .offset(y = (-40).dp)
        ) {
            // ── Main result card ──────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Device category
                    Text(
                        text = labelDeviceType,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = category,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDark
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(20.dp))

                    // Price range
                    Text(
                        text = labelEstimatedValue,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = priceRange,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            color = PrimaryGreen
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // CO₂ + Eco Points row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // CO₂ card
                        Surface(
                            color = PrimaryGreen.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("🌿", fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "$co2Saved kg",
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryDark,
                                    fontSize = 18.sp
                                )
                                Text(
                                    text = labelCo2,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Eco Points card
                        Surface(
                            color = Color(0xFFFFF8E1),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("⭐", fontSize = 28.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                CountUpText(
                                    targetValue = ecoPoints,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFE65100),
                                        fontSize = 18.sp
                                    )
                                )
                                Text(
                                    text = labelEcoPoints,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                navController.navigate(
                                    "schedule_pickup?name=$category&price=${priceRange.substringAfter("EGP ").substringBefore(" –")}"
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark),
                            shape = RoundedCornerShape(28.dp)
                        ) {
                            Text(btnSellNow)
                        }
                        OutlinedButton(
                            onClick = { navController.navigate("home") },
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp),
                            shape = RoundedCornerShape(28.dp),
                            border = BorderStroke(1.dp, PrimaryDark)
                        ) {
                            Text(btnCancel)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Similar devices ───────────────────────────────────────────
            Text(
                text = labelSimilarDevices,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(similarDevices) { device ->
                    Card(
                        modifier = Modifier.width(140.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(device.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(device.price, color = PrimaryGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(device.date, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ── Earn points banner ────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(labelEarnPoints, color = PrimaryDark, fontWeight = FontWeight.Bold)
                    CountUpText(
                        targetValue = ecoPoints,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = PrimaryGreen,
                            fontWeight = FontWeight.ExtraBold
                        )
                    )
                    Text(labelGreenPoints, color = PrimaryDark, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

data class SimilarDevice(val name: String, val price: String, val date: String)


