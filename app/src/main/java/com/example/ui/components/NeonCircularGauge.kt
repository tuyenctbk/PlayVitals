package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun NeonCircularGauge(
    value: Int,
    maxValue: Int = 100,
    label: String,
    modifier: Modifier = Modifier,
    size: Dp = 110.dp,
    strokeWidth: Dp = 8.dp,
    valueSuffix: String = "%",
    primaryColor: Color = StatusGreen,
    trackColor: Color = HighDensityCardElevated
) {
    val targetFraction = (value.toFloat() / maxValue).coerceIn(0f, 1f)
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(targetFraction) {
        animatedProgress.animateTo(
            targetValue = targetFraction,
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(strokeWidth / 2)) {
            val sweep = 360f * animatedProgress.value
            val strokePx = strokeWidth.toPx()

            // Background Track
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )

            // Active Progress Arc
            if (sweep > 0f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        0.0f to primaryColor.copy(alpha = 0.6f),
                        0.7f to primaryColor,
                        1.0f to AccentLavender
                    ),
                    startAngle = -90f,
                    sweepAngle = sweep,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )
            }
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "$value$valueSuffix",
                color = TextPrimary,
                fontSize = if (size > 100.dp) 24.sp else 16.sp,
                fontWeight = FontWeight.Bold
            )
            if (label.isNotBlank()) {
                Text(
                    text = label.uppercase(),
                    color = TextSecondary,
                    fontSize = if (size > 100.dp) 9.sp else 7.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

