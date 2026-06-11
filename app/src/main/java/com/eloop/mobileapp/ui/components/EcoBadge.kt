package com.eloop.mobileapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eloop.mobileapp.ui.theme.AccentYellow
import com.eloop.mobileapp.ui.theme.PrimaryDarkGreen

@Composable
fun EcoBadge(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AccentYellow)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = PrimaryDarkGreen,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp
        )
    }
}


