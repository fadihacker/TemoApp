package com.eloop.mobileapp.ui.components

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle

@Composable
fun CountUpText(
    targetValue: Int,
    durationMs: Int = 1200,
    suffix: String = "",
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    var countStarted by remember { mutableStateOf(false) }
    val animatedValue by animateIntAsState(
        targetValue = if (countStarted) targetValue else 0,
        animationSpec = tween(durationMillis = durationMs, easing = EaseOutCubic),
        label = "CountUp"
    )

    LaunchedEffect(Unit) {
        countStarted = true
    }

    Text(
        text = "$animatedValue$suffix",
        style = style,
        modifier = modifier
    )
}

@Composable
fun CountUpFloatText(
    targetValue: Float,
    precision: Int = 1,
    durationMs: Int = 1200,
    suffix: String = "",
    style: TextStyle,
    modifier: Modifier = Modifier
) {
    // To animate a float, we can animate an Int representation (e.g., target * 10) and then divide
    val multiplier = when(precision) {
        1 -> 10
        2 -> 100
        else -> 1
    }
    
    var countStarted by remember { mutableStateOf(false) }
    val animatedValue by animateIntAsState(
        targetValue = if (countStarted) (targetValue * multiplier).toInt() else 0,
        animationSpec = tween(durationMillis = durationMs, easing = EaseOutCubic),
        label = "CountUpFloat"
    )

    LaunchedEffect(Unit) {
        countStarted = true
    }

    val displayValue = animatedValue.toFloat() / multiplier
    Text(
        text = "%.${precision}f%s".format(displayValue, suffix),
        style = style,
        modifier = modifier
    )
}


