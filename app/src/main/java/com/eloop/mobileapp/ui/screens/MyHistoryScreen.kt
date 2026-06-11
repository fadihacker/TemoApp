package com.eloop.mobileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.eloop.mobileapp.data.FirebaseRepository
import com.eloop.mobileapp.data.PickupOrder
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import com.eloop.mobileapp.LocalAppLanguage

@Composable
fun MyHistoryScreen(navController: NavController) {
    var orders by remember { mutableStateOf<List<PickupOrder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        orders = FirebaseRepository.getPickupOrders(context)
        isLoading = false
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(
            title = if (isAr) "سجلي 📜" else "My History 📜", 
            onBack = { navController.popBackStack() }
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryDark)
            }
        } else if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyState(
                    icon = Icons.Rounded.History,
                    title = if (isAr) "لا يوجد سجل بعد" else "No History Yet",
                    subtitle = if (isAr) "ستظهر أجهزتك المعاد تدويرها هنا." else "Your recycled devices will appear here."
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
            ) {
                items(orders) { order ->
                    HistoryOrderCard(order)
                }
            }
        }
    }
}

@Composable
fun HistoryOrderCard(order: PickupOrder) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val dateString = dateFormat.format(Date(order.timestamp))
    
    val isCompleted = order.status.equals("completed", ignoreCase = true)
    val statusColor = if (isCompleted) PrimaryGreen else Color(0xFFFFB300)

    ELoopCard {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!order.deviceImageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = order.deviceImageUrl,
                    contentDescription = order.deviceName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.PhoneAndroid, contentDescription = null, tint = PrimaryDark)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = order.deviceName.ifEmpty { "Unknown Device" },
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = dateString,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.LocalOffer, contentDescription = null, modifier = Modifier.size(14.dp), tint = PrimaryGreen)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = order.devicePrice.ifEmpty { "Price Pending" },
                        fontWeight = FontWeight.SemiBold,
                        color = PrimaryGreen,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Eco, contentDescription = null, modifier = Modifier.size(14.dp), tint = PrimaryDark)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${order.ecoPointsEarned} pts",
                        fontSize = 12.sp,
                        color = PrimaryDark
                    )
                }
            }

            Surface(
                color = statusColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = order.status,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    color = statusColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}


