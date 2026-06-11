package com.eloop.mobileapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.LocalAppLanguage
import com.eloop.mobileapp.data.FirebaseRepository
import com.eloop.mobileapp.data.UserProfile
import com.eloop.mobileapp.ui.components.ELoopCard
import com.eloop.mobileapp.ui.components.ScreenHeader
import com.eloop.mobileapp.ui.theme.PrimaryDark
import com.eloop.mobileapp.ui.theme.PrimaryGreen

data class Badge(
    val id: String,
    val nameEn: String,
    val nameAr: String,
    val descEn: String,
    val descAr: String,
    val icon: ImageVector,
    val unlockCondition: (UserProfile?) -> Boolean
)

val badgesList = listOf(
    Badge(
        id = "first_recycler",
        nameEn = "First Recycler",
        nameAr = "أول إعادة تدوير",
        descEn = "Recycle your first device",
        descAr = "قم بإعادة تدوير أول جهاز لك",
        icon = Icons.Rounded.EmojiEvents,
        unlockCondition = { profile -> (profile?.devicesRecycled ?: 0) >= 1 }
    ),
    Badge(
        id = "five_device_club",
        nameEn = "5-Device Club",
        nameAr = "نادي الـ 5 أجهزة",
        descEn = "Recycle 5 devices",
        descAr = "قم بإعادة تدوير 5 أجهزة",
        icon = Icons.Rounded.Devices,
        unlockCondition = { profile -> (profile?.devicesRecycled ?: 0) >= 5 }
    ),
    Badge(
        id = "co2_hero",
        nameEn = "CO₂ Hero",
        nameAr = "بطل الـ CO₂",
        descEn = "Save 10.0 kg of CO₂",
        descAr = "وفر 10.0 كجم من ثاني أكسيد الكربون",
        icon = Icons.Rounded.CloudOff,
        unlockCondition = { profile -> (profile?.co2Saved ?: 0.0) >= 10.0 }
    ),
    Badge(
        id = "points_pioneer",
        nameEn = "Points Pioneer",
        nameAr = "رائد النقاط",
        descEn = "Earn 500 Eco Points",
        descAr = "اكسب 500 نقطة بيئية",
        icon = Icons.Rounded.StarBorder,
        unlockCondition = { profile -> (profile?.ecoPoints ?: 0) >= 500 }
    ),
    Badge(
        id = "eco_champion",
        nameEn = "Eco Champion",
        nameAr = "بطل البيئة",
        descEn = "Earn 2000 Eco Points",
        descAr = "اكسب 2000 نقطة بيئية",
        icon = Icons.Rounded.MilitaryTech,
        unlockCondition = { profile -> (profile?.ecoPoints ?: 0) >= 2000 }
    ),
    Badge(
        id = "green_legend",
        nameEn = "Green Legend",
        nameAr = "أسطورة خضراء",
        descEn = "Recycle 20 devices",
        descAr = "قم بإعادة تدوير 20 جهاز",
        icon = Icons.Rounded.WorkspacePremium,
        unlockCondition = { profile -> (profile?.devicesRecycled ?: 0) >= 20 }
    )
)

@Composable
fun AchievementsScreen(navController: NavController) {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    val profile by FirebaseRepository.listenToUserProfile().collectAsState(initial = null)

    Scaffold(
        topBar = {
            ScreenHeader(
                title = if (isAr) "الإنجازات 🏅" else "Achievements 🏅",
                onBack = { navController.popBackStack() }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(badgesList) { badge ->
                BadgeItem(badge = badge, isUnlocked = badge.unlockCondition(profile), isAr = isAr)
            }
        }
    }
}

@Composable
fun BadgeItem(badge: Badge, isUnlocked: Boolean, isAr: Boolean) {
    var showShine by remember { mutableStateOf(false) }

    LaunchedEffect(isUnlocked) {
        if (isUnlocked) {
            showShine = true
        }
    }

    ELoopCard(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .graphicsLayer {
                alpha = if (isUnlocked) 1f else 0.35f
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(
                            if (isUnlocked) PrimaryGreen.copy(alpha = 0.2f)
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = badge.icon,
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = if (isUnlocked) PrimaryDark else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )

                    if (!isUnlocked) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = if (isAr) badge.nameAr else badge.nameEn,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    maxLines = 1,
                    fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isAr) badge.descAr else badge.descEn,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    color = if (isUnlocked) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                    maxLines = 2,
                    lineHeight = 14.sp,
                    fontSize = 11.sp
                )
            }

            // Shine effect for unlocked badges
            androidx.compose.animation.AnimatedVisibility(
                visible = isUnlocked && showShine,
                enter = fadeIn(animationSpec = tween(1500)),
                modifier = Modifier.matchParentSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color.White.copy(alpha = 0.2f), Color.Transparent),
                                radius = 150f
                            )
                        )
                )
            }
        }
    }
}


