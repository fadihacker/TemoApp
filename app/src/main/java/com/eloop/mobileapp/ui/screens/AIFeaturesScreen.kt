package com.eloop.mobileapp.ui.screens

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SmartToy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*

@Composable
fun AIFeaturesScreen(navController: NavController) {
    val features = listOf(
        AIFeature("🔍", "Smart Device Recognition", "AI scans and identifies brand, model, and specs instantly"),
        AIFeature("💰", "Instant Value Estimation", "Analyzes market data for best real-time valuation"),
        AIFeature("🌱", "Eco-Impact Prediction", "Calculates CO₂ savings before you even recycle"),
    )
    val steps = listOf(
        "Scan your device" to "Point camera at device or enter IMEI manually",
        "AI analyzes data" to "Model, condition, age and market price are analyzed",
        "Get valuation" to "Estimated price appears in seconds",
        "Choose your action" to "Sell, recycle or get a discount voucher",
    )
    val compatibleDevices = listOf("iPhone", "Samsung", "MacBook", "Dell", "iPad", "Google Pixel", "Apple Watch")

    val infiniteTransition = rememberInfiniteTransition(label = "HeaderGradient")
    
    // Animated Colors for Gradient
    val color1 by infiniteTransition.animateColor(
        initialValue = Color(0xFF1A2F18),
        targetValue = Color(0xFF0D2010),
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "C1"
    )
    val color2 by infiniteTransition.animateColor(
        initialValue = Color(0xFF0D2010),
        targetValue = Color(0xFF1A3020),
        animationSpec = infiniteRepeatable(tween(2000), RepeatMode.Reverse),
        label = "C2"
    )

    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(tween(1500, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "Pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(title = "AI Features", onBack = { navController.popBackStack() })

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.SpacingLg)
                        .clip(RoundedCornerShape(Dimens.RadiusLg))
                        .background(Brush.verticalGradient(listOf(color1, color2)))
                        .padding(Dimens.SpacingLg),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .scale(scale)
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Rounded.SmartToy,
                                contentDescription = null,
                                tint = PrimaryGreen,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(Dimens.SpacingMd))
                        Text(
                            "Smart AI Detection",
                            color = Color.White,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            CountUpText(
                                targetValue = 4250,
                                style = MaterialTheme.typography.titleMedium.copy(color = PrimaryGreen, fontWeight = FontWeight.Bold),
                                suffix = ""
                            )
                            Text(
                                " devices valued this month",
                                color = Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = Dimens.SpacingLg, vertical = Dimens.SpacingMd)) {
                    Text("Compatible Devices", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    androidx.compose.foundation.lazy.LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(compatibleDevices) { device ->
                            Surface(
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ) {
                                Text(device, modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }

            items(features) { feature ->
                ELoopCard(modifier = Modifier.padding(horizontal = Dimens.SpacingLg, vertical = 4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(Dimens.RadiusMd))
                                .background(PrimaryGreen.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(feature.emoji, fontSize = 24.sp)
                        }
                        Spacer(modifier = Modifier.width(Dimens.SpacingMd))
                        Column {
                            Text(feature.title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(feature.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                Text(
                    "How It Works",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = Dimens.SpacingLg, vertical = Dimens.SpacingMd)
                )
            }

            items(steps.size) { index ->
                val (title, desc) = steps[index]
                Row(
                    modifier = Modifier
                        .padding(horizontal = Dimens.SpacingLg)
                        .height(IntrinsicSize.Min)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.secondary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text((index + 1).toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        if (index < steps.size - 1) {
                            androidx.compose.foundation.Canvas(modifier = Modifier.weight(1f).width(2.dp)) {
                                drawLine(
                                    color = Color.Gray.copy(alpha = 0.3f),
                                    start = androidx.compose.ui.geometry.Offset(0f, 4f),
                                    end = androidx.compose.ui.geometry.Offset(0f, size.height - 4f),
                                    strokeWidth = 2f,
                                    pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(Dimens.SpacingMd))
                    Column(modifier = Modifier.padding(bottom = Dimens.SpacingLg)) {
                        Text(title, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(desc, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item {
                var showButton by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) {
                    delay(500)
                    showButton = true
                }
                androidx.compose.animation.AnimatedVisibility(
                    visible = showButton,
                    enter = androidx.compose.animation.slideInVertically(initialOffsetY = { it }) + androidx.compose.animation.fadeIn()
                ) {
                    ELoopButton(
                        text = "🤖 Try AI Valuing Now",
                        onClick = { navController.navigate("scan") },
                        modifier = Modifier.padding(horizontal = Dimens.SpacingLg)
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(Dimens.SpacingXl)) }
        }
    }
}

data class AIFeature(val emoji: String, val title: String, val description: String)


