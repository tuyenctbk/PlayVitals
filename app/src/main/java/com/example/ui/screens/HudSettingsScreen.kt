package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HudSettings
import com.example.data.model.HudVisualStyle
import com.example.data.model.ThemeMode
import com.example.service.HudOverlayContent
import com.example.ui.theme.*
import com.example.viewmodel.ActiveScreen
import com.example.viewmodel.PlayVitalsViewModel

@Composable
fun HudSettingsScreen(
    viewModel: PlayVitalsViewModel,
    hudSettings: HudSettings,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    var hasOverlayPermission by remember {
        mutableStateOf(Settings.canDrawOverlays(context))
    }

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
                        .testTag("hud_settings_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.nav_hud_settings),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Overlay Permission Banner if not granted
            if (!hasOverlayPermission) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2E2412))
                        .border(1.dp, WarningAmber, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = WarningAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Display Over Other Apps Permission",
                                color = WarningAmber,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "The live HUD floating panel requires this permission to render metrics over your games.",
                            color = TextPrimary,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                val intent = Intent(
                                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                    Uri.parse("package:${context.packageName}")
                                )
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WarningAmber, contentColor = Color.Black),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(32.dp)
                                .tvFocusHighlight(shape = RoundedCornerShape(6.dp))
                        ) {
                            Text(stringResource(R.string.btn_grant_permission), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Define composable sections to arrange responsively
            val sectionMasterSwitch = @Composable {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                            Text(
                                text = "Show Game HUD while playing",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "A small floating panel appears over your game with live device readings, and disappears when you leave the game.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }

                        Switch(
                            checked = hudSettings.isHudMasterEnabled,
                            onCheckedChange = { viewModel.updateHudSettings { s -> s.copy(isHudMasterEnabled = it) } },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonGreen,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = DarkSurfaceElevated
                            ),
                            modifier = Modifier
                                .tvFocusHighlight(shape = RoundedCornerShape(12.dp))
                                .testTag("hud_master_switch")
                        )
                    }
                }
            }

            val sectionAutoLimiter = @Composable {
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
                            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                Text(
                                    text = "Auto Eco Refresh Rate Limiter",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Automatically caps device refresh rate to 60 Hz when battery drops below low power threshold during gameplay.",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }

                            Switch(
                                checked = hudSettings.isAutoRefreshRateLimiterEnabled,
                                onCheckedChange = { viewModel.updateHudSettings { s -> s.copy(isAutoRefreshRateLimiterEnabled = it) } },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = NeonGreen,
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = DarkSurfaceElevated
                                ),
                                modifier = Modifier
                                    .tvFocusHighlight(shape = RoundedCornerShape(12.dp))
                                    .testTag("auto_refresh_rate_limiter_switch")
                            )
                        }

                        if (hudSettings.isAutoRefreshRateLimiterEnabled) {
                            Spacer(modifier = Modifier.height(10.dp))
                            HorizontalDivider(color = DarkBorder, thickness = 1.dp)
                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Low Battery Threshold: ${hudSettings.lowBatteryRefreshRateThreshold}%",
                                color = NeonGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Slider(
                                value = hudSettings.lowBatteryRefreshRateThreshold.toFloat(),
                                onValueChange = { viewModel.updateHudSettings { s -> s.copy(lowBatteryRefreshRateThreshold = it.toInt()) } },
                                valueRange = 10f..40f,
                                steps = 5,
                                colors = SliderDefaults.colors(
                                    thumbColor = NeonGreen,
                                    activeTrackColor = NeonGreen,
                                    inactiveTrackColor = DarkSurfaceElevated
                                ),
                                modifier = Modifier.tvFocusHighlight(shape = RoundedCornerShape(12.dp))
                            )
                        }
                    }
                }
            }

            val sectionAppTheme = @Composable {
                Column {
                    Text(
                        text = "APP THEME & SYSTEM MODE",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Set system-aware automatic theme switching or force dark / light appearance.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ThemeMode.values().forEach { mode ->
                            val isSelected = hudSettings.themeMode == mode
                            val cardBg = if (isSelected) DarkSurfaceElevated else DarkSurface
                            val borderColor = if (isSelected) NeonGreen else DarkBorder

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(cardBg)
                                    .border(if (isSelected) 1.5.dp else 1.dp, borderColor, RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.updateHudSettings { s -> s.copy(themeMode = mode) }
                                    }
                                    .tvFocusHighlight(shape = RoundedCornerShape(10.dp))
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = mode.displayName,
                                        color = if (isSelected) TextPrimary else TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = when (mode) {
                                            ThemeMode.SYSTEM -> "System (Auto)"
                                            ThemeMode.DARK -> "Dark Mode"
                                            ThemeMode.LIGHT -> "Light Mode"
                                        },
                                        color = if (isSelected) NeonGreen else TextTertiary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }

            val sectionVisualStyle = @Composable {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = NeonCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HUD VISUAL STYLE",
                            color = NeonCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Select a theme for the floating panel and overlay highlights.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        HudVisualStyle.values().forEach { styleOption ->
                            val isSelected = hudSettings.visualStyle == styleOption
                            val activeBorder = if (isSelected) styleOption.primaryColor else DarkBorder
                            val cardBg = if (isSelected) DarkSurfaceElevated else DarkSurface

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(cardBg)
                                    .border(if (isSelected) 1.5.dp else 1.dp, activeBorder, RoundedCornerShape(10.dp))
                                    .clickable {
                                        viewModel.updateHudSettings { s -> s.copy(visualStyle = styleOption) }
                                    }
                                    .tvFocusHighlight(shape = RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        // Swatch
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .clip(CircleShape)
                                                .background(styleOption.backgroundColor)
                                                .border(1.5.dp, styleOption.primaryColor, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(8.dp)
                                                    .clip(CircleShape)
                                                    .background(styleOption.primaryColor)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = styleOption.displayName,
                                                color = if (isSelected) TextPrimary else TextSecondary,
                                                fontSize = 12.sp,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = styleOption.description,
                                                color = TextSecondary,
                                                fontSize = 10.sp,
                                                lineHeight = 13.sp
                                            )
                                        }
                                    }

                                    if (isSelected) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = "Selected",
                                            tint = styleOption.primaryColor,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            val sectionBackgroundOpacity = @Composable {
                Column {
                    Text(
                        text = "BACKGROUND OPACITY & TRANSPARENCY",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Adjust the floating panel alpha so game graphics remain visible underneath.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Opacity Quick Presets
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(0.3f, 0.5f, 0.75f, 0.9f, 1.0f).forEach { opacityVal ->
                            val label = "${(opacityVal * 100).toInt()}%"
                            val isCurrent = Math.abs(hudSettings.backgroundOpacity - opacityVal) < 0.05f
                            val chipBg = if (isCurrent) hudSettings.visualStyle.primaryColor.copy(alpha = 0.2f) else DarkSurface
                            val chipBorder = if (isCurrent) hudSettings.visualStyle.primaryColor else DarkBorder

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(32.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(chipBg)
                                    .border(1.dp, chipBorder, RoundedCornerShape(6.dp))
                                    .clickable {
                                        viewModel.updateHudSettings { s -> s.copy(backgroundOpacity = opacityVal) }
                                    }
                                    .tvFocusHighlight(shape = RoundedCornerShape(6.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    color = if (isCurrent) hudSettings.visualStyle.primaryColor else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Slider(
                        value = hudSettings.backgroundOpacity,
                        onValueChange = { viewModel.updateHudSettings { s -> s.copy(backgroundOpacity = it) } },
                        valueRange = 0.2f..1.0f,
                        colors = SliderDefaults.colors(
                            thumbColor = hudSettings.visualStyle.primaryColor,
                            activeTrackColor = hudSettings.visualStyle.primaryColor,
                            inactiveTrackColor = DarkSurfaceElevated
                        ),
                        modifier = Modifier.tvFocusHighlight(shape = RoundedCornerShape(12.dp))
                    )
                }
            }

            val sectionReadingsShown = @Composable {
                Column {
                    Text(
                        text = "READINGS SHOWN",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        HudReadingCheckbox(
                            title = "Free RAM",
                            checked = hudSettings.showFreeRam,
                            onCheckedChange = { viewModel.updateHudSettings { s -> s.copy(showFreeRam = it) } }
                        )
                        HudReadingCheckbox(
                            title = "Battery temp",
                            checked = hudSettings.showBatteryTemp,
                            onCheckedChange = { viewModel.updateHudSettings { s -> s.copy(showBatteryTemp = it) } }
                        )
                        HudReadingCheckbox(
                            title = "Battery level",
                            checked = hudSettings.showBatteryLevel,
                            onCheckedChange = { viewModel.updateHudSettings { s -> s.copy(showBatteryLevel = it) } }
                        )
                        HudReadingCheckbox(
                            title = "Network latency",
                            checked = hudSettings.showNetworkLatency,
                            onCheckedChange = { viewModel.updateHudSettings { s -> s.copy(showNetworkLatency = it) } }
                        )
                        HudReadingCheckbox(
                            title = "Screen refresh rate",
                            checked = hudSettings.showScreenRefreshRate,
                            onCheckedChange = { viewModel.updateHudSettings { s -> s.copy(showScreenRefreshRate = it) } }
                        )
                    }
                }
            }

            val sectionAlertThresholds = @Composable {
                Column {
                    Text(
                        text = "ALERT THRESHOLDS",
                        color = NeonGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "HUD metrics highlight amber when readings cross safe limits.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "Low Free RAM alert: < ${hudSettings.warnLowRamThreshold}%",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "High Battery Temp alert: > ${String.format("%.0f°C", hudSettings.warnHighTempThreshold)}",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            val sectionCloudSync = @Composable {
                val isSyncing by viewModel.isCloudSyncing.collectAsState()
                val syncMsg by viewModel.cloudSyncMessage.collectAsState()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(HighDensityCard)
                        .border(1.dp, HighDensityBorder, RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "FIREBASE FIRESTORE CLOUD SYNC",
                                    color = NeonCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Sync game session history and HUD preferences across devices.",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }

                            Button(
                                onClick = { viewModel.syncGameSessionsAndSettingsWithCloud() },
                                enabled = !isSyncing,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HighDensityCardElevated,
                                    contentColor = NeonCyan
                                ),
                                shape = RoundedCornerShape(8.dp),
                                border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(NeonCyan.copy(alpha = 0.5f))),
                                modifier = Modifier
                                    .height(36.dp)
                                    .tvFocusHighlight(shape = RoundedCornerShape(8.dp))
                            ) {
                                if (isSyncing) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(14.dp),
                                        color = NeonCyan,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(stringResource(R.string.sync_now), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (syncMsg != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = syncMsg ?: "",
                                color = StatusGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        HorizontalDivider(color = HighDensityBorder, thickness = 1.dp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = stringResource(R.string.manual_room_sync),
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.backupRoomToCloud() },
                                enabled = !isSyncing,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HighDensityCardElevated,
                                    contentColor = NeonGreen
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .tvFocusHighlight(shape = RoundedCornerShape(8.dp)),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(stringResource(R.string.backup_cloud), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.restoreRoomFromCloud() },
                                enabled = !isSyncing,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = HighDensityCardElevated,
                                    contentColor = WarningAmber
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(36.dp)
                                    .tvFocusHighlight(shape = RoundedCornerShape(8.dp)),
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(stringResource(R.string.restore_cloud), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            val sectionLivePreview = @Composable {
                Column {
                    Text(
                        text = stringResource(R.string.live_hud_preview),
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        HudOverlayContent(
                            gameTitle = "Ember Drift",
                            freeRamPercent = 49,
                            freeRamGb = "1.9 GB",
                            latencyMs = 45,
                            refreshRateHz = 60,
                            batteryTempC = 25f,
                            elapsedSeconds = 24L,
                            minRamPercent = 46,
                            peakTempC = 25f,
                            isMinimized = false,
                            hudSettings = hudSettings,
                            onToggleMinimize = {},
                            onFinish = { viewModel.navigateTo(ActiveScreen.DASHBOARD) },
                            onDrag = { _, _ -> }
                        )
                    }
                }
            }

            // Adaptive layout rendering
            if (isWide) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1.2f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        sectionMasterSwitch()
                        sectionAutoLimiter()
                        sectionAppTheme()
                        sectionBackgroundOpacity()
                        sectionAlertThresholds()
                    }

                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        sectionVisualStyle()
                        sectionReadingsShown()
                        sectionCloudSync()
                        sectionLivePreview()
                    }
                }
            } else {
                // Mobile layout
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    sectionMasterSwitch()
                    sectionAutoLimiter()
                    sectionAppTheme()
                    sectionVisualStyle()
                    sectionBackgroundOpacity()
                    sectionReadingsShown()
                    sectionAlertThresholds()
                    sectionCloudSync()
                    sectionLivePreview()
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun HudReadingCheckbox(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(DarkSurface)
            .clickable { onCheckedChange(!checked) }
            .tvFocusHighlight(shape = RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = NeonGreen,
                checkmarkColor = Color(0xFF003816),
                uncheckedColor = TextSecondary
            ),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
