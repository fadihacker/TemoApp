package com.eloop.mobileapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.eloop.mobileapp.ui.theme.PrimaryGreen
import com.eloop.mobileapp.ui.theme.PrimaryDarkGreen
import com.eloop.mobileapp.ui.theme.TextMuted

@Composable
fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    ELoopCard(modifier = modifier) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                icon,
                contentDescription = null,
                tint = PrimaryGreen,
                modifier = Modifier.size(36.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                color = PrimaryDarkGreen
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted
            )
        }
    }
}


