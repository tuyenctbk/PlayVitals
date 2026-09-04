package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameItem
import com.example.data.model.GameSession
import com.example.system.LiveDeviceStats
import com.example.ui.components.GameIconBadge
import com.example.ui.components.LivePingWaveform
import com.example.ui.components.NeonCircularGauge
import com.example.ui.components.SessionPerformanceTrendChart
import com.example.ui.theme.*
import com.example.viewmodel.ActiveScreen
import com.example.viewmodel.PlayVitalsViewModel
import com.example.viewmodel.GameModeState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    viewModel: PlayVitalsViewModel,
    liveStats: LiveDeviceStats,
    launcherGames: List<GameItem>,
    gameModeState: GameModeState,
    bestReactionMs: Long?,
    allSessions: List<GameSession> = emptyList(),
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val isSimulating by viewModel.isSimulatedGamingSession.collectAsState()
    val simElapsed by viewModel.simulatedElapsedSeconds.collectAsState()
    val selectedGame by viewModel.selectedGame.collectAsState()
    val hudSettings by viewModel.hudSettings.collectAsState()

    var isBoosting by remember { mutableStateOf(false) }
    var boostFeedback by remember { mutableStateOf<String?>(null) }
    var selectedFilterTag by remember { mutableStateOf("ALL") }
    val coroutineScope = rememberCoroutineScope()

    val storageUsedGb = liveStats.storageUsedBytes / (1024.0 * 1024.0 * 1024.0)
    val storageTotalGb = liveStats.storageTotalBytes / (1024.0 * 1024.0 * 1024.0)

    val availableTags = remember(launcherGames) {
        val tags = launcherGames.flatMap { it.tagList }.distinct().sorted()
        if (tags.isEmpty()) listOf("Competitive", "Relaxing", "Offline") else tags
    }

    val filterChips = remember(launcherGames, availableTags) {
        listOf("ALL (${launcherGames.size})", "FAVORITES") + availableTags
    }

    val displayedGames = remember(launcherGames, selectedFilterTag) {
        when {
            selectedFilterTag.startsWith("ALL") -> launcherGames
            selectedFilterTag == "FAVORITES" -> launcherGames.filter { it.isFavorite }
            else -> launcherGames.filter { game -> game.tagList.contains(selectedFilterTag) }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(HighDensityCanvas)
    ) {
        val isWide = maxWidth >= 720.dp

        // Declarations of components for modular, responsive layout
        val performanceMetricsCard = @Composable {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(HighDensityCard)
                    .border(1.dp, HighDensityBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "PERFORMANCE METRICS",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (gameModeState.isGameModeEnabled) StatusGreen.copy(alpha = 0.15f) else HighDensityCardElevated)
                                .border(
                                    1.dp,
                                    if (gameModeState.isGameModeEnabled) StatusGreen.copy(alpha = 0.4f) else HighDensityBorder,
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (gameModeState.isGameModeEnabled) StatusGreen else TextTertiary)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (gameModeState.isGameModeEnabled) "GAME MODE ON" else "STANDARD",
                                color = if (gameModeState.isGameModeEnabled) StatusGreen else TextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Circular Free RAM Gauge
                        NeonCircularGauge(
                            value = liveStats.freeRamPercent,
                            maxValue = 100,
                            label = "FREE RAM",
                            size = 104.dp,
                            strokeWidth = 8.5.dp,
                            primaryColor = StatusGreen
                        )

                        Spacer(modifier = Modifier.width(16.dp))

                        // Linear Metric Bars
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Storage
                            MetricBarItem(
                                label = "Storage: ${String.format("%.1f/%.1f GB", storageUsedGb, storageTotalGb)} (${liveStats.storageUsedPercent}%)",
                                fraction = (liveStats.storageUsedPercent / 100f).coerceIn(0f, 1f),
                                barColor = WarningOrange
                            )

                            // Battery
                            MetricBarItem(
                                label = "Battery: ${liveStats.batteryLevel}%${if (liveStats.isCharging) " ⚡" else ""}",
                                fraction = (liveStats.batteryLevel / 100f).coerceIn(0f, 1f),
                                barColor = AccentLavender
                            )

                            // Battery Temp
                            val tempFraction = ((liveStats.batteryTempC - 20) / 30f).coerceIn(0.1f, 1f)
                            val tempColor = if (liveStats.batteryTempC > 38f) CriticalRed else StatusGreen
                            MetricBarItem(
                                label = "Thermals: ${String.format("%.0f°C", liveStats.batteryTempC)} ${if (liveStats.batteryTempC <= 36f) "(Cool)" else "(Warm)"}",
                                fraction = tempFraction,
                                barColor = tempColor
                            )
                        }
                    }
                }
            }
        }

        val actionButtonsGrid = @Composable {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardActionCard(
                        title = "GAME MODE",
                        subtitle = if (gameModeState.isGameModeEnabled) "ON" else "OFF",
                        subtitleColor = if (gameModeState.isGameModeEnabled) StatusGreen else TextSecondary,
                        icon = Icons.Default.SportsEsports,
                        modifier = Modifier.weight(1f).testTag("game_mode_card"),
                        onClick = { viewModel.navigateTo(ActiveScreen.GAME_MODE) }
                    )
                    DashboardActionCard(
                        title = "GAME INSIGHTS",
                        subtitle = "HISTORY",
                        subtitleColor = AccentLavender,
                        icon = Icons.Default.Analytics,
                        modifier = Modifier.weight(1f).testTag("game_insights_card"),
                        onClick = { viewModel.navigateTo(ActiveScreen.GAME_INSIGHTS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    DashboardActionCard(
                        title = "GFX GUIDE",
                        subtitle = "FPS TUNER",
                        subtitleColor = AccentLavender,
                        icon = Icons.Default.Speed,
                        modifier = Modifier.weight(1f).testTag("gfx_guide_card"),
                        onClick = { viewModel.navigateTo(ActiveScreen.GFX_GUIDE) }
                    )
                    DashboardActionCard(
                        title = "GAME HUD",
                        subtitle = if (hudSettings.isHudMasterEnabled) "ON" else "OFF",
                        subtitleColor = if (hudSettings.isHudMasterEnabled) StatusGreen else TextSecondary,
                        icon = Icons.Default.Layers,
                        modifier = Modifier.weight(1f).testTag("game_hud_card"),
                        onClick = { viewModel.navigateTo(ActiveScreen.HUD_SETTINGS) }
                    )
                }
            }
        }

        val pingWaveform = @Composable {
            LivePingWaveform(
                pingHistory = liveStats.pingHistory,
                currentPingMs = liveStats.networkLatencyMs
            )
        }

        val performanceTrend = @Composable {
            SessionPerformanceTrendChart(
                sessions = allSessions
            )
        }

        val reflexTrainerBanner = @Composable {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                AccentPurpleDark,
                                HighDensityCard
                            )
                        )
                    )
                    .border(1.dp, AccentLavender.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
                    .clickable { viewModel.navigateTo(ActiveScreen.REFLEX_TRAINER) }
                    .tvFocusHighlight(shape = RoundedCornerShape(14.dp))
                    .padding(horizontal = 14.dp, vertical = 14.dp)
                    .testTag("reflex_trainer_banner")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AccentPurple)
                                .border(1.dp, AccentLavender, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Bolt,
                                contentDescription = null,
                                tint = AccentLavender,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.reflex_speed_lab),
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp
                            )
                            Text(
                                text = stringResource(R.string.reflex_speed_desc),
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (bestReactionMs != null) "$bestReactionMs MS" else stringResource(R.string.play_drill),
                            color = StatusGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Open Reflex Trainer",
                            tint = StatusGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(StatusGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "PLAYVITALS",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = stringResource(R.string.no_ads_tagline),
                        color = AccentLavender,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Rate & Share Button
                    IconButton(
                        onClick = { viewModel.openRateShareDialogManually() },
                        modifier = Modifier
                            .testTag("rate_share_button")
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(WarningAmber.copy(alpha = 0.2f))
                            .border(1.dp, WarningAmber.copy(alpha = 0.5f), CircleShape)
                            .tvFocusHighlight(shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Rate & Share",
                            tint = WarningAmber,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Onboarding / Help Guide Button
                    IconButton(
                        onClick = { viewModel.openOnboarding() },
                        modifier = Modifier
                            .testTag("onboarding_help_button")
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.2f))
                            .border(1.dp, NeonCyan.copy(alpha = 0.5f), CircleShape)
                            .tvFocusHighlight(shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "Onboarding Guide",
                            tint = NeonCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    // Turbo Clean RAM Button
                    IconButton(
                        onClick = {
                            if (!isBoosting) {
                                isBoosting = true
                                viewModel.cleanBackgroundProcesses()
                                viewModel.recordBoostAction()
                                coroutineScope.launch {
                                    delay(600)
                                    isBoosting = false
                                    val optResult = gameModeState.lastOptimizationResult
                                    boostFeedback = if (optResult != null && optResult.freedMemoryMb > 0) {
                                        "Memory Optimized • +${optResult.freedMemoryMb} MB Freed"
                                    } else {
                                        "Memory Trimmed • Ready for Gaming"
                                    }
                                    delay(3000)
                                    boostFeedback = null
                                }
                            }
                        },
                        modifier = Modifier
                            .testTag("turbo_boost_button")
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AccentPurple.copy(alpha = 0.6f))
                            .border(1.dp, AccentLavender.copy(alpha = 0.5f), CircleShape)
                            .tvFocusHighlight(shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = "Turbo Boost",
                            tint = AccentLavender,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = { viewModel.navigateTo(ActiveScreen.HUD_SETTINGS) },
                        modifier = Modifier
                            .testTag("settings_button")
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(HighDensityCardElevated)
                            .border(1.dp, HighDensityBorder, CircleShape)
                            .tvFocusHighlight(shape = CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Quick Feedback Toast / Banner
            AnimatedVisibility(
                visible = boostFeedback != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(StatusGreen.copy(alpha = 0.15f))
                        .border(1.dp, StatusGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = StatusGreen,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = boostFeedback ?: "",
                            color = StatusGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // In-App Simulated Active Session Banner (if running)
            if (isSimulating && selectedGame != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E2822))
                        .border(1.5.dp, StatusGreen, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            GameIconBadge(
                                presetIndex = selectedGame!!.iconPresetIndex,
                                title = selectedGame!!.displayName,
                                size = 36.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "PLAYING: ${selectedGame!!.displayName}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Active • ${simElapsed / 60}m ${simElapsed % 60}s",
                                    color = StatusGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Button(
                            onClick = { viewModel.finishSimulatedGamingSession() },
                            colors = ButtonDefaults.buttonColors(containerColor = StatusGreen, contentColor = Color(0xFF003816)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                            modifier = Modifier.height(34.dp).tvFocusHighlight(shape = RoundedCornerShape(8.dp))
                        ) {
                            Text(stringResource(R.string.btn_finish), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Adaptive Split Row Layout or Single Column Layout
            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1.1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        performanceMetricsCard()
                        actionButtonsGrid()
                    }
                    Column(
                        modifier = Modifier.weight(0.9f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        reflexTrainerBanner()
                        pingWaveform()
                        performanceTrend()
                    }
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    performanceMetricsCard()
                    actionButtonsGrid()
                    pingWaveform()
                    performanceTrend()
                    reflexTrainerBanner()
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

        // PlayVitals Shelf Header & Filter Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.my_games),
                color = AccentLavender,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.manage),
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(HighDensityCardElevated)
                        .border(1.dp, HighDensityBorder, RoundedCornerShape(6.dp))
                        .clickable { viewModel.navigateTo(ActiveScreen.MANAGE_GAMES) }
                        .tvFocusHighlight(shape = RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                        .testTag("manage_games_button")
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Category & Tag Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(filterChips) { chipTitle ->
                val isSelected = selectedFilterTag == chipTitle
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSelected) AccentLavender else HighDensityCard)
                        .border(1.dp, if (isSelected) AccentLavender else HighDensityBorder, RoundedCornerShape(16.dp))
                        .clickable { selectedFilterTag = chipTitle }
                        .tvFocusHighlight(shape = RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = chipTitle,
                        color = if (isSelected) AccentPurpleDark else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Games Row
        if (displayedGames.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(HighDensityCard)
                    .border(1.dp, HighDensityBorder, RoundedCornerShape(12.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsEsports,
                        contentDescription = null,
                        tint = TextTertiary,
                        modifier = Modifier.size(36.dp)
                    )
                    Text(
                        text = if (launcherGames.isEmpty()) "No games on your launcher shelf yet." else if (selectedFilterTag == "FAVORITES") stringResource(R.string.no_favorites_yet) else "No games match tag '$selectedFilterTag'.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Button(
                        onClick = { viewModel.navigateTo(ActiveScreen.MANAGE_GAMES) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = HighDensityCardElevated,
                            contentColor = AccentLavender
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(AccentLavender.copy(alpha = 0.5f))),
                        modifier = Modifier.tvFocusHighlight(shape = RoundedCornerShape(8.dp))
                    ) {
                        Text(
                            text = if (launcherGames.isEmpty()) "ADD INSTALLED APPS" else stringResource(R.string.tap_manage_hint),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(displayedGames, key = { it.packageName }) { game ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(HighDensityCard.copy(alpha = 0.6f))
                            .border(1.dp, HighDensityBorder, RoundedCornerShape(12.dp))
                            .clickable { viewModel.openGameDetail(game) }
                            .tvFocusHighlight(shape = RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                            .width(96.dp)
                    ) {
                        GameIconBadge(
                            presetIndex = game.iconPresetIndex,
                            title = game.displayName,
                            size = 52.dp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = game.displayName,
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (game.lastPlayedTimestamp > 0) "${game.totalPlaytimeMinutes}m played" else "Ready",
                            color = StatusGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Medium
                        )

                        // Render primary tag badge if present
                        if (game.tagList.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AccentLavender.copy(alpha = 0.15f))
                                    .border(0.8.dp, AccentLavender.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = game.tagList.first(),
                                    color = AccentLavender,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MetricBarItem(
    label: String,
    fraction: Float,
    barColor: Color
) {
    Column {
        Text(
            text = label,
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(HighDensityCardElevated)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(barColor)
            )
        }
    }
}

@Composable
private fun DashboardActionCard(
    title: String,
    subtitle: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    subtitleColor: Color = TextSecondary,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(HighDensityCard)
            .border(1.dp, HighDensityBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = subtitleColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        color = subtitleColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
