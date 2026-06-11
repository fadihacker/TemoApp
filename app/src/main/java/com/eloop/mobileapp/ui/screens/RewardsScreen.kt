package com.eloop.mobileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*
import com.eloop.mobileapp.data.FirebaseRepository
import kotlinx.coroutines.launch

data class Voucher(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val cost: Int
)

val vouchers = listOf(
    Voucher("10% Off Eco-Shop", "Valid for next purchase", Icons.Rounded.ShoppingCart, 500),
    Voucher("Free Pickup", "Avoid the trip to drop-off", Icons.Rounded.LocalShipping, 1000),
    Voucher("50 EGP Wallet", "Instant cash rewards", Icons.Rounded.AccountBalanceWallet, 1500)
)

@Composable
@Suppress("DEPRECATION")
fun RewardsScreen(navController: NavController) {
    val coroutineScope = rememberCoroutineScope()
    var userProfile by remember { mutableStateOf<com.eloop.mobileapp.data.UserProfile?>(null) }
    
    var redeemingVoucher by remember { mutableStateOf<String?>(null) }
    var successCode by remember { mutableStateOf<String?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        FirebaseRepository.listenToUserProfile().collect {
            userProfile = it
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            snackbarHostState.showSnackbar(errorMessage!!)
            errorMessage = null
        }
    }

    val transactions = listOf(
        Transaction("Phone Recycling", "+50 pts", "Oct 12, 2024", true),
        Transaction("Weekly Bonus", "+10 pts", "Oct 05, 2024", true)
    )

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScreenHeader(title = "Eco Rewards 🏆", onBack = { navController.popBackStack() })

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(PrimaryDark, Color(0xFF1B5E20))
                                )
                            )
                            .padding(24.dp)
                    ) {
                        Column {
                            Text("Current Balance", color = Color.White.copy(alpha = 0.7f))
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.Bottom) {
                                CountUpText(
                                    targetValue = userProfile?.ecoPoints ?: 0,
                                    style = MaterialTheme.typography.displaySmall.copy(color = Color.White, fontWeight = FontWeight.ExtraBold)
                                )
                                Text(" pts", color = Color.White, modifier = Modifier.padding(bottom = 8.dp))
                            }
                            Text("≈ EGP ${(userProfile?.ecoPoints ?: 0) / 10.0} Cash Value", color = Color.White.copy(alpha = 0.6f), fontSize = 12.sp)
                        }
                        Icon(
                            Icons.Rounded.MilitaryTech,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.size(120.dp).align(Alignment.CenterEnd).offset(x = 20.dp, y = 20.dp)
                        )
                    }
                }
            }

            item {
                Text("Redeem Vouchers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(vouchers) { voucher ->
                VoucherCard(
                    voucher = voucher,
                    isLoading = redeemingVoucher == voucher.title,
                    onRedeem = {
                        coroutineScope.launch {
                            redeemingVoucher = voucher.title
                            val result = FirebaseRepository.redeemVoucher(voucher.cost, voucher.title)
                            redeemingVoucher = null
                            if (result.isSuccess) {
                                successCode = result.getOrNull()
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Redemption failed"
                            }
                        }
                    }
                )
            }

            item {
                Text("Recent Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            items(transactions) { tx ->
                HistoryItem(tx)
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
    }

    if (successCode != null) {
        AlertDialog(
            onDismissRequest = { successCode = null },
            title = { Text("Success!") },
            text = { Text("Your voucher code is:\n\n$successCode\n\nKeep it safe!") },
            confirmButton = {
                TextButton(onClick = { successCode = null }) {
                    Text("OK")
                }
            }
        )
    }
}

@Composable
fun VoucherCard(voucher: Voucher, isLoading: Boolean, onRedeem: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(PrimaryGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(voucher.icon, contentDescription = null, tint = PrimaryGreen)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(voucher.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text(voucher.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Button(
                onClick = onRedeem,
                enabled = !isLoading,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("${voucher.cost} pts", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun HistoryItem(tx: Transaction) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(if (tx.isPositive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                if (tx.isPositive) Icons.Rounded.Add else Icons.Rounded.Remove,
                contentDescription = null,
                tint = if (tx.isPositive) Color(0xFF2E7D32) else Color(0xFFC62828),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(tx.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(tx.date, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(
            tx.amount,
            fontWeight = FontWeight.Bold,
            color = if (tx.isPositive) Color(0xFF2E7D32) else Color(0xFFC62828)
        )
    }
}

data class Transaction(val title: String, val amount: String, val date: String, val isPositive: Boolean)


