package com.eloop.mobileapp.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.eloop.mobileapp.data.EcoArticle
import com.eloop.mobileapp.data.EcoData
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun EcoArticlesScreen(navController: NavController) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "Safety", "DIY", "News", "Tips", "Tech")
    
    val filteredArticles = if (selectedCategory == "All") {
        EcoData.articles
    } else {
        EcoData.articles.filter { it.category == selectedCategory }
    }

    val featuredArticles = EcoData.articles.take(3)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        ScreenHeader(title = "Eco Articles 📖", onBack = { navController.popBackStack() })

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // STEP 2: Auto Carousel
            item {
                ArticleCarousel(featuredArticles) { article ->
                    navController.navigate("article_detail/${article.id}")
                }
            }

            // STEP 3: Working Filter Chips
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            item {
                Text(
                    "Latest Content",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            items(filteredArticles) { article ->
                ArticleCard(
                    article = article,
                    onClick = { navController.navigate("article_detail/${article.id}") },
                    onBookmarkClick = { EcoData.toggleBookmark(article.id) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ArticleCarousel(articles: List<EcoArticle>, onClick: (EcoArticle) -> Unit) {
    val pagerState = rememberPagerState(pageCount = { articles.size })
    val scope = rememberCoroutineScope()

    // Auto-advance
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            val nextPage = (pagerState.currentPage + 1) % articles.size
            scope.launch {
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 12.dp
        ) { page ->
            val article = articles[page]
            val color = EcoData.getCategoryColor(article.category)
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clickable { onClick(article) },
                shape = RoundedCornerShape(24.dp)
            ) {
                Box {
                    // Real image with Coil
                    SubcomposeAsyncImage(
                        model = article.imageUrl,
                        contentDescription = article.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        loading = {
                            // Shimmer/Loading box
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.verticalGradient(listOf(color.copy(alpha = 0.3f), color.copy(alpha = 0.1f))))
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp).align(Alignment.Center),
                                    color = color,
                                    strokeWidth = 2.dp
                                )
                            }
                        },
                        error = {
                            // Error placeholder
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Brush.verticalGradient(listOf(color, color.copy(alpha = 0.5f))))
                            ) {
                                Icon(
                                    Icons.Rounded.ImageNotSupported,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                    )
                    
                    // Gradient overlay to ensure text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                )
                            )
                    )
                    
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp)
                    ) {
                        Surface(
                            color = Color.White.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                article.category.uppercase(),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                fontSize = 10.sp,
                                color = color,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            article.title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 2
                        )
                        Text(
                            article.readTime,
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }
        }

        // Dot indicators
        Row(
            Modifier
                .height(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(articles.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) PrimaryGreen else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
fun ArticleCard(article: EcoArticle, onClick: () -> Unit, onBookmarkClick: () -> Unit) {
    val isBookmarked = EcoData.isBookmarked(article.id)
    val color = EcoData.getCategoryColor(article.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            // Small thumbnail with Coil
            Card(
                modifier = Modifier
                    .size(80.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                SubcomposeAsyncImage(
                    model = article.imageUrl,
                    contentDescription = article.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = color, strokeWidth = 2.dp)
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(color.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.BrokenImage, contentDescription = null, tint = color.copy(alpha = 0.5f))
                        }
                    }
                )
            }
            
            if (isBookmarked) {
                // Keep the bookmark visual somehow? The user didn't specify, 
                // but the old code had a bookmark icon inside the box.
                // Since the real image might cover it, I'll rely on the IconButton at the end 
                // or just leave it. The old code had a box with an icon.
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(article.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 2)
                Text(
                    "${article.category} • ${article.readTime}", 
                    style = MaterialTheme.typography.labelSmall, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    article.summary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            IconButton(onClick = onBookmarkClick) {
                Icon(
                    if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder, 
                    contentDescription = null, 
                    tint = if (isBookmarked) PrimaryGreen else Color.Gray
                )
            }
        }
    }
}


