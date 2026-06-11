package com.eloop.mobileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*
import com.eloop.mobileapp.LocalAppLanguage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConditionScreen(
    navController: NavController, 
    deviceName: String,
    basePrice: Int = 8000,
    co2Saved: Float = 4.2f,
    ecoPoints: Int = 820
) {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    var selectedCondition by remember { mutableStateOf<String?>(null) }
    
    val conditions = listOf(
        ConditionOption(
            if (isAr) "ممتازة" else "Excellent",
            if (isAr) "مثل الجديد، لا توجد خدوش" else "Like new, no scratches",
            Icons.Rounded.Star,
            "excellent"
        ),
        ConditionOption(
            if (isAr) "جيدة" else "Good",
            if (isAr) "علامات استخدام طفيفة" else "Minor signs of use",
            Icons.Rounded.ThumbUp,
            "good"
        ),
        ConditionOption(
            if (isAr) "متوسطة" else "Fair",
            if (isAr) "خدوش/صدمات ظاهرة" else "Visible scratches/dents",
            Icons.Rounded.Warning,
            "fair"
        ),
        ConditionOption(
            if (isAr) "تالفة" else "Broken",
            if (isAr) "مكسور أو غير صالح" else "Cracked or non-functional",
            Icons.Rounded.Build,
            "broken"
        )
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(if (isAr) "كيف حال جهازك؟" else "How's your device?", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                if (isAr) "هذا يؤثر على قيمتك التقديرية لجهاز $deviceName" else "This affects your estimated value for $deviceName",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(conditions) { option ->
                    ConditionCard(
                        option = option,
                        isSelected = selectedCondition == option.id,
                        onClick = { selectedCondition = option.id }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(if (isAr) "اكسب ٥٠ نقطة خضراء! ♻️" else "Earn 50 Green Points! ♻️", color = PrimaryDark, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            ELoopButton(
                text = if (isAr) "عرض التقييم النهائي" else "Show Final Valuation",
                onClick = {
                    selectedCondition?.let { condition ->
                        navController.navigate(
                            "ai_valuation?" +
                            "category=$deviceName" +
                            "&basePrice=$basePrice" +
                            "&condition=$condition" +
                            "&co2Saved=$co2Saved" +
                            "&ecoPoints=$ecoPoints"
                        )
                    }
                },
                enabled = selectedCondition != null,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ConditionCard(
    option: ConditionOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) PrimaryGreen else Color.Transparent
    val backgroundColor = if (isSelected) PrimaryGreen.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surface
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .border(2.dp, borderColor, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                option.icon,
                contentDescription = null,
                tint = if (isSelected) PrimaryGreen else PrimaryDark,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                option.title,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) PrimaryGreen else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                option.subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )
        }
    }
}

data class ConditionOption(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val id: String
)


