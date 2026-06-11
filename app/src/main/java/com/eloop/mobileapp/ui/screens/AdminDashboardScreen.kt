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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.data.FirebaseRepository
import com.eloop.mobileapp.data.PickupOrder
import com.eloop.mobileapp.ui.components.ScreenHeader
import com.eloop.mobileapp.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(navController: NavController) {
    var orders by remember { mutableStateOf<List<PickupOrder>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        orders = FirebaseRepository.getAllOrders()
        isLoading = false
    }

    Scaffold(
        topBar = {
            ScreenHeader(title = "Admin Dashboard 🛠️", onBack = { navController.popBackStack() })
        },
        containerColor = Color(0xFFF5F7F9) // Light grey background for admin look
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stats Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                AdminStatCard("Total Orders", orders.size.toString(), Icons.Rounded.ListAlt, Modifier.weight(1f))
                AdminStatCard("Pending", orders.count { it.status == "Scheduled" }.toString(), Icons.Rounded.Pending, Modifier.weight(1f))
            }

            // Seed Data Row
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = {
                        scope.launch {
                            FirebaseRepository.seedSampleData()
                            orders = FirebaseRepository.getAllOrders()
                        }
                    }
                ) {
                    Icon(Icons.Rounded.DataUsage, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Seed Sample Data", fontSize = 12.sp)
                }
            }

            Text(
                "Recent Pickup Requests",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(orders) { order ->
                        AdminOrderCard(order) { newStatus ->
                            scope.launch {
                                FirebaseRepository.updateOrderStatus(order.id, order.userId, newStatus)
                                orders = FirebaseRepository.getAllOrders() // Refresh
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = PrimaryDark, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun AdminOrderCard(order: PickupOrder, onStatusChange: (String) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(order.userName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(order.deviceName, fontSize = 14.sp, color = Color.Gray)
                }
                Surface(
                    color = when(order.status) {
                        "Scheduled" -> Color(0xFFFFF3E0)
                        "In Transit" -> Color(0xFFE3F2FD)
                        "Completed" -> Color(0xFFE8F5E9)
                        else -> Color.LightGray
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        order.status,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = when(order.status) {
                            "Scheduled" -> Color(0xFFE65100)
                            "In Transit" -> Color(0xFF0D47A1)
                            "Completed" -> Color(0xFF1B5E20)
                            else -> Color.DarkGray
                        }
                    )
                }
            }
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.CalendarToday, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(order.pickupDate, fontSize = 12.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(16.dp))
                Icon(Icons.Rounded.AccessTime, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Text(order.pickupTime, fontSize = 12.sp, color = Color.Gray)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (order.status == "Scheduled") {
                    Button(
                        onClick = { onStatusChange("In Transit") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text("Accept", fontSize = 12.sp)
                    }
                }
                if (order.status == "In Transit") {
                    Button(
                        onClick = { onStatusChange("Completed") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
                    ) {
                        Text("Complete", fontSize = 12.sp)
                    }
                }
                OutlinedButton(
                    onClick = { /* Call User */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Call", fontSize = 12.sp)
                }
            }
        }
    }
}


