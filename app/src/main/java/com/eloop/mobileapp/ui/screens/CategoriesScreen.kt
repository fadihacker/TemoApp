package com.eloop.mobileapp.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.SearchOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*
import com.eloop.mobileapp.LocalAppLanguage

data class Category(
    val title: String,
    val titleAr: String,
    val description: String,
    val descriptionAr: String,
    val icon: ImageVector,
    val imageUrl: String
)

val categories = listOf(
    Category("Smartphone", "هاتف ذكي", "iPhone, Samsung, Pixel", "آيفون، سامسونج، بكسل", Icons.Rounded.PhoneAndroid, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=500&q=80"),
    Category("Laptop", "كمبيوتر محمول", "MacBook, Dell, HP", "ماكبوك، ديل، إتش بي", Icons.Rounded.LaptopMac, "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=500&q=80"),
    Category("Headphone", "سماعة رأس", "AirPods, Galaxy Buds, Sony", "إيربودز، جالكسي بودز، سوني", Icons.Rounded.Headphones, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=500&q=80"),
    Category("Smartwatch", "ساعة ذكية", "Apple Watch, Galaxy Watch", "ساعة آبل، ساعة جالكسي", Icons.Rounded.Watch, "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=500&q=80"),
    Category("Tablet", "جهاز لوحي (تابلت)", "iPad, Galaxy Tab, Surface", "آيباد، جالكسي تاب، سيرفيس", Icons.Rounded.TabletMac, "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=500&q=80"),
    Category("Camera", "كاميرا تصوير", "DSLR, Mirrorless, GoPro", "دي إس إل آر، ميرورليس، جوبرو", Icons.Rounded.PhotoCamera, "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=500&q=80"),
    Category("Gaming Console", "أجهزة ألعاب", "PlayStation, Xbox, Switch", "بلايستيشن، إكس بوكس، سويتش", Icons.Rounded.VideogameAsset, "https://images.unsplash.com/photo-1606144042614-b2417e99c4e3?w=500&q=80"),
    Category("Accessories", "ملحقات وإكسسوارات", "Keyboard, Chargers, Cables", "لوحة مفاتيح، شواحن، كابلات", Icons.Rounded.Keyboard, "https://images.unsplash.com/photo-1587829741301-dc798b83add3?w=500&q=80")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(navController: NavController) {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    var searchQuery by remember { mutableStateOf("") }
    
    val filteredCategories = categories.filter {
        val titleText = if (isAr) it.titleAr else it.title
        val descText = if (isAr) it.descriptionAr else it.description
        titleText.contains(searchQuery, ignoreCase = true) || 
        descText.contains(searchQuery, ignoreCase = true) ||
        it.title.contains(searchQuery, ignoreCase = true) || 
        it.description.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(title = if (isAr) "الفئات" else "Categories", onBack = { navController.popBackStack() })

        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(if (isAr) "ابحث عن الأجهزة..." else "Search devices...") },
                leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                shape = RoundedCornerShape(28.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            if (filteredCategories.isEmpty()) {
                EmptyState(
                    icon = Icons.Rounded.SearchOff,
                    title = if (isAr) "لم يتم العثور على نتائج" else "No results found",
                    subtitle = if (isAr) "حاول البحث عن جهاز أو علامة تجارية أخرى." else "Try searching for a different device or brand."
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(filteredCategories) { category ->
                        CategoryCard(category) {
                            navController.navigate("scan?hint=${java.net.URLEncoder.encode(category.title, "UTF-8")}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryCard(category: Category, onClick: () -> Unit) {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                AsyncImage(
                    model = category.imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.2f)))
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.padding(8.dp).align(Alignment.TopEnd)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (isAr) category.titleAr else category.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Text(
                text = if (isAr) category.descriptionAr else category.description,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 1
            )
        }
    }
}


