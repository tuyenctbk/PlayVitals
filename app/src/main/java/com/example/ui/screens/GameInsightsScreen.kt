package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.data.model.GameItem
import com.example.data.model.GameSession
import com.example.system.LiveDeviceStats
import com.example.ui.theme.*
import com.example.viewmodel.ActiveScreen
import com.example.viewmodel.PlayVitalsViewModel

@Composable
fun GameInsightsScreen(
    viewModel: PlayVitalsViewModel,
    liveStats: LiveDeviceStats,
    allGames: List<GameItem>,
    allSessions: List<GameSession>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val totalPlaytimeMinutes = (allGames.sumOf { it.totalPlaytimeMillis } / 60000L).coerceAtLeast(0)
    val totalSessionsCount = (allGames.sumOf { it.sessionCount }).coerceAtLeast(allSessions.size)
    val mostPlayedGame = allGames.maxByOrNull { it.totalPlaytimeMillis }

    val freeStoragePercent = 100 - liveStats.storageUsedPercent

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(DarkCanvas)
    ) {
        val isWide = maxWidth >= 720.dp

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(ActiveScreen.DASHBOARD) },
                    modifier = Modifier
                        .size(36.dp)
                        .tvFocusHighlight(shape = CircleShape)
                        .testTag("insights_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.game_insights),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "What your recorded play sessions show, plus how your device is right now. A button appears only where this app can actually do something.",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Total Playtime
                        InsightCard(
                            title = "Total tracked playtime",
                            value = "${totalPlaytimeMinutes}m",
                            description = "Across $totalSessionsCount recorded sessions.",
                            valueColor = NeonGreen
                        )

                        // Most Played
                        InsightCard(
                            title = "Most played",
                            value = if (mostPlayedGame != null) "${(mostPlayedGame.totalPlaytimeMillis / 60000L)}m" else "0m",
                            description = if (mostPlayedGame != null) "${mostPlayedGame.displayName} has the most tracked playtime." else "No sessions recorded yet.",
                            valueColor = NeonGreen
                        )

                        // Latency Diagnostic
                        val isLatencyHigh = liveStats.networkLatencyMs > 100
                        InsightCard(
                            title = if (isLatencyHigh) "Network latency is high" else "Network latency is optimal",
                            value = "${liveStats.networkLatencyMs} ms",
                            description = "Average latency to a public DNS server across your sessions. This app cannot change your connection; a closer Wi-Fi point or a different network is what helps.",
                            valueColor = if (isLatencyHigh) WarningAmber else NeonGreen
                        )
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Free RAM right now + Action button
                        InsightCard(
                            title = "Free RAM right now",
                            value = "${liveStats.freeRamPercent}%",
                            description = if (liveStats.freeRamPercent > 25) "Plenty of memory free for gaming." else "RAM is heavily committed.",
                            valueColor = if (liveStats.freeRamPercent > 25) NeonGreen else WarningAmber,
                            actionButton = {
                                Button(
                                    onClick = { viewModel.cleanBackgroundProcesses() },
                                    colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated, contentColor = NeonGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(NeonGreen)),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                    modifier = Modifier
                                        .padding(top = 8.dp)
                                        .tvFocusHighlight(shape = RoundedCornerShape(8.dp))
                                ) {
                                    Text(text = stringResource(R.string.clear_memory), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        )

                        // Battery Temperature
                        val isTempHigh = liveStats.batteryTempC > 38f
                        InsightCard(
                            title = "Battery temperature right now",
                            value = String.format("%.0f°C", liveStats.batteryTempC),
                            description = if (isTempHigh) "Battery is warm. Avoid fast charging while gaming." else "Battery temperature is normal.",
                            valueColor = if (isTempHigh) WarningAmber else NeonGreen
                        )

                        // Free Storage
                        InsightCard(
                            title = "Free storage right now",
                            value = "$freeStoragePercent%",
                            description = if (freeStoragePercent > 15) "Enough free storage for shaders and game caches." else "Storage is running low.",
                            valueColor = NeonGreen
                        )
                    }
                }
            } else {
                // Mobile Portrait Column List
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Total Playtime
                    InsightCard(
                        title = "Total tracked playtime",
                        value = "${totalPlaytimeMinutes}m",
                        description = "Across $totalSessionsCount recorded sessions.",
                        valueColor = NeonGreen
                    )

                    // Most Played
                    InsightCard(
                        title = "Most played",
                        value = if (mostPlayedGame != null) "${(mostPlayedGame.totalPlaytimeMillis / 60000L)}m" else "0m",
                        description = if (mostPlayedGame != null) "${mostPlayedGame.displayName} has the most tracked playtime." else "No sessions recorded yet.",
                        valueColor = NeonGreen
                    )

                    // Latency Diagnostic
                    val isLatencyHigh = liveStats.networkLatencyMs > 100
                    InsightCard(
                        title = if (isLatencyHigh) "Network latency is high" else "Network latency is optimal",
                        value = "${liveStats.networkLatencyMs} ms",
                        description = "Average latency to a public DNS server across your sessions. This app cannot change your connection; a closer Wi-Fi point or a different network is what helps.",
                        valueColor = if (isLatencyHigh) WarningAmber else NeonGreen
                    )

                    // Free RAM right now + Action button
                    InsightCard(
                        title = "Free RAM right now",
                        value = "${liveStats.freeRamPercent}%",
                        description = if (liveStats.freeRamPercent > 25) "Plenty of memory free for gaming." else "RAM is heavily committed.",
                        valueColor = if (liveStats.freeRamPercent > 25) NeonGreen else WarningAmber,
                        actionButton = {
                            Button(
                                onClick = { viewModel.cleanBackgroundProcesses() },
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceElevated, contentColor = NeonGreen),
                                shape = RoundedCornerShape(8.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(NeonGreen)),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .tvFocusHighlight(shape = RoundedCornerShape(8.dp))
                            ) {
                                Text(text = stringResource(R.string.clear_memory), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    )

                    // Battery Temperature
                    val isTempHigh = liveStats.batteryTempC > 38f
                    InsightCard(
                        title = "Battery temperature right now",
                        value = String.format("%.0f°C", liveStats.batteryTempC),
                        description = if (isTempHigh) "Battery is warm. Avoid fast charging while gaming." else "Battery temperature is normal.",
                        valueColor = if (isTempHigh) WarningAmber else NeonGreen
                    )

                    // Free Storage
                    InsightCard(
                        title = "Free storage right now",
                        value = "$freeStoragePercent%",
                        description = if (freeStoragePercent > 15) "Enough free storage for shaders and game caches." else "Storage is running low.",
                        valueColor = NeonGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun InsightCard(
    title: String,
    value: String,
    description: String,
    valueColor: Color,
    actionButton: (@Composable () -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = value,
                    color = valueColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )
            actionButton?.invoke()
        }
    }
}
