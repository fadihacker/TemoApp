package com.eloop.mobileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.eloop.mobileapp.data.SessionManager
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*
import com.eloop.mobileapp.data.FirebaseRepository
import com.eloop.mobileapp.data.UserProfile
import com.eloop.mobileapp.LocalAppLanguage
import com.google.firebase.auth.FirebaseAuth

@Composable
@Suppress("DEPRECATION")
fun ProfileScreen(navController: NavController) {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    
    LaunchedEffect(Unit) {
        FirebaseRepository.listenToUserProfile().collect {
            profile = it
        }
    }

    val name = profile?.name ?: (sessionManager.getUserName() ?: if (isAr) "محارب البيئة" else "Eco Warrior")
    
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            ProfileTopSection(name, navController)
        }

        item {
            ImpactSummaryGrid(isAr, profile)
        }

        item {
            InviteFriendsCard(isAr)
        }

        item {
            RecentBadgesRow(isAr)
        }

        item {
            ProfileActionsList(navController, sessionManager, isAr)
        }
    }
}

@Composable
fun ProfileTopSection(name: String, navController: NavController) {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Level Ring
            CircularProgressIndicator(
                progress = { 0.75f },
                modifier = Modifier.size(110.dp),
                color = PrimaryGreen,
                strokeWidth = 4.dp,
                trackColor = PrimaryGreen.copy(alpha = 0.1f)
            )
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(PrimaryDark),
                contentAlignment = Alignment.Center
            ) {
                Text(name.take(1).uppercase(), color = Color.White, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Bold)
            }
            // Level Badge
            Surface(
                color = AccentYellow,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = 10.dp)
            ) {
                Text("Lv. 4", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text(name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(if (isAr) "نشط منذ أكتوبر ٢٠٢٤" else "Active since October 2024", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = { navController.navigate("account_settings") },
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(if (isAr) "تعديل الملف الشخصي ✏️" else "Edit Profile ✏️")
        }
    }
}

@Composable
fun ImpactSummaryGrid(isAr: Boolean, profile: UserProfile?) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        ProfileStatCard(profile?.ecoPoints?.toString() ?: "0", if (isAr) "نقطة" else "Points", Modifier.weight(1f))
        ProfileStatCard(profile?.devicesRecycled?.toString() ?: "0", if (isAr) "عنصر" else "Items", Modifier.weight(1f))
        ProfileStatCard("${profile?.co2Saved ?: 0.0}kg", if (isAr) "توفير CO2" else "CO2 Saved", Modifier.weight(1f))
    }
}

@Composable
fun ProfileStatCard(value: String, label: String, modifier: Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Bold, color = PrimaryGreen, fontSize = 18.sp)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun InviteFriendsCard(isAr: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(if (isAr) "ادعُ الأصدقاء" else "Invite Friends", fontWeight = FontWeight.Bold)
                Text(if (isAr) "اربح ٥٠ نقطة لكل إحالة" else "Earn 50 pts for each referral", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(12.dp))
                Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(8.dp)) {
                    Text("ELOOP2024", modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp), fontWeight = FontWeight.Bold, letterSpacing = 2.sp, color = MaterialTheme.colorScheme.onSurface)
                }
            }
            Icon(Icons.Rounded.Share, contentDescription = null, tint = PrimaryGreen)
        }
    }
}

@Composable
fun RecentBadgesRow(isAr: Boolean) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Text(if (isAr) "الإنجازات" else "Achievements", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) {
                Box(modifier = Modifier.size(50.dp).background(MaterialTheme.colorScheme.surfaceVariant, CircleShape), contentAlignment = Alignment.Center) {
                    Text("🌟")
                }
            }
        }
    }
}

@Composable
fun ProfileActionsList(navController: NavController, sessionManager: SessionManager, isAr: Boolean) {
    Column(modifier = Modifier.padding(24.dp)) {
        ProfileActionItem(Icons.Rounded.EmojiEvents, if (isAr) "الإنجازات" else "Achievements") { navController.navigate("achievements") }
        ProfileActionItem(Icons.Rounded.Leaderboard, if (isAr) "لوحة الصدارة" else "Leaderboard") { navController.navigate("leaderboard") }
        ProfileActionItem(Icons.Rounded.History, if (isAr) "سجلي" else "My History") { navController.navigate("my_history") }
        ProfileActionItem(Icons.AutoMirrored.Rounded.Help, if (isAr) "المساعدة والأسئلة الشائعة" else "Help & FAQ") { navController.navigate("help_faq") }
        ProfileActionItem(Icons.Rounded.Settings, if (isAr) "إعدادات الحساب" else "Account Settings") { navController.navigate("account_settings") }
        ProfileActionItem(Icons.AutoMirrored.Rounded.Logout, if (isAr) "تسجيل الخروج" else "Log Out", isDanger = true) {
            FirebaseAuth.getInstance().signOut()
            sessionManager.logout()
            navController.navigate("login") { popUpTo(0) }
        }
    }
}

@Composable
fun ProfileActionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, isDanger: Boolean = false, onClick: () -> Unit) {
    val color = if (isDanger) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
    Surface(
        onClick = onClick,
        color = Color.Transparent,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = color.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.width(16.dp))
            Text(label, color = color, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}


