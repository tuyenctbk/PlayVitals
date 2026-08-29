package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameSession
import com.example.ui.theme.*

data class PerformanceTrendPoint(
    val sessionLabel: String,
    val ramUsagePercent: Float, // 0..100
    val batteryTempC: Float,    // 20..50
    val latencyMs: Int
)

@Composable
fun SessionPerformanceTrendChart(
    sessions: List<GameSession>,
    modifier: Modifier = Modifier
) {
    // Generate trend points from sessions or realistic telemetry samples if session list is empty
    val points = remember(sessions) {
        if (sessions.isNotEmpty()) {
            sessions.take(10).reversed().mapIndexed { idx, s ->
                PerformanceTrendPoint(
                    sessionLabel = "S${idx + 1}",
                    ramUsagePercent = (100 - s.minFreeRamPercent).toFloat().coerceIn(20f, 95f),
                    batteryTempC = s.peakBatteryTempC.coerceIn(25f, 45f),
                    latencyMs = s.avgLatencyMs
                )
            }
        } else {
            listOf(
                PerformanceTrendPoint("S1", 42f, 28f, 32),
                PerformanceTrendPoint("S2", 55f, 31f, 38),
                PerformanceTrendPoint("S3", 68f, 36f, 45),
                PerformanceTrendPoint("S4", 60f, 39f, 42),
                PerformanceTrendPoint("S5", 52f, 34f, 35),
                PerformanceTrendPoint("S6", 48f, 32f, 30),
                PerformanceTrendPoint("S7", 74f, 38f, 48)
            )
        }
    }

    var selectedPointIndex by remember { mutableIntStateOf(points.size - 1) }
    val selectedPoint = points.getOrElse(selectedPointIndex) { points.last() }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(HighDensityCard)
            .border(1.dp, HighDensityBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Column {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PERFORMANCE TRENDS",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "RAM Usage & Battery Temperature history",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    LegendItem(label = "RAM %", color = AccentLavender)
                    Spacer(modifier = Modifier.width(10.dp))
                    LegendItem(label = "TEMP °C", color = WarningOrange)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Chart Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(points) {
                            detectTapGestures { offset ->
                                val stepX = size.width / (points.size - 1).coerceAtLeast(1)
                                val tappedIndex = (offset.x / stepX).toInt().coerceIn(0, points.size - 1)
                                selectedPointIndex = tappedIndex
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val steps = points.size
                    val stepX = width / (steps - 1).coerceAtLeast(1)

                    // Draw Background Grid
                    val gridLines = 3
                    for (i in 0..gridLines) {
                        val y = (height / gridLines) * i
                        drawLine(
                            color = Color(0x1AFFFFFF),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Temperature Warning threshold line at 38°C (scaled 20..45)
                    val warnTempNorm = (38f - 20f) / (45f - 20f)
                    val warnY = height - (warnTempNorm * height)
                    drawLine(
                        color = CriticalRed.copy(alpha = 0.35f),
                        start = Offset(0f, warnY),
                        end = Offset(width, warnY),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    val ramPath = Path()
                    val tempPath = Path()

                    points.forEachIndexed { index, p ->
                        val x = index * stepX

                        // RAM scale 0..100
                        val ramNorm = (p.ramUsagePercent / 100f).coerceIn(0f, 1f)
                        val ramY = height - (ramNorm * (height * 0.8f) + height * 0.1f)

                        // Temp scale 20..45
                        val tempNorm = ((p.batteryTempC - 20f) / 25f).coerceIn(0f, 1f)
                        val tempY = height - (tempNorm * (height * 0.8f) + height * 0.1f)

                        if (index == 0) {
                            ramPath.moveTo(x, ramY)
                            tempPath.moveTo(x, tempY)
                        } else {
                            ramPath.lineTo(x, ramY)
                            tempPath.lineTo(x, tempY)
                        }
                    }

                    // Draw RAM line
                    drawPath(
                        path = ramPath,
                        color = AccentLavender,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw Temp line
                    drawPath(
                        path = tempPath,
                        color = WarningOrange,
                        style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw Data Points & Selection Highlight
                    points.forEachIndexed { index, p ->
                        val x = index * stepX
                        val ramNorm = (p.ramUsagePercent / 100f).coerceIn(0f, 1f)
                        val ramY = height - (ramNorm * (height * 0.8f) + height * 0.1f)

                        val tempNorm = ((p.batteryTempC - 20f) / 25f).coerceIn(0f, 1f)
                        val tempY = height - (tempNorm * (height * 0.8f) + height * 0.1f)

                        val isSelected = index == selectedPointIndex

                        // RAM Point
                        drawCircle(
                            color = AccentLavender,
                            radius = if (isSelected) 5.dp.toPx() else 3.dp.toPx(),
                            center = Offset(x, ramY)
                        )

                        // Temp Point
                        drawCircle(
                            color = WarningOrange,
                            radius = if (isSelected) 5.dp.toPx() else 3.dp.toPx(),
                            center = Offset(x, tempY)
                        )

                        if (isSelected) {
                            // Vertical Selection Indicator
                            drawLine(
                                color = Color.White.copy(alpha = 0.4f),
                                start = Offset(x, 0f),
                                end = Offset(x, height),
                                strokeWidth = 1.dp.toPx(),
                                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Point Inspector Footer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(HighDensityCardElevated)
                    .border(1.dp, HighDensityBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SESSION ${selectedPoint.sessionLabel}:",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "RAM ${selectedPoint.ramUsagePercent.toInt()}%",
                        color = AccentLavender,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "TEMP ${String.format("%.1f°C", selectedPoint.batteryTempC)}",
                        color = if (selectedPoint.batteryTempC > 38f) CriticalRed else WarningOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${selectedPoint.latencyMs} ms",
                    color = StatusGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
