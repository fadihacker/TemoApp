package com.eloop.mobileapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.data.SessionManager
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    var showLogo by remember { mutableStateOf(false) }
    var showAIText by remember { mutableStateOf(false) }
    var showProgress by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        showLogo = true
        delay(300)
        showAIText = true
        delay(800) 
        showProgress = true
        delay(400) 
        
        val currentUser = FirebaseAuth.getInstance().currentUser
        val route = if (currentUser != null) {
            // Sync session manager just in case
            sessionManager.setLoggedIn(
                true, 
                currentUser.email ?: "", 
                currentUser.displayName ?: ""
            )
            "home"
        } else if (sessionManager.isLoggedIn()) {
            // This case handles if firebase session is lost but we have local session
            // or vice versa. For consistency, if Firebase says no user, we should probably re-auth.
            "home" // Or we could force login if we want strictly Firebase
        } else {
            "onboarding"
        }

        navController.navigate(route) {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            AnimatedVisibility(
                visible = showLogo,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    )
                ) + fadeIn(animationSpec = tween(1000))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier.size(120.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Recycling,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                            modifier = Modifier.size(80.dp)
                        )
                        Icon(
                            imageVector = Icons.Rounded.Sync,
                            contentDescription = null,
                            tint = PrimaryDark,
                            modifier = Modifier
                                .size(70.dp)
                                .offset(x = 15.dp, y = (-15).dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "e-loop",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryDark
                        )
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            AnimatedVisibility(
                visible = showAIText,
                enter = fadeIn(tween(500)) + expandVertically()
            ) {
                Text(
                    text = "Powered by AI ♻️",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            AnimatedVisibility(
                visible = showProgress,
                enter = fadeIn(tween(300))
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}


