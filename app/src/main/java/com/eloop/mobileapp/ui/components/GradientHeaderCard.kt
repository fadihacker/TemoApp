package com.eloop.mobileapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.eloop.mobileapp.ui.theme.Dimens
import com.eloop.mobileapp.ui.theme.PrimaryGreen
import com.eloop.mobileapp.ui.theme.PrimaryDarkGreen

@Composable
fun GradientHeaderCard(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable (ColumnScope.() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(Dimens.RadiusLg))
            .background(
                Brush.linearGradient(
                    colors = listOf(PrimaryDarkGreen, PrimaryGreen)
                )
            )
            .padding(Dimens.SpacingLg)
    ) {
        // Decorative circle
        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = 80.dp, y = (-40).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.1f))
                .align(Alignment.TopEnd)
        )
        Column {
            Text(
                title,
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                subtitle,
                color = Color.White.copy(alpha = 0.85f),
                style = MaterialTheme.typography.bodyLarge
            )
            content?.invoke(this)
        }
    }
}


