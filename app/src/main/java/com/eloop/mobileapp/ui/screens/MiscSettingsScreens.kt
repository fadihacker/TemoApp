package com.eloop.mobileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.eloop.mobileapp.ui.components.ScreenHeader
import com.eloop.mobileapp.ui.theme.Dimens

@Composable
fun PlaceholderSettingsScreen(navController: NavController, title: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(title = title, onBack = { navController.popBackStack() })
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.SpacingLg),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Coming Soon",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "The $title feature is currently being finalized and will be available in the next update.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

// Concrete wrappers for navigation
@Composable fun EditProfileScreen(navController: NavController) = PlaceholderSettingsScreen(navController, "Edit Profile")
@Composable fun ChangePasswordScreen(navController: NavController) = PlaceholderSettingsScreen(navController, "Change Password")
@Composable fun PrivacyPolicyScreen(navController: NavController) = PlaceholderSettingsScreen(navController, "Privacy Policy")
@Composable fun TermsConditionsScreen(navController: NavController) = PlaceholderSettingsScreen(navController, "Terms & Conditions")


