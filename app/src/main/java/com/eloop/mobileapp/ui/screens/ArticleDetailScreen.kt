package com.eloop.mobileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Bookmark
import androidx.compose.material.icons.rounded.BookmarkBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import com.eloop.mobileapp.data.EcoData
import com.eloop.mobileapp.ui.components.ELoopButton
import com.eloop.mobileapp.ui.theme.PrimaryGreen

@Composable
fun ArticleDetailScreen(navController: NavController, articleId: Int) {
    val article = EcoData.articles.find { it.id == articleId } ?: EcoData.articles[0]
    val isBookmarked = EcoData.isBookmarked(article.id)
    val categoryColor = EcoData.getCategoryColor(article.category)

    Scaffold(
        bottomBar = {
            Box(modifier = Modifier.padding(24.dp).background(MaterialTheme.colorScheme.background)) {
                ELoopButton(
                    text = "♻️ Recycle with ELoop",
                    onClick = { navController.navigate("scan") }
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Real header image with Coil
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
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
                                .background(categoryColor.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = categoryColor)
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(categoryColor, categoryColor.copy(alpha = 0.6f))
                                    )
                                )
                        )
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
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.3f))
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    
                    IconButton(
                        onClick = { EcoData.toggleBookmark(article.id) },
                        colors = IconButtonDefaults.iconButtonColors(containerColor = Color.White.copy(alpha = 0.3f))
                    ) {
                        Icon(
                            if (isBookmarked) Icons.Rounded.Bookmark else Icons.Rounded.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = Color.White
                        )
                    }
                }

                // Category Badge on Image
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(24.dp)
                ) {
                    Text(
                        article.category.uppercase(),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = categoryColor
                    )
                }
            }

            // Content
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = article.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    lineHeight = 34.sp
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "${article.category} • ${article.readTime}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = article.content,
                    style = MaterialTheme.typography.bodyLarge,
                    lineHeight = 28.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(100.dp)) // Padding for bottom button
            }
        }
    }
}


