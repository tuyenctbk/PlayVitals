package com.example.ui.screens

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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.ui.theme.*
import com.example.viewmodel.ActiveScreen
import com.example.viewmodel.PlayVitalsViewModel
import com.example.viewmodel.GameModeState

@Composable
fun GameModeScreen(
    viewModel: PlayVitalsViewModel,
    gameModeState: GameModeState,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

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
                        .testTag("game_mode_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.game_mode_title),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp)
                ) {
                    // Left column: Master Toggle and Status readouts
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (gameModeState.isGameModeEnabled) stringResource(R.string.game_mode_on) else stringResource(R.string.game_mode_off),
                            color = if (gameModeState.isGameModeEnabled) NeonGreen else TextSecondary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Switch(
                            checked = gameModeState.isGameModeEnabled,
                            onCheckedChange = { viewModel.toggleGameMode(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = NeonGreen,
                                uncheckedThumbColor = TextSecondary,
                                uncheckedTrackColor = DarkSurfaceElevated
                            ),
                            modifier = Modifier
                                .testTag("game_mode_master_switch")
                                .tvFocusHighlight(shape = CircleShape)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Releasing background processes status
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = stringResource(R.string.releasing_bg_proc),
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider(color = NeonGreen, thickness = 2.dp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = gameModeState.lastOptimizationResult?.message ?: "Memory: managed by Android on this version",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // Switching notifications status
                            Text(
                                text = stringResource(R.string.switching_notif),
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            HorizontalDivider(color = NeonGreen, thickness = 2.dp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = if (gameModeState.isGameModeEnabled && gameModeState.doNotDisturbEnabled) stringResource(R.string.notif_silenced) else stringResource(R.string.notif_active),
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Right column: Individual settings card
                    Column(
                        modifier = Modifier.weight(1.2f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.while_game_mode_on),
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        // Settings Cards
                        GameModeToggleCard(
                            title = stringResource(R.string.clear_memory),
                            description = stringResource(R.string.clear_memory_desc),
                            checked = gameModeState.clearMemoryEnabled,
                            onCheckedChange = { viewModel.toggleClearMemory(it) }
                        )

                        GameModeToggleCard(
                            title = stringResource(R.string.dnd),
                            description = stringResource(R.string.dnd_desc),
                            checked = gameModeState.doNotDisturbEnabled,
                            onCheckedChange = { viewModel.toggleDoNotDisturb(it) }
                        )

                        GameModeToggleCard(
                            title = stringResource(R.string.keep_awake),
                            description = stringResource(R.string.keep_awake_desc),
                            checked = gameModeState.keepScreenAwakeEnabled,
                            onCheckedChange = { viewModel.toggleKeepScreenAwake(it) }
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Disclaimer Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurface)
                                .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                                .padding(14.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.game_mode_disclaimer),
                                color = TextSecondary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            } else {
                // Mobile layout
                // Status Header & Master Toggle
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (gameModeState.isGameModeEnabled) stringResource(R.string.game_mode_on) else stringResource(R.string.game_mode_off),
                        color = if (gameModeState.isGameModeEnabled) NeonGreen else TextSecondary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Switch(
                        checked = gameModeState.isGameModeEnabled,
                        onCheckedChange = { viewModel.toggleGameMode(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = NeonGreen,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = DarkSurfaceElevated
                        ),
                        modifier = Modifier
                            .testTag("game_mode_master_switch")
                            .tvFocusHighlight(shape = CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // While Game Mode is on section
                Text(
                    text = stringResource(R.string.while_game_mode_on),
                    color = NeonGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Settings Cards
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Clear Memory
                    GameModeToggleCard(
                        title = stringResource(R.string.clear_memory),
                        description = stringResource(R.string.clear_memory_desc),
                        checked = gameModeState.clearMemoryEnabled,
                        onCheckedChange = { viewModel.toggleClearMemory(it) }
                    )

                    // Do Not Disturb
                    GameModeToggleCard(
                        title = stringResource(R.string.dnd),
                        description = stringResource(R.string.dnd_desc),
                        checked = gameModeState.doNotDisturbEnabled,
                        onCheckedChange = { viewModel.toggleDoNotDisturb(it) }
                    )

                    // Keep Screen Awake
                    GameModeToggleCard(
                        title = stringResource(R.string.keep_awake),
                        description = stringResource(R.string.keep_awake_desc),
                        checked = gameModeState.keepScreenAwakeEnabled,
                        onCheckedChange = { viewModel.toggleKeepScreenAwake(it) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Disclaimer Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = stringResource(R.string.game_mode_disclaimer),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Releasing background processes status
                Text(
                    text = stringResource(R.string.releasing_bg_proc),
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = NeonGreen, thickness = 2.dp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = gameModeState.lastOptimizationResult?.message ?: "Memory: managed by Android on this version",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Switching notifications status
                Text(
                    text = stringResource(R.string.switching_notif),
                    color = TextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                HorizontalDivider(color = NeonGreen, thickness = 2.dp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = if (gameModeState.isGameModeEnabled && gameModeState.doNotDisturbEnabled) stringResource(R.string.notif_silenced) else stringResource(R.string.notif_active),
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun GameModeToggleCard(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
            .clickable { onCheckedChange(!checked) }
            .tvFocusHighlight(shape = RoundedCornerShape(10.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = NeonGreen,
                    uncheckedThumbColor = TextSecondary,
                    uncheckedTrackColor = DarkSurfaceElevated
                )
            )
        }
    }
}
