package com.eloop.mobileapp.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.eloop.mobileapp.data.FirebaseRepository
import com.eloop.mobileapp.data.PickupOrder
import com.eloop.mobileapp.ui.components.ScreenHeader
import com.eloop.mobileapp.ui.theme.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.eloop.mobileapp.LocalAppLanguage

enum class StepStatus { COMPLETED, IN_PROGRESS, PENDING }

data class JourneyStep(
    val title: String,
    val subtitle: String,
    val status: StepStatus
)

// Helper to map Firestore status to UI StepStatus
fun mapStatus(orderStatus: String, stepIndex: Int): StepStatus {
    return when (stepIndex) {
        0 -> StepStatus.COMPLETED // Placed
        1 -> if (orderStatus == "In Transit") StepStatus.IN_PROGRESS else if (orderStatus == "Scheduled") StepStatus.PENDING else StepStatus.COMPLETED
        2 -> if (orderStatus == "Processing") StepStatus.IN_PROGRESS else if (orderStatus == "Completed") StepStatus.COMPLETED else StepStatus.PENDING
        3 -> if (orderStatus == "Completed") StepStatus.COMPLETED else StepStatus.PENDING
        else -> StepStatus.PENDING
    }
}

@Composable
fun DeviceJourneyScreen(navController: NavController) {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    val context = LocalContext.current
    var orders by remember { mutableStateOf<List<PickupOrder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        orders = FirebaseRepository.getPickupOrders(context)
        isLoading = false
    }

    Scaffold(
        topBar = {
            ScreenHeader(title = if (isAr) "تاريخ طلباتك 📦" else "Your History 📦", onBack = { navController.popBackStack() })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (orders.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(if (isAr) "لا يوجد سجل حتى الآن." else "No history found yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                orders.forEach { order ->
                    Text(
                        "${order.deviceName}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    val steps = listOf(
                        JourneyStep(if (isAr) "تم تقديم الطلب" else "Order Placed", order.pickupDate, mapStatus(order.status, 0)),
                        JourneyStep(if (isAr) "جاري الاستلام" else "Collection", if (isAr) (if (order.status == "Scheduled") "مجدول" else if (order.status == "In Transit") "في الطريق" else "تم الاستلام") else order.status, mapStatus(order.status, 1)),
                        JourneyStep(if (isAr) "المعالجة" else "Processing", if (isAr) "مختبر الاستدامة" else "Sustainability Lab", mapStatus(order.status, 2)),
                        JourneyStep(if (isAr) "تم إرسال المكافآت" else "Rewards Sent", "+${order.ecoPointsEarned} " + (if (isAr) "نقطة" else "pts"), mapStatus(order.status, 3))
                    )

                    steps.forEachIndexed { index, step ->
                        JourneyTimelineItem(step = step, isLast = index == steps.size - 1)
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 32.dp))
                }
            }
        }
    }
}

@Composable
fun JourneyTimelineItem(step: JourneyStep, isLast: Boolean) {
    Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
        // Timeline Graphics Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            when (step.status) {
                StepStatus.COMPLETED -> {
                    Surface(
                        shape = CircleShape,
                        color = PrimaryGreen,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
                StepStatus.IN_PROGRESS -> {
                    // Pulsing animation
                    val infiniteTransition = rememberInfiniteTransition()
                    val scale by infiniteTransition.animateFloat(
                        initialValue = 0.8f,
                        targetValue = 1.1f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    val alpha by infiniteTransition.animateFloat(
                        initialValue = 0.5f,
                        targetValue = 0f,
                        animationSpec = infiniteRepeatable(
                            animation = tween(1000, easing = LinearOutSlowInEasing),
                            repeatMode = RepeatMode.Reverse
                        )
                    )

                    Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size((32 * scale).dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen.copy(alpha = alpha))
                        )
                        Surface(
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(2.dp, PrimaryDark),
                            modifier = Modifier.size(24.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryDark)
                                )
                            }
                        }
                    }
                }
                StepStatus.PENDING -> {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.size(24.dp)
                    ) {}
                }
            }

            if (!isLast) {
                val lineColor = if (step.status == StepStatus.COMPLETED) PrimaryGreen else MaterialTheme.colorScheme.surfaceVariant
                Canvas(
                    modifier = Modifier
                        .width(4.dp)
                        .fillMaxHeight()
                        .padding(vertical = 8.dp)
                ) {
                    drawLine(
                        color = lineColor,
                        start = Offset(size.width / 2, 0f),
                        end = Offset(size.width / 2, size.height),
                        strokeWidth = size.width
                    )
                }
            }
        }

        // Text Column
        Column(
            modifier = Modifier
                .padding(start = 16.dp, bottom = 48.dp)
                .weight(1f)
        ) {
            val titleColor = if (step.status == StepStatus.PENDING) 
                MaterialTheme.colorScheme.onSurfaceVariant 
            else MaterialTheme.colorScheme.onBackground
            
            val fontWeight = if (step.status == StepStatus.IN_PROGRESS) FontWeight.Bold else FontWeight.SemiBold

            Text(
                text = step.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = fontWeight,
                color = titleColor
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = step.subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


