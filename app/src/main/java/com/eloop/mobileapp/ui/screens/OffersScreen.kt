package com.eloop.mobileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*

data class Offer(
    val title: String,
    val subtitle: String,
    val discount: String,
    val icon: ImageVector,
    val color: Color
)

val sampleOffers = listOf(
    Offer("iPhone 14 Pro Max", "64GB+ in Good Condition", "Up to EGP 18,000", Icons.Rounded.Smartphone, Color(0xFF2E7D32)),
    Offer("Samsung Galaxy S23", "Any Storage – Fair Condition", "Up to EGP 12,500", Icons.Rounded.PhoneAndroid, Color(0xFF1565C0)),
    Offer("MacBook Pro M2", "Working – Any Condition", "Up to EGP 25,000", Icons.Rounded.Laptop, Color(0xFF4A148C)),
    Offer("iPad Pro 2022", "Wi-Fi or Cellular", "Up to EGP 10,000", Icons.Rounded.TabletMac, Color(0xFFBF360C)),
    Offer("HP / Dell Laptop", "Core i5 or better", "Up to EGP 8,000", Icons.Rounded.Computer, Color(0xFF004D40)),
    Offer("Airpods / Earbuds", "Any Brand – Functional", "Up to EGP 2,000", Icons.Rounded.Headphones, Color(0xFF37474F)),
)

@Composable
fun OffersScreen(navController: NavController) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PrimaryDark, MaterialTheme.colorScheme.primary)
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Text(
                            "Special Offers 🎁",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Best prices for your e-waste today",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 48.dp)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Today's Top Prices",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        items(sampleOffers) { offer ->
            OfferCard(offer) { navController.navigate("scan") }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Info,
                        contentDescription = null,
                        tint = PrimaryDark,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "Prices shown are estimates. Final price confirmed after physical inspection.",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimaryDark
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun OfferCard(offer: Offer, onScanClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(offer.color.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = offer.icon,
                    contentDescription = null,
                    tint = offer.color,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(offer.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(offer.subtitle, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    offer.discount,
                    color = PrimaryDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onScanClick,
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
            ) {
                Text("Sell", fontSize = 12.sp)
            }
        }
    }
}


