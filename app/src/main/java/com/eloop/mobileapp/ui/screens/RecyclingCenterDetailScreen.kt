package com.eloop.mobileapp.ui.screens

import androidx.core.net.toUri

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Directions
import androidx.compose.material.icons.rounded.Map
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.LocalShipping
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.data.RecyclingCentersData

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecyclingCenterDetailScreen(navController: NavController, centerId: String) {
    val center = RecyclingCentersData.centers.find { it.id == centerId }
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    if (center == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Center not found.")
            Button(onClick = { navController.popBackStack() }) {
                Text("Go Back")
            }
        }
        return
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Confirm Pickup") },
            text = { Text("Are you sure you want to book a pickup for ${center.name}?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.popBackStack()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Center Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Rounded.Map, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = center.name,
                fontSize = 32.sp,
                lineHeight = 36.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${center.area} • ${center.distance}",
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Info rows
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Schedule, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Hours: ${center.hours}", fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Accepted Waste
            Text(text = "Accepted Waste Categories", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                center.acceptedWaste.forEach { waste ->
                    val color = when (waste) {
                        "Electronics" -> Color(0xFFE8F5E9)   // Light Green
                        "Metal" -> Color(0xFFF3E5F5)         // Light Purple
                        "Plastic" -> Color(0xFFE3F2FD)       // Light Blue
                        "Paper" -> Color(0xFFFFF3E0)         // Light Orange
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                    val textColor = when (waste) {
                        "Electronics" -> Color(0xFF2E7D32)
                        "Metal" -> Color(0xFF6A1B9A)
                        "Plastic" -> Color(0xFF1565C0)
                        "Paper" -> Color(0xFFEF6C00)
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    
                    Surface(
                        color = color,
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.wrapContentSize()
                    ) {
                        Text(
                            text = waste,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = textColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Actions
            Button(
                onClick = {
                    val uri = "geo:${center.location.latitude},${center.location.longitude}?q=${center.location.latitude},${center.location.longitude}(${center.name})".toUri()
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Rounded.Directions, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Get Directions", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
            ) {
                Icon(Icons.Rounded.LocalShipping, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Book Pickup", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}



