package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun LivePingWaveform(
    pingHistory: List<Int>,
    currentPingMs: Int,
    modifier: Modifier = Modifier
) {
    val points = if (pingHistory.isEmpty()) listOf(35, 40, 38, 42, 35) else pingHistory
    val minPing = points.minOrNull() ?: currentPingMs
    val maxPingVal = points.maxOrNull() ?: currentPingMs
    val avgPing = if (points.isNotEmpty()) points.sum() / points.size else currentPingMs

    val (qualityLabel, qualityColor) = when {
        currentPingMs <= 40 -> "EXCELLENT" to StatusGreen
        currentPingMs <= 80 -> "GOOD" to AccentLavender
        currentPingMs <= 130 -> "MODERATE" to WarningOrange
        else -> "HIGH JITTER" to CriticalRed
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(HighDensityCard)
            .border(1.dp, HighDensityBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            // Header with Quality Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(qualityColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PING • LAST 2 MIN",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(qualityColor.copy(alpha = 0.15f))
                            .border(1.dp, qualityColor.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = qualityLabel,
                            color = qualityColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "$currentPingMs ms",
                        color = qualityColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Waveform Graph Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(86.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Grid lines (horizontal)
                    val gridSteps = 3
                    for (i in 1..gridSteps) {
                        val y = (height / (gridSteps + 1)) * i
                        drawLine(
                            color = Color(0x14FFFFFF),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    val graphMin = 10f
                    val graphMax = (maxPingVal.toFloat()).coerceAtLeast(100f)
                    val range = (graphMax - graphMin).coerceAtLeast(1f)

                    val stepX = width / (points.size - 1).coerceAtLeast(1)

                    val linePath = Path()
                    val fillPath = Path()

                    points.forEachIndexed { index, ping ->
                        val x = index * stepX
                        val normalized = ((ping - graphMin) / range).coerceIn(0f, 1f)
                        val y = height - (normalized * (height * 0.80f) + height * 0.10f)

                        if (index == 0) {
                            linePath.moveTo(x, y)
                            fillPath.moveTo(x, height)
                            fillPath.lineTo(x, y)
                        } else {
                            linePath.lineTo(x, y)
                            fillPath.lineTo(x, y)
                        }
                    }

                    fillPath.lineTo(width, height)
                    fillPath.close()

                    // Draw Gradient Area Fill under wave
                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                qualityColor.copy(alpha = 0.30f),
                                qualityColor.copy(alpha = 0.05f),
                                Color.Transparent
                            )
                        )
                    )

                    // Draw Stroke Waveform
                    drawPath(
                        path = linePath,
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                AccentLavender.copy(alpha = 0.7f),
                                qualityColor
                            )
                        ),
                        style = Stroke(
                            width = 2.5.dp.toPx(),
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )

                    // Current Point dot
                    if (points.isNotEmpty()) {
                        val lastIndex = points.size - 1
                        val lastX = lastIndex * stepX
                        val lastNormalized = ((points.last() - graphMin) / range).coerceIn(0f, 1f)
                        val lastY = height - (lastNormalized * (height * 0.80f) + height * 0.10f)

                        // Outer Glow
                        drawCircle(
                            color = qualityColor.copy(alpha = 0.35f),
                            radius = 7.dp.toPx(),
                            center = Offset(lastX, lastY)
                        )
                        drawCircle(
                            color = qualityColor,
                            radius = 3.5.dp.toPx(),
                            center = Offset(lastX, lastY)
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 1.8.dp.toPx(),
                            center = Offset(lastX, lastY)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Min / Avg / Max Chips Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TelemetryStatChip(label = "MIN", value = "$minPing ms", valueColor = StatusGreen)
                TelemetryStatChip(label = "AVG", value = "$avgPing ms", valueColor = AccentLavender)
                TelemetryStatChip(label = "MAX", value = "$maxPingVal ms", valueColor = if (maxPingVal > 120) WarningOrange else TextPrimary)
                TelemetryStatChip(label = "LOSS", value = "0.0%", valueColor = StatusGreen)
            }
        }
    }
}

@Composable
private fun TelemetryStatChip(
    label: String,
    value: String,
    valueColor: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label: ",
            color = TextTertiary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = value,
            color = valueColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
