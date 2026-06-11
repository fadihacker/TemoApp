package com.eloop.mobileapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*

import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSettingsScreen(
    navController: NavController,
    currentThemeMode: String,
    onThemeChange: (String) -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val sessionManager = remember { com.eloop.mobileapp.data.SessionManager(context) }
    
    var isPushEnabled by remember { mutableStateOf(sessionManager.isPushEnabled) }
    var selectedLanguage by remember(currentLanguage) { mutableStateOf(if (currentLanguage == "AR") "Arabic" else "English") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = { Text("This will permanently remove your data, eco-points, and recycling history. Are you sure?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        sessionManager.logout()
                        navController.navigate("login") { popUpTo(0) }
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete Permanently")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (selectedLanguage == "Arabic") "إعدادات الحساب" else "Account Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        val isAr = selectedLanguage == "Arabic"
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
        ) {
            item {
                ProfileSummaryCard(
                    name = sessionManager.getUserName() ?: if (isAr) "محارب البيئة" else "Eco Warrior",
                    email = sessionManager.getUserEmail() ?: "user@eloop.app"
                )
                Spacer(modifier = Modifier.height(24.dp))
            }

            item { SectionLabel(if (isAr) "التفضيلات" else "PREFERENCES") }
            
            item {
                SettingsGroup {
                    // Language Switcher
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Language, contentDescription = null, tint = PrimaryGreen)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(if (isAr) "لغة التطبيق" else "App Language", style = MaterialTheme.typography.bodyLarge)
                        }
                        Row {
                            FilterChip(
                                selected = currentLanguage == "EN",
                                onClick = { 
                                    onLanguageChange("EN")
                                },
                                label = { Text("EN") }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FilterChip(
                                selected = currentLanguage == "AR",
                                onClick = { 
                                    onLanguageChange("AR")
                                },
                                label = { Text("AR") }
                            )
                        }
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
                    
                    // Theme Switcher
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Palette, contentDescription = null, tint = PrimaryGreen)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(if (isAr) "المظهر" else "Appearance", style = MaterialTheme.typography.bodyLarge)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            ThemeChip(if (isAr) "فاتح" else "Light", Icons.Rounded.LightMode, currentThemeMode == "LIGHT") { onThemeChange("LIGHT") }
                            ThemeChip(if (isAr) "داكن" else "Dark", Icons.Rounded.DarkMode, currentThemeMode == "DARK") { onThemeChange("DARK") }
                            ThemeChip(if (isAr) "النظام" else "System", Icons.Rounded.SettingsBrightness, currentThemeMode == "SYSTEM") { onThemeChange("SYSTEM") }
                        }
                    }
                }
            }

            item { SectionLabel(if (isAr) "الإدارة" else "ADMINISTRATION") }
            item {
                ELoopListItem(
                    icon = Icons.Rounded.AdminPanelSettings,
                    title = if (isAr) "لوحة التحكم" else "Admin Dashboard",
                    subtitle = if (isAr) "إدارة الطلبات والأسعار" else "Manage orders & prices",
                    onClick = { navController.navigate("admin_dashboard") }
                )
            }

            item { SectionLabel(if (isAr) "الإشعارات" else "NOTIFICATIONS") }
            item {
                SettingsGroup {
                    ToggleSettingItem(
                        icon = Icons.Rounded.NotificationsActive,
                        title = if (isAr) "تنبيهات فورية" else "Push Notifications",
                        subtitle = if (isAr) "تنبيهات لمواعيد الاستلام والعروض" else "Alerts for pickups & offers",
                        checked = isPushEnabled,
                        onCheckedChange = { 
                            isPushEnabled = it
                            sessionManager.isPushEnabled = it
                        }
                    )
                }
            }

            item { SectionLabel(if (isAr) "منطقة الخطر" else "DANGER ZONE") }
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(if (isAr) "حذف الحساب" else "Delete Account", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                        Text(if (isAr) "هذا الإجراء نهائي وسيحذف جميع بياناتك وإنجازاتك." else "This is permanent and will erase all your impact stats.", style = MaterialTheme.typography.bodySmall)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showDeleteDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(if (isAr) "طلب الحذف" else "Request Deletion")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    "ELoop Mobile • v1.2.0\nMade with 💚 for a Greener Future",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun SettingsGroup(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(content = content)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeChip(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, isSelected: Boolean, onClick: () -> Unit) {
    Surface(
        selected = isSelected,
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
        modifier = Modifier.width(90.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
fun ProfileSummaryCard(name: String, email: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen),
                contentAlignment = Alignment.Center
            ) {
                Text(name.take(1).uppercase(), color = Color.White, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = PrimaryGreen.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Text("Level 4 Eco Hero 🏆", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 10.sp, color = PrimaryGreen, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ThemeModeItem(title: String, mode: String, isSelected: Boolean, onClick: () -> Unit) {
    ELoopListItem(
        icon = if (mode == "DARK") Icons.Default.DarkMode else if (mode == "LIGHT") Icons.Default.LightMode else Icons.Default.Palette,
        title = title,
        trailingIcon = if (isSelected) Icons.Default.Check else Icons.Default.ChevronRight,
        iconTint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        onClick = onClick
    )
}


@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(vertical = Dimens.SpacingSm),
        letterSpacing = 1.sp
    )
}


