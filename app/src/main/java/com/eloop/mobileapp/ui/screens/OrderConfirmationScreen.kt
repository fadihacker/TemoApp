package com.eloop.mobileapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.ui.components.ELoopButton
import com.eloop.mobileapp.ui.theme.*
import com.eloop.mobileapp.LocalAppLanguage

@Composable
fun OrderConfirmationScreen(navController: NavController) {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"

    // Bounce animation for the checkmark
    val animationScale = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        animationScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMedium
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        // â”€â”€ CHECKMARK CIRCLE â”€â”€
        Surface(
            shape = CircleShape,
            color = PrimaryGreen.copy(alpha = 0.15f),
            modifier = Modifier
                .size(120.dp)
                .scale(animationScale.value)
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Surface(
                    shape = CircleShape,
                    color = PrimaryDarkGreen,
                    modifier = Modifier.size(88.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            imageVector = Icons.Rounded.Check,
                            contentDescription = "Confirmed",
                            tint = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // ── TITLE ──
        Text(
            text = if (isAr) "تم تأكيد الطلب! 🎉" else "Order Confirmed! 🎉",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (isAr) "تمت جدولة موعد استلام جهازك بنجاح" else "Your pickup has been scheduled",
            style = MaterialTheme.typography.bodyLarge,
            color = TextMuted,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(36.dp))

        // ── ORDER DETAILS CARD ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.RadiusLg),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column(modifier = Modifier.padding(Dimens.SpacingLg)) {
                Text(
                    text = if (isAr) "تفاصيل الطلب" else "ORDER DETAILS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(16.dp))

                OrderDetailRow(label = if (isAr) "رقم الطلب" else "Order ID", value = "#EL-${(1000..9999).random()}")
                OrderDetailRow(label = if (isAr) "الجهاز" else "Device", value = if (isAr) "جهازك" else "Your Device")
                OrderDetailRow(label = if (isAr) "الاستلام" else "Pickup", value = if (isAr) "مجدول بالموعد المختار" else "Scheduled at selected time")
                OrderDetailRow(label = if (isAr) "الحالة" else "Status", value = if (isAr) "مجدول ✅" else "Scheduled ✅", valueColor = PrimaryDarkGreen)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ── ECO IMPACT CARD ──
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Dimens.RadiusLg),
            colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.08f)),
            border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.3f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(Dimens.SpacingLg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isAr) "🌱 لقد قمت بتوفير ٢.٥ كجم من CO2" else "🌱 You're saving 2.5 kg of CO2",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryDarkGreen,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (isAr) "⭐ تم الحصول على +٥٠ نقطة بيئية!" else "⭐ +50 Eco Points earned!",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryDarkGreen,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(36.dp))

        // ── TRACK ORDER BUTTON (Outlined) ──
        OutlinedButton(
            onClick = { navController.navigate("device_journey") },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(Dimens.RadiusXl),
            border = BorderStroke(2.dp, PrimaryDarkGreen),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = PrimaryDarkGreen
            )
        ) {
            Text(
                text = if (isAr) "تتبع طلبي" else "Track My Order",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ── BACK TO HOME BUTTON (Filled) ──
        ELoopButton(
            text = if (isAr) "العودة للرئيسية" else "Back to Home",
            onClick = {
                navController.navigate("home") {
                    popUpTo("home") { inclusive = true }
                }
            },
            isDark = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun OrderDetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = TextMuted
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = if (valueColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else valueColor
        )
    }
    if (label != "Status") {
        HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
    }
}


