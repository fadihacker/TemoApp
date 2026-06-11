package com.eloop.mobileapp.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eloop.mobileapp.ui.theme.BorderColor
import com.eloop.mobileapp.ui.theme.PrimaryGreen
import com.eloop.mobileapp.ui.theme.PrimaryDarkGreen

@Composable
fun ProgressBarItem(label: String, current: Float, max: Float, unit: String) {
    Column(modifier = Modifier.padding(bottom = 14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(label, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Text(
                "$current $unit / $max $unit",
                color = PrimaryDarkGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        LinearProgressIndicator(
            progress = { if (max > 0) current / max else 0f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(20.dp)),
            color = PrimaryGreen,
            trackColor = BorderColor
        )
    }
}


