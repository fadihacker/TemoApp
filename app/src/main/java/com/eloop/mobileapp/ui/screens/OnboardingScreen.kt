package com.eloop.mobileapp.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.eloop.mobileapp.LocalAppLanguage

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    
    val onboardingPages = listOf(
        OnboardingPage(
            if (isAr) "حول نفاياتك إلى قيمة" else "Turn your waste into value",
            if (isAr) "قم بإعادة تدوير أجهزتك الإلكترونية واكسب المكافآت مع الحفاظ على البيئة." else "Recycle your electronics and earn rewards while saving the environment.",
            "https://images.unsplash.com/photo-1532996122724-e3c354a0b15b?w=400&q=80"
        ),
        OnboardingPage(
            if (isAr) "لماذا نعيد التدوير؟" else "Why Recycle?",
            if (isAr) "لحماية كوكبنا من النفايات السامة واستعادة المواد القيمة." else "To protect our planet from toxic waste and recover valuable materials.",
            "https://images.unsplash.com/photo-1611284446314-60a58ac0deb9?w=400&q=80"
        ),
        OnboardingPage(
            if (isAr) "كيف يعمل الأمر؟" else "How it Works?",
            if (isAr) "١. امسح جهازك 📸\n٢. احصل على سعر فوري 💰\n٣. اكسب نقاط مكافأة 🏆" else "1. Scan your device 📸\n2. Get instant price 💰\n3. Earn rewards units 🏆",
            "https://images.unsplash.com/photo-1556742049-0cfed4f6a45d?w=400&q=80"
        )
    )

    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    
    // Gradients per page
    val backgroundGradients: List<Brush> = listOf(
        Brush.verticalGradient(colors = listOf(OnboardingG1Start, OnboardingG1End)),
        Brush.verticalGradient(colors = listOf(OnboardingG2Start, OnboardingG2End)),
        Brush.verticalGradient(colors = listOf(OnboardingG3Start, OnboardingG3End))
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                val page = onboardingPages[pageIndex]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        SubcomposeAsyncImage(
                            model = page.imageUrl,
                            contentDescription = null,
                            modifier = Modifier
                                .size(300.dp)
                                .clip(RoundedCornerShape(32.dp)),
                            contentScale = ContentScale.Crop,
                            loading = {
                                CircularProgressIndicator(modifier = Modifier.size(40.dp), color = PrimaryGreen)
                            },
                            error = {
                                Icon(Icons.Rounded.BrokenImage, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(40.dp))
                            }
                        )
                    }
                    
                    if (pageIndex == 0) {
                        var showSwipeHint by remember { mutableStateOf(true) }
                        LaunchedEffect(Unit) {
                            delay(2000)
                            showSwipeHint = false
                        }
                        AnimatedVisibility(
                            visible = showSwipeHint,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Text(
                                if (isAr) "← اسحب للاستكشاف" else "Swipe to explore →",
                                color = TextMuted,
                                modifier = Modifier.padding(top = 16.dp),
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = page.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextMain,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = page.subtitle,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextMuted,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Animated Dots
            Row(
                modifier = Modifier
                    .padding(vertical = 24.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(onboardingPages.size) { iteration ->
                    val isSelected = pagerState.currentPage == iteration
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                        label = "DotWidth"
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(if (isSelected) PrimaryGreen else PrimaryGreen.copy(alpha = 0.2f))
                    )
                }
            }

            val isLastPage = pagerState.currentPage == onboardingPages.size - 1
            
            Button(
                onClick = {
                    if (pagerState.currentPage < onboardingPages.size - 1) {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    } else {
                        navController.navigate("login") {
                            popUpTo("onboarding") { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isLastPage) PrimaryGreen else Color.White,
                    contentColor = if (isLastPage) Color.White else PrimaryDarkGreen
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = if (isLastPage) (if (isAr) "ابدأ الآن 🌿" else "Get Started 🌿") else (if (isAr) "التالي" else "Next"),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }

            TextButton(
                onClick = {
                    navController.navigate("login") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(if (isAr) "تخطي" else "Skip", color = TextMuted)
            }
        }
    }
}

data class OnboardingPage(
    val title: String,
    val subtitle: String,
    val imageUrl: String
)


