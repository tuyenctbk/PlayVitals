package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.R
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.ActiveScreen
import com.example.viewmodel.PlayVitalsViewModel
import com.example.viewmodel.ReactionState
import com.example.viewmodel.ReflexDrillType

@Composable
fun ReflexTrainerScreen(
    viewModel: PlayVitalsViewModel,
    modifier: Modifier = Modifier
) {
    val drillType by viewModel.reflexDrillType.collectAsState()
    val reactionState by viewModel.reactionState.collectAsState()
    val reactionResultMs by viewModel.reactionTimeResultMs.collectAsState()
    val bestReactionMs by viewModel.bestReactionScore.collectAsState()

    val blitzTargets by viewModel.blitzTargets.collectAsState()
    val blitzRemaining by viewModel.blitzRemainingCount.collectAsState()
    val blitzElapsedMs by viewModel.blitzTimeElapsedMs.collectAsState()
    val isBlitzActive by viewModel.isBlitzActive.collectAsState()
    val blitzFinalScore by viewModel.blitzFinalScore.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HighDensityCanvas)
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
                    .testTag("reflex_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.reflex_trainer_title),
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Drill Selector Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(HighDensityCard)
                .border(1.dp, HighDensityBorder, RoundedCornerShape(10.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            DrillTabButton(
                title = stringResource(R.string.reaction_speed),
                isSelected = drillType == ReflexDrillType.REACTION_SPEED,
                onClick = { viewModel.setReflexDrillType(ReflexDrillType.REACTION_SPEED) },
                modifier = Modifier.weight(1f)
            )
            DrillTabButton(
                title = stringResource(R.string.targets_blitz_12),
                isSelected = drillType == ReflexDrillType.TARGET_BLITZ,
                onClick = { viewModel.setReflexDrillType(ReflexDrillType.TARGET_BLITZ) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (drillType == ReflexDrillType.REACTION_SPEED) {
            // Reaction Speed Test Area
            ReactionSpeedDrillContent(
                state = reactionState,
                resultMs = reactionResultMs,
                bestScore = bestReactionMs,
                onTap = { viewModel.handleReactionTap() }
            )
        } else {
            // 12-Targets Blitz Test Area
            TargetBlitzDrillContent(
                targets = blitzTargets,
                remaining = blitzRemaining,
                elapsedMs = blitzElapsedMs,
                isActive = isBlitzActive,
                finalScore = blitzFinalScore,
                onStart = { viewModel.startTargetBlitzDrill() },
                onHitTarget = { viewModel.hitBlitzTarget() }
            )
        }
    }
}

@Composable
private fun DrillTabButton(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) AccentLavender else Color.Transparent)
            .clickable { onClick() }
            .tvFocusHighlight(shape = RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) AccentPurpleDark else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun ReactionSpeedDrillContent(
    state: ReactionState,
    resultMs: Long?,
    bestScore: Long?,
    onTap: () -> Unit
) {
    val targetBgColor by animateColorAsState(
        targetValue = when (state) {
            ReactionState.IDLE -> HighDensityCard
            ReactionState.WAITING_FOR_GREEN -> Color(0xFF7A1C1C) // Red waiting
            ReactionState.READY_TAP_NOW -> StatusGreen // Vibrant Green
            ReactionState.EARLY_TAP -> Color(0xFF991B1B)
            ReactionState.RESULT -> HighDensityCard
        },
        label = "reactionBg"
    )

    val interactionSource = remember { MutableInteractionSource() }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(targetBgColor)
                .border(1.5.dp, if (state == ReactionState.READY_TAP_NOW) Color.White else HighDensityBorder, RoundedCornerShape(16.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) { onTap() }
                .tvFocusHighlight(shape = RoundedCornerShape(16.dp))
                .padding(20.dp)
                .testTag("reaction_tap_area")
        ) {
            when (state) {
                ReactionState.IDLE -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = AccentLavender,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = stringResource(R.string.tap_to_start),
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.reaction_instruction),
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                ReactionState.WAITING_FOR_GREEN -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.wait_for_green),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.dont_tap_yet),
                            color = Color(0xFFFFCDD2),
                            fontSize = 13.sp
                        )
                    }
                }
                ReactionState.READY_TAP_NOW -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.tap_now),
                            color = Color(0xFF003816),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.5.sp
                        )
                    }
                }
                ReactionState.EARLY_TAP -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(R.string.too_early),
                            color = Color.White,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = stringResource(R.string.tapped_before_green),
                            color = Color(0xFFFFCDD2),
                            fontSize = 12.sp
                        )
                    }
                }
                ReactionState.RESULT -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${resultMs ?: 0} ms",
                            color = StatusGreen,
                            fontSize = 44.sp,
                            fontWeight = FontWeight.Black
                        )
                        val rank = when {
                            (resultMs ?: 0) < 200 -> "GODLIKE (PRO ESPORTS)"
                            (resultMs ?: 0) < 250 -> "MASTER SPEED"
                            (resultMs ?: 0) < 320 -> "SOLID GAMER"
                            else -> "NEEDS WARMUP"
                        }
                        Text(
                            text = rank,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(HighDensityCardElevated)
                                .border(1.dp, HighDensityBorder, RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.input_latency_note),
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = stringResource(R.string.tap_anywhere_try_again),
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Best Score Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(HighDensityCard)
                .border(1.dp, HighDensityBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = WarningOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.personal_best),
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = if (bestScore != null) "$bestScore ms" else "322 ms",
                    color = StatusGreen,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TargetBlitzDrillContent(
    targets: List<com.example.viewmodel.TargetPosition>,
    remaining: Int,
    elapsedMs: Long,
    isActive: Boolean,
    finalScore: Long?,
    onStart: () -> Unit,
    onHitTarget: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Status Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "TARGETS REMAINING: $remaining/12",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
            Text(
                text = String.format("%.2f s", elapsedMs / 1000f),
                color = StatusGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Blitz Canvas Arena
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(HighDensityCard)
                .border(1.dp, HighDensityBorder, RoundedCornerShape(16.dp))
        ) {
            val arenaWidth = maxWidth
            val arenaHeight = maxHeight

            if (!isActive && finalScore == null) {
                // Pre-game launch prompt
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.GpsFixed,
                        contentDescription = null,
                        tint = AccentLavender,
                        modifier = Modifier.size(50.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = stringResource(R.string.targets_blitz_12),
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.targets_blitz_desc),
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen, contentColor = Color(0xFF003816)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .tvFocusHighlight(shape = RoundedCornerShape(8.dp))
                            .testTag("start_blitz_button")
                    ) {
                        Text(stringResource(R.string.btn_start_blitz), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (finalScore != null && !isActive) {
                // Result screen
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.drill_complete),
                        color = StatusGreen,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = String.format("%.3f seconds", finalScore / 1000f),
                        color = TextPrimary,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Avg ${finalScore / 12} ms per target",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = onStart,
                        colors = ButtonDefaults.buttonColors(containerColor = StatusGreen, contentColor = Color(0xFF003816)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.tvFocusHighlight(shape = RoundedCornerShape(8.dp))
                    ) {
                        Text(stringResource(R.string.btn_play_again), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                // Active Target Rendering
                targets.forEach { target ->
                    val offsetX = arenaWidth * target.xPercent
                    val offsetY = arenaHeight * target.yPercent

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .offset(x = offsetX - 28.dp, y = offsetY - 28.dp)
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(StatusGreen.copy(alpha = 0.25f))
                            .border(2.dp, StatusGreen, CircleShape)
                            .clickable { onHitTarget() }
                            .tvFocusHighlight(shape = CircleShape)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .background(StatusGreen)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}
