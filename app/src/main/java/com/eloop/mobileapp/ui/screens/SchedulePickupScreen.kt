package com.eloop.mobileapp.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.data.FirebaseRepository
import com.eloop.mobileapp.ui.components.ELoopButton
import com.eloop.mobileapp.ui.components.ScreenHeader
import com.eloop.mobileapp.ui.theme.*
import com.eloop.mobileapp.LocalAppLanguage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulePickupScreen(navController: NavController, deviceName: String = "Your Device", devicePrice: String = "2,450") {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    val context = LocalContext.current
    var selectedPickupType by remember { mutableIntStateOf(0) } // 0 = Home, 1 = Drop-off
    var selectedDateIndex by remember { mutableIntStateOf(0) }
    var selectedTimeIndex by remember { mutableIntStateOf(0) }
    var address by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    var isSubmitting by remember { mutableStateOf(false) }

    val dates = if (isAr) {
        listOf("اليوم", "غداً", "٢٦ أكتوبر", "٢٧ أكتوبر", "٢٨ أكتوبر")
    } else {
        listOf("Today", "Tomorrow", "Oct 26", "Oct 27", "Oct 28")
    }
    
    val times = if (isAr) {
        listOf("٩-١١ صباحاً", "١١-١ ظهراً", "٢-٤ عصراً", "٤-٦ مساءً")
    } else {
        listOf("9-11 AM", "11-1 PM", "2-4 PM", "4-6 PM")
    }

    Scaffold(
        topBar = {
            ScreenHeader(title = if (isAr) "جدولة الاستلام 🚚" else "Schedule Pickup 🚚", onBack = { navController.popBackStack() })
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {

            // ── DEVICE CARD ──
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(Dimens.RadiusLg),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(Dimens.SpacingLg),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = CircleShape,
                        color = PrimaryGreen.copy(alpha = 0.1f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                            Icon(
                                imageVector = Icons.Rounded.PhoneAndroid,
                                contentDescription = null,
                                tint = PrimaryDarkGreen,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            deviceName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            if (isAr) "جاهز للاستلام" else "Ready for pickup",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextMuted
                        )
                    }
                    Text(
                        "$devicePrice EGP",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = PrimaryDarkGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── PICKUP TYPE ──
            PickupSectionLabel(if (isAr) "نوع الاستلام" else "PICKUP TYPE")
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PickupTypeCard(
                    icon = Icons.Rounded.Home,
                    label = if (isAr) "استلام من المنزل" else "Home Pickup",
                    isSelected = selectedPickupType == 0,
                    onClick = { selectedPickupType = 0 },
                    modifier = Modifier.weight(1f)
                )
                PickupTypeCard(
                    icon = Icons.Rounded.LocationOn,
                    label = if (isAr) "نقطة تسليم" else "Drop-off Point",
                    isSelected = selectedPickupType == 1,
                    onClick = { selectedPickupType = 1 },
                    modifier = Modifier.weight(1f)
                )
            }

            // Address input if Home Pickup is selected
            if (selectedPickupType == 0) {
                var expanded by remember { mutableStateOf(false) }
                val areas = if (isAr) listOf("القاهرة", "الجيزة", "القاهرة الجديدة", "الشيخ زايد", "المعادي") 
                            else listOf("Cairo", "Giza", "New Cairo", "Sheikh Zayed", "Maadi")
                var selectedArea by remember { mutableStateOf(areas[0]) }

                Spacer(modifier = Modifier.height(16.dp))
                
                // Area Dropdown
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedArea,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (isAr) "المنطقة" else "Area") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(Dimens.RadiusMd),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryDarkGreen,
                            unfocusedBorderColor = BorderColor
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        areas.forEach { area ->
                            DropdownMenuItem(
                                text = { Text(area) },
                                onClick = {
                                    selectedArea = area
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text(if (isAr) "أدخل عنوانك" else "Enter your address") },
                    placeholder = { Text(if (isAr) "الشارع، المبنى، الطابق..." else "Street, Building, Floor...") },
                    leadingIcon = {
                        Icon(Icons.Rounded.EditLocationAlt, contentDescription = null, tint = PrimaryGreen)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.RadiusMd),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryDarkGreen,
                        unfocusedBorderColor = BorderColor,
                        cursorColor = PrimaryDarkGreen
                    ),
                    singleLine = false,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── SELECT DATE ──
            PickupSectionLabel(if (isAr) "اختر التاريخ" else "SELECT DATE")
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                dates.forEachIndexed { index, date ->
                    SelectableChip(
                        text = date,
                        isSelected = selectedDateIndex == index,
                        onClick = { selectedDateIndex = index }
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── SELECT TIME ──
            PickupSectionLabel(if (isAr) "اختر الوقت" else "SELECT TIME")
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                times.forEachIndexed { index, time ->
                    SelectableChip(
                        text = time,
                        isSelected = selectedTimeIndex == index,
                        onClick = { selectedTimeIndex = index }
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // ── CONFIRM BUTTON ──
            ELoopButton(
                text = if (isSubmitting) (if (isAr) "جاري التأكيد..." else "Confirming...") else (if (isAr) "تأكيد الاستلام 🚚" else "Confirm Pickup 🚚"),
                onClick = {
                    scope.launch {
                        isSubmitting = true
                        try {
                            FirebaseRepository.savePickupOrder(
                                context = context,
                                deviceName = deviceName,
                                price = devicePrice,
                                date = dates[selectedDateIndex],
                                time = times[selectedTimeIndex],
                                address = address,
                                pickupType = if (selectedPickupType == 0) "Home Pickup" else "Drop-off Point"
                            )
                            navController.navigate("order_confirmation")
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            isSubmitting = false
                        }
                    }
                },
                isDark = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// â”€â”€ Reusable Components â”€â”€

@Composable
private fun PickupSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = TextMuted,
        letterSpacing = 1.sp
    )
}

@Composable
private fun PickupTypeCard(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryDarkGreen else BorderColor
    )
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryGreen.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
    )

    Card(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(Dimens.RadiusMd))
            .clickable { onClick() },
        shape = RoundedCornerShape(Dimens.RadiusMd),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) PrimaryDarkGreen else TextMuted,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) PrimaryDarkGreen else TextMuted
            )
        }
    }
}

@Composable
private fun SelectableChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue = if (isSelected) PrimaryDarkGreen else Color.Transparent
    )
    val textColor by animateColorAsState(
        targetValue = if (isSelected) Color.White else TextMuted
    )
    val border = if (isSelected) BorderStroke(0.dp, Color.Transparent) else BorderStroke(1.dp, BorderColor)

    Surface(
        shape = RoundedCornerShape(Dimens.RadiusXl),
        color = bgColor,
        border = border,
        modifier = Modifier
            .clip(RoundedCornerShape(Dimens.RadiusXl))
            .clickable { onClick() }
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = textColor
        )
    }
}


