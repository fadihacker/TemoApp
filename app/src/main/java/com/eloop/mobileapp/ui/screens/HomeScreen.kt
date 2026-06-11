package com.eloop.mobileapp.ui.screens

import androidx.compose.foundation.*
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
import com.eloop.mobileapp.data.FirebaseRepository
import com.eloop.mobileapp.data.UserProfile
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*
import kotlinx.coroutines.launch
import com.eloop.mobileapp.LocalAppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    var profile by remember { mutableStateOf<UserProfile?>(null) }
    var isLoadingProfile by remember { mutableStateOf(true) }
    val language = LocalAppLanguage.current
    val isAr = language == "AR"

    LaunchedEffect(Unit) {
        FirebaseRepository.listenToUserProfile().collect {
            profile = it
            isLoadingProfile = false
        }
    }

    val userName = profile?.name ?: if (isAr) "محارب البيئة" else "Eco Warrior"
    var activeTab by remember { mutableStateOf("track") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        item {
            HomeGreetingHeader(userName, navController)
        }

        item {
            HeroSection(navController)
        }

        item {
            TabsSection(activeTab) { activeTab = it }
        }

        item {
            if (activeTab == "track") {
                StatsSection(profile, isLoadingProfile)
            } else {
                ImpactStatsSection(profile, isLoadingProfile)
            }
        }

        item {
            QuickActionsSection(navController)
        }

        item {
            PromoCarousel(navController)
        }

        item {
            TipsSection(navController)
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun HomeGreetingHeader(name: String, navController: NavController) {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (name != "Eco Warrior") name.first().toString() else "E",
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = if (isAr) "مرحباً، $name 👋" else "Hello, $name 👋",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isAr) "مستعد لإنقاذ الكوكب؟" else "Ready to save the planet?",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        
        IconButton(
            onClick = { navController.navigate("notifications") },
            modifier = Modifier
                .size(48.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
        ) {
            BadgedBox(
                badge = { Badge { Text("3") } }
            ) {
                Icon(Icons.Rounded.Notifications, contentDescription = null)
            }
        }
    }
}

@Composable
fun HeroSection(navController: NavController) {
    val language = LocalAppLanguage.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .height(180.dp),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(PrimaryDark, MaterialTheme.colorScheme.primary)
                    )
                )
        ) {
            AsyncImage(
                model = "https://images.unsplash.com/photo-1611288875055-3b73360aa13a?w=600&q=60",
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.5f)
                    .align(Alignment.CenterEnd),
                alpha = 0.4f
            )

            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (language == "AR") "اكسب المكافآت" else "Earn Rewards",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (language == "AR") "قم بإعادة تدوير أجهزتك الإلكترونية\nواحصل على أموالك فوراً." else "Recycle your old electronics\nand get paid instantly.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { navController.navigate("scan") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = PrimaryDark),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(if (language == "AR") "ابدأ المسح" else "Start Scanning", style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}

@Composable
fun TabsSection(activeTab: String, onTabSelected: (String) -> Unit) {
    val language = LocalAppLanguage.current
    Row(
        modifier = Modifier
            .padding(24.dp)
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(4.dp)
    ) {
        TabButton(
            text = if (language == "AR") "النشاط" else "Activity",
            isActive = activeTab == "track",
            modifier = Modifier.weight(1f)
        ) { onTabSelected("track") }
        TabButton(
            text = if (language == "AR") "تأثيرك 🌍" else "Your Impact 🌍",
            isActive = activeTab == "impact",
            modifier = Modifier.weight(1f)
        ) { onTabSelected("impact") }
    }
}

@Composable
fun TabButton(text: String, isActive: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(24.dp))
            .background(if (isActive) MaterialTheme.colorScheme.primary else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isActive) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
fun StatsSection(profile: UserProfile?, isLoading: Boolean) {
    val language = LocalAppLanguage.current
    
    var animated by remember { mutableStateOf(false) }
    LaunchedEffect(isLoading) {
        if (!isLoading) animated = true
    }
    
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(
                icon = Icons.Rounded.Recycling, 
                value = profile?.devicesRecycled ?: 0, 
                suffix = " " + (if (language == "AR") "عناصر" else "Items"), 
                label = if (language == "AR") "مُعاد تدويرها" else "Recycled", 
                modifier = Modifier.weight(1f),
                animated = animated
            )
            StatCard(
                icon = Icons.Rounded.Star, 
                value = profile?.ecoPoints ?: 0, 
                suffix = " " + (if (language == "AR") "نقطة" else "pts"), 
                label = if (language == "AR") "المكافآت" else "Rewards", 
                modifier = Modifier.weight(1f),
                animated = animated
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        StatCardStatic(
            Icons.Rounded.Timer, 
            if (language == "AR") "٢ قيد الانتظار" else "2 Pending", 
            if (language == "AR") "عمليات الاستلام الحالية" else "Current Pickups", 
            Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun ImpactStatsSection(profile: UserProfile?, isLoading: Boolean) {
    val language = LocalAppLanguage.current
    
    var animated by remember { mutableStateOf(false) }
    LaunchedEffect(isLoading) {
        if (!isLoading) animated = true
    }
    
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(64.dp).background(PrimaryGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Public, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    if (animated) {
                        CountUpFloatText(
                            targetValue = (profile?.co2Saved ?: 0.0).toFloat(),
                            precision = 1,
                            style = MaterialTheme.typography.headlineMedium.copy(color = PrimaryGreen, fontWeight = FontWeight.ExtraBold),
                            suffix = " kg"
                        )
                    } else {
                        Text(
                            text = "0.0 kg",
                            style = MaterialTheme.typography.headlineMedium.copy(color = PrimaryGreen, fontWeight = FontWeight.ExtraBold)
                        )
                    }
                    Text(if (language == "AR") "إجمالي تقليل الكربون" else "Total CO2 Offset", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}

@Composable
fun StatCard(icon: ImageVector, value: Int, suffix: String, label: String, modifier: Modifier = Modifier, color: Color = PrimaryGreen, animated: Boolean = true) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            if (animated) {
                CountUpText(
                    targetValue = value,
                    suffix = suffix,
                    style = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)
                )
            } else {
                Text(text = "0$suffix", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
            Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun StatCardStatic(icon: ImageVector, value: String, label: String, modifier: Modifier = Modifier, color: Color = PrimaryGreen) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun QuickActionsSection(navController: NavController) {
    val language = LocalAppLanguage.current
    Column(modifier = Modifier.padding(24.dp)) {
        Text(if (language == "AR") "إجراءات سريعة" else "Quick Actions", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickActionButton(Icons.Rounded.QrCodeScanner, if (language == "AR") "مسح" else "Scan") { navController.navigate("scan") }
            QuickActionButton(Icons.Rounded.Map, if (language == "AR") "مراكز" else "Find Centers") { navController.navigate("find") }
            QuickActionButton(Icons.Rounded.LocalOffer, if (language == "AR") "عروض" else "Best Deals", hasBadge = true) { navController.navigate("offers") }
            QuickActionButton(Icons.Rounded.History, if (language == "AR") "سجل" else "History") { navController.navigate("device_journey") }
        }
    }
}

@Composable
fun QuickActionButton(icon: ImageVector, label: String, hasBadge: Boolean = false, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            BadgedBox(
                badge = { if (hasBadge) Badge { Text("!") } }
            ) {
                Icon(imageVector = icon, contentDescription = label, modifier = Modifier.size(28.dp), tint = PrimaryDark)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelSmall)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PromoCarousel(navController: NavController) {
    val language = LocalAppLanguage.current
    val items = listOf(
        "Collection Week!" to "https://images.unsplash.com/photo-1532187863486-abf9d3c5443d?w=600&q=60",
        "New Rewards" to "https://images.unsplash.com/photo-1557804506-669a67965ba0?w=600&q=60",
        "Eco Tips" to "https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=600&q=60"
    )
    val pagerState = androidx.compose.foundation.pager.rememberPagerState(pageCount = { items.size })

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        androidx.compose.foundation.pager.HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 16.dp
        ) { page ->
            val (title, imageUrl) = items[page]
            Card(
                modifier = Modifier.fillMaxWidth().height(120.dp).clickable { navController.navigate("scan") },
                shape = RoundedCornerShape(20.dp)
            ) {
                Box {
                    AsyncImage(model = imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.4f)))
                    Column(modifier = Modifier.padding(16.dp).align(Alignment.CenterStart)) {
                        Text(if (language == "AR") (if (title == "Collection Week!") "أسبوع التجميع!" else if (title == "New Rewards") "مكافآت جديدة" else "نصائح بيئية") else title, style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(if (language == "AR") "تعلم كيف تضاعف نقاطك" else "Learn how to double your points", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            Modifier.fillMaxWidth().height(8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(items.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) PrimaryGreen else Color.Gray.copy(alpha = 0.3f)
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(if (pagerState.currentPage == iteration) 16.dp else 8.dp, 8.dp)
                )
            }
        }
    }
}

@Composable
fun TipsSection(navController: NavController) {
    val language = LocalAppLanguage.current
    Column(modifier = Modifier.padding(24.dp)) {
        Text(if (language == "AR") "المكتبة البيئية" else "Eco Library", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TipCard(
                if (language == "AR") "إعادة تدوير لابتوب" else "Recycling 1 Laptop", 
                if (language == "AR") "يوفر طاقة لمدة ٥ سنوات" else "Saves energy for 5 years of usage.",
                "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&q=60",
                Modifier.weight(1f)
            )
            TipCard(
                if (language == "AR") "عمر الهاتف" else "Phone Life", 
                if (language == "AR") "لا تتخلص منهم، احصل على مقابل" else "Don't throw them, get paid.",
                "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&q=60",
                Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedButton(
            onClick = { navController.navigate("eco_articles") },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Text(if (language == "AR") "استكشف المقالات" else "Explore Articles", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun TipCard(title: String, subtitle: String, imageUrl: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(160.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Box {
            AsyncImage(model = imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
            Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))
            Column(modifier = Modifier.padding(12.dp).align(Alignment.BottomStart)) {
                Text(text = title, fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.8f), maxLines = 2)
            }
        }
    }
}


