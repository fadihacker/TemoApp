package com.eloop.mobileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*
import com.eloop.mobileapp.LocalAppLanguage

import com.eloop.mobileapp.data.FirebaseRepository

@Composable
fun ImpactScreen(navController: NavController) {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    
    val userProfile by FirebaseRepository.listenToUserProfile().collectAsState(initial = null)
    
    val co2Saved = userProfile?.co2Saved?.toFloat() ?: 0f
    val devicesRecycled = userProfile?.devicesRecycled ?: 0
    val treesSaved = (co2Saved / 8.0f).toInt() // Rough estimate: 8kg CO2 = 1 tree
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(title = if (isAr) "تأثيرك 🌍" else "Your Impact 🌍", onBack = { navController.popBackStack() })

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f))
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier.size(80.dp).background(PrimaryGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Public, contentDescription = null, tint = Color.White, modifier = Modifier.size(40.dp))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        CountUpText(
                            targetValue = co2Saved.toInt(),
                            style = MaterialTheme.typography.displayMedium.copy(color = PrimaryGreen, fontWeight = FontWeight.ExtraBold),
                            suffix = if (isAr) " كجم" else " kg"
                        )
                        Text(if (isAr) "إجمالي تقليل الكربون" else "Total CO2 Offset", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(if (isAr) "يساوي زراعة $treesSaved أشجار! 🌳" else "Equal to planting $treesSaved trees! 🌳", color = PrimaryGreen, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ImpactStatCard(Icons.Rounded.Opacity, if (isAr) "١٢٠ لتر" else "120L", if (isAr) "مياه محفوظة" else "Water Saved", Modifier.weight(1f))
                    ImpactStatCard(Icons.Rounded.Bolt, if (isAr) "٤٥ كيلوواط" else "45kWh", if (isAr) "طاقة محفوظة" else "Energy Saved", Modifier.weight(1f))
                }
            }

            item {
                Text(if (isAr) "التقدم البيئي" else "Environmental Progress", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    ProgressBarItem(label = if (isAr) "نفايات مُحولة من المكبات" else "Landfill Diverted", current = 12f, max = 20f, unit = if (isAr) "كجم" else "kg")
                    ProgressBarItem(label = if (isAr) "نفايات سامة تم منعها" else "Toxic Waste Prevented", current = 2.4f, max = 5f, unit = if (isAr) "كجم" else "kg")
                }
            }

            item {
                Text(if (isAr) "مجموعة الشارات البيئية" else "Eco Badges Collection", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BadgeIcon("🌱", if (isAr) "مبتدئ" else "Starter")
                    BadgeIcon("🔋", if (isAr) "ملك البطاريات" else "Battery King")
                    BadgeIcon("📱", if (isAr) "منقذ التقنية" else "Tech Saver")
                    BadgeIcon("🏆", if (isAr) "بطل البيئة" else "Eco Hero")
                }
            }

            item {
                val context = androidx.compose.ui.platform.LocalContext.current
                Button(
                    onClick = {
                        val sendIntent = android.content.Intent().apply {
                            action = android.content.Intent.ACTION_SEND
                            val shareText = if (isAr) "لقد قمت بتوفير ${co2Saved} كجم من CO2 على ELoop! انضم إلي في إعادة تدوير النفايات الإلكترونية: https://eloop.app/join" else "I just saved ${co2Saved}kg of CO2 on ELoop! Join me in recycling e-waste: https://eloop.app/join"
                            putExtra(android.content.Intent.EXTRA_TEXT, shareText)
                            type = "text/plain"
                        }
                        val shareIntent = android.content.Intent.createChooser(sendIntent, if (isAr) "شارك تأثيرك البيئي" else "Share your eco-impact")
                        context.startActivity(shareIntent)
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryDark)
                ) {
                    Icon(Icons.Rounded.Share, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isAr) "شارك تقدمك" else "Share Your Progress")
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
fun ImpactStatCard(icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = PrimaryGreen)
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun BadgeIcon(emoji: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 28.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}


