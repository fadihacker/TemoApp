package com.eloop.mobileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*
import com.eloop.mobileapp.LocalAppLanguage
import com.eloop.mobileapp.data.FirebaseRepository
import com.eloop.mobileapp.data.LeaderboardEntry
import androidx.compose.runtime.*
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import kotlinx.coroutines.delay

// Moved LeaderboardEntry to FirebaseRepository.kt

@Composable
fun LeaderboardScreen(navController: NavController) {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"

    var leaderboardList by remember { mutableStateOf<List<LeaderboardEntry>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        leaderboardList = FirebaseRepository.getLeaderboard()
        isLoading = false
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = PrimaryDark)
        }
        return
    }

    val topThree = leaderboardList.take(3)
    val rest = leaderboardList.drop(3)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            // Header gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(colors = listOf(Color(0xFF5B3A1A), Color(0xFFC47F2D)))
                    )
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Text(
                            if (isAr) "الترتيب ولوحة الصدارة 🏆" else "Ranking & Leaderboard 🏆",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                    Text(
                        if (isAr) "نافس المحاربين البيئيين الآخرين" else "Compete with other eco-warriors",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 13.sp,
                        modifier = Modifier.padding(start = 48.dp)
                    )
                }
            }
        }

        item {
            // Top 3 podium
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.Bottom
                ) {
                    var showRank3 by remember { mutableStateOf(false) }
                    var showRank2 by remember { mutableStateOf(false) }
                    var showRank1 by remember { mutableStateOf(false) }

                    LaunchedEffect(Unit) {
                        showRank3 = true
                        delay(150)
                        showRank2 = true
                        delay(150)
                        showRank1 = true
                    }

                    // 2nd place
                    if (topThree.size >= 2) {
                        AnimatedVisibility(
                            visible = showRank2,
                            enter = slideInVertically { it } + fadeIn()
                        ) {
                            PodiumItem(topThree[1], podiumHeight = 80.dp)
                        }
                    }
                    // 1st place
                    if (topThree.isNotEmpty()) {
                        AnimatedVisibility(
                            visible = showRank1,
                            enter = slideInVertically(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            ) { it } + fadeIn()
                        ) {
                            PodiumItem(topThree[0], podiumHeight = 110.dp, isFirst = true)
                        }
                    }
                    // 3rd place
                    if (topThree.size >= 3) {
                        AnimatedVisibility(
                            visible = showRank3,
                            enter = slideInVertically { it } + fadeIn()
                        ) {
                            PodiumItem(topThree[2], podiumHeight = 60.dp)
                        }
                    }
                }
            }
        }

        item {
            Text(
                if (isAr) "الترتيب الكامل" else "Full Rankings",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        items(leaderboardList.size) { i ->
            var isVisible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(i * 60L)
                isVisible = true
            }
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn()
            ) {
                LeaderboardRow(leaderboardList[i])
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

@Composable
fun PodiumItem(entry: LeaderboardEntry, podiumHeight: androidx.compose.ui.unit.Dp, isFirst: Boolean = false) {
    val medal = when (entry.rank) { 1 -> "🥇"; 2 -> "🥈"; else -> "🥉" }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(medal, fontSize = if (isFirst) 28.sp else 22.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .size(if (isFirst) 52.dp else 44.dp)
                .clip(CircleShape)
                .background(entry.color),
            contentAlignment = Alignment.Center
        ) {
            Text(entry.initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = if (isFirst) 16.sp else 13.sp)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(entry.name.split(" ").first(), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text("${entry.points} " + (if (LocalAppLanguage.current == "AR") "نقطة" else "pts"), color = PrimaryDark, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .width(if (isFirst) 80.dp else 64.dp)
                .height(podiumHeight)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(entry.color.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Text("#${entry.rank}", color = Color.White, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        }
    }
}

@Composable
fun LeaderboardRow(entry: LeaderboardEntry) {
    val bgColor = if (entry.isCurrentUser) PrimaryDark.copy(alpha = 0.08f) else Color.Transparent
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = if (entry.isCurrentUser) PrimaryDark.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(if (entry.isCurrentUser) 4.dp else 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "#${entry.rank}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = if (entry.rank <= 3) entry.color else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.width(32.dp)
            )
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(entry.color),
                contentAlignment = Alignment.Center
            ) {
                Text(entry.initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                val isAr = LocalAppLanguage.current == "AR"
                Text(
                    if (entry.isCurrentUser) (if (isAr) "أنت (أنا)" else "You (Me)") else entry.name,
                    fontWeight = if (entry.isCurrentUser) FontWeight.ExtraBold else FontWeight.Normal,
                    fontSize = 14.sp
                )
                Text("${entry.devices} " + (if (isAr) "أجهزة معاد تدويرها" else "devices recycled"), fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("${entry.points}", fontWeight = FontWeight.Bold, color = PrimaryDark, fontSize = 15.sp)
                Text(if (LocalAppLanguage.current == "AR") "نقطة" else "pts", fontSize = 10.sp, color = PrimaryDark.copy(alpha = 0.7f))
            }
        }
    }
}


