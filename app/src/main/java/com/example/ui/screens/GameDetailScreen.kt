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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GameItem
import com.example.data.model.GameSession
import com.example.ui.components.GameIconBadge
import com.example.ui.theme.*
import com.example.viewmodel.ActiveScreen
import com.example.viewmodel.PlayVitalsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GameDetailScreen(
    viewModel: PlayVitalsViewModel,
    game: GameItem,
    allSessions: List<GameSession>,
    modifier: Modifier = Modifier
) {
    val gameSessions = remember(allSessions, game.packageName) {
        allSessions.filter { it.packageName == game.packageName }
    }

    val totalMinutes = (game.totalPlaytimeMillis / 60000L).coerceAtLeast(0)
    val displayPlayed = if (totalMinutes >= 60) "${totalMinutes / 60}h ${totalMinutes % 60}m" else "${totalMinutes}m"
    val scrollState = rememberScrollState()

    var customTagInput by remember { mutableStateOf("") }
    val suggestedTags = listOf("Competitive", "Relaxing", "Offline", "Singleplayer", "Multiplayer", "FPS", "RPG")

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
            // Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo(ActiveScreen.DASHBOARD) },
                    modifier = Modifier
                        .size(36.dp)
                        .tvFocusHighlight(shape = CircleShape)
                        .testTag("back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.game_detail_title),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Left Column: Identity & Primary Play Controls
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Game Header Card
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GameIconBadge(
                                presetIndex = game.iconPresetIndex,
                                title = game.displayName,
                                size = 56.dp
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = game.displayName,
                                    color = TextPrimary,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "$displayPlayed • ${game.sessionCount.coerceAtLeast(gameSessions.size)} SESSIONS",
                                    color = NeonGreen,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        // Big PLAY Button
                        Button(
                            onClick = { viewModel.launchGame(game) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .tvFocusHighlight(shape = RoundedCornerShape(10.dp))
                                .testTag("play_game_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = NeonGreen,
                                contentColor = Color(0xFF003816)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.play_btn),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        // HUD Toggle Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurface)
                                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                .clickable { viewModel.toggleGameHud(game, !game.isHudEnabled) }
                                .tvFocusHighlight(shape = RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                    Text(
                                        text = stringResource(R.string.show_hud_while_playing),
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = stringResource(R.string.hud_desc),
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp
                                    )
                                }

                                Switch(
                                    checked = game.isHudEnabled,
                                    onCheckedChange = { viewModel.toggleGameHud(game, it) },
                                    colors = SwitchDefaults.colors(
                                        checkedThumbColor = Color.White,
                                        checkedTrackColor = NeonGreen,
                                        uncheckedThumbColor = TextSecondary,
                                        uncheckedTrackColor = DarkSurfaceElevated
                                    ),
                                    modifier = Modifier.testTag("hud_toggle_switch")
                                )
                            }
                        }

                        // 6 Stats Grid
                        Text(
                            text = stringResource(R.string.play_history_stats),
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HistoryStatCard(stringResource(R.string.played_label), displayPlayed, Modifier.weight(1f))
                                HistoryStatCard(stringResource(R.string.sessions_label), "${gameSessions.size}", Modifier.weight(1f))
                                HistoryStatCard(stringResource(R.string.min_ram_label), if (game.minRamRecorded > 0) "${game.minRamRecorded}%" else "--", Modifier.weight(1f))
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                HistoryStatCard(stringResource(R.string.peak_temp_label), if (game.peakTempRecorded > 0f) String.format("%.0f°C", game.peakTempRecorded) else "--", Modifier.weight(1f))
                                HistoryStatCard(stringResource(R.string.latency_label), if (game.avgLatencyRecorded > 0) "${game.avgLatencyRecorded}ms" else "--", Modifier.weight(1f))
                                HistoryStatCard(stringResource(R.string.refresh_label), "${game.screenRefreshRate}Hz", Modifier.weight(1f))
                            }
                        }
                    }

                    // Right Column: User Tags & Sessions Row
                    Column(
                        modifier = Modifier.weight(1.2f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // User-Defined Game Tags Card
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurface)
                                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.user_defined_tags),
                                    color = AccentLavender,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = stringResource(R.string.tag_hint),
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Current Active Tags
                                if (game.tagList.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.no_tags_yet),
                                        color = TextTertiary,
                                        fontSize = 11.sp
                                    )
                                } else {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        game.tagList.forEach { tag ->
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(16.dp))
                                                    .background(AccentLavender.copy(alpha = 0.2f))
                                                    .border(1.dp, AccentLavender, RoundedCornerShape(16.dp))
                                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                            ) {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = tag,
                                                        color = AccentLavender,
                                                        fontSize = 11.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    Spacer(modifier = Modifier.width(4.dp))
                                                    Icon(
                                                        imageVector = Icons.Default.Close,
                                                        contentDescription = "Remove $tag tag",
                                                        tint = AccentLavender,
                                                        modifier = Modifier
                                                            .size(14.dp)
                                                            .clickable {
                                                                val updated = game.tagList.filter { it != tag }
                                                                viewModel.updateGameTags(game, updated)
                                                            }
                                                            .tvFocusHighlight(shape = CircleShape)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Quick Tag Suggestion Chips
                                Text(
                                    text = stringResource(R.string.quick_suggestions),
                                    color = TextTertiary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    suggestedTags.forEach { suggestion ->
                                        val isAlreadyAdded = game.tagList.contains(suggestion)
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isAlreadyAdded) StatusGreen.copy(alpha = 0.2f) else DarkSurfaceElevated)
                                                .border(1.dp, if (isAlreadyAdded) StatusGreen else DarkBorder, RoundedCornerShape(12.dp))
                                                .clickable {
                                                    val updated = if (isAlreadyAdded) {
                                                        game.tagList.filter { it != suggestion }
                                                    } else {
                                                        game.tagList + suggestion
                                                    }
                                                    viewModel.updateGameTags(game, updated)
                                                }
                                                .tvFocusHighlight(shape = RoundedCornerShape(12.dp))
                                                .padding(horizontal = 8.dp, vertical = 3.dp)
                                        ) {
                                            Text(
                                                text = if (isAlreadyAdded) "✓ $suggestion" else "+ $suggestion",
                                                color = if (isAlreadyAdded) StatusGreen else TextSecondary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.SemiBold
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Custom Tag Input Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedTextField(
                                        value = customTagInput,
                                        onValueChange = { customTagInput = it },
                                        placeholder = { Text(stringResource(R.string.placeholder_add_custom), fontSize = 11.sp, color = TextTertiary) },
                                        singleLine = true,
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = AccentLavender,
                                            unfocusedBorderColor = DarkBorder,
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary
                                        ),
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(44.dp)
                                            .testTag("custom_tag_input")
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Button(
                                        onClick = {
                                            if (customTagInput.isNotBlank()) {
                                                val updated = game.tagList + customTagInput.trim()
                                                viewModel.updateGameTags(game, updated)
                                                customTagInput = ""
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = AccentLavender,
                                            contentColor = AccentPurpleDark
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier
                                            .height(44.dp)
                                            .tvFocusHighlight(shape = RoundedCornerShape(8.dp))
                                            .testTag("add_tag_button")
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(stringResource(R.string.btn_add), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Recent Sessions List
                        Text(
                            text = stringResource(R.string.recent_sessions),
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (gameSessions.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkSurfaceElevated)
                                        .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                                        .padding(14.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Text(
                                            text = "No recorded sessions for ${game.displayName}",
                                            color = TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "Launch game or start a test session with HUD enabled to track telemetry.",
                                            color = TextTertiary,
                                            fontSize = 10.sp,
                                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                        )
                                    }
                                }
                            } else {
                                gameSessions.forEach { session ->
                                    val mins = (session.durationMillis / 60000L).coerceAtLeast(1)
                                    val dateFormatted = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(session.startTime))
                                    SessionRowItem(
                                        duration = "${mins}m",
                                        timeAgo = dateFormatted,
                                        statsSummary = "RAM ${session.minFreeRamPercent}% • ${String.format("%.0f°C", session.peakBatteryTempC)} • ${session.avgLatencyMs} ms",
                                        onClick = { viewModel.openSessionReport(session) }
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Mobile Portrait Layout
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    // Game Header Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GameIconBadge(
                            presetIndex = game.iconPresetIndex,
                            title = game.displayName,
                            size = 56.dp
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = game.displayName,
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "$displayPlayed • ${game.sessionCount.coerceAtLeast(gameSessions.size)} SESSIONS",
                                color = NeonGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        }
                    }

                    // Big PLAY Button
                    Button(
                        onClick = { viewModel.launchGame(game) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .tvFocusHighlight(shape = RoundedCornerShape(10.dp))
                            .testTag("play_game_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = NeonGreen,
                            contentColor = Color(0xFF003816)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.play_btn),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }

                    // HUD Toggle Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            .clickable { viewModel.toggleGameHud(game, !game.isHudEnabled) }
                            .tvFocusHighlight(shape = RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
                                Text(
                                    text = stringResource(R.string.show_hud_while_playing),
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = stringResource(R.string.hud_desc),
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }

                            Switch(
                                checked = game.isHudEnabled,
                                onCheckedChange = { viewModel.toggleGameHud(game, it) },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = NeonGreen,
                                    uncheckedThumbColor = TextSecondary,
                                    uncheckedTrackColor = DarkSurfaceElevated
                                ),
                                modifier = Modifier.testTag("hud_toggle_switch")
                            )
                        }
                    }

                    // User-Defined Game Tags Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.user_defined_tags),
                                color = AccentLavender,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.tag_hint),
                                color = TextSecondary,
                                fontSize = 11.sp
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Current Active Tags
                            if (game.tagList.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.no_tags_yet),
                                    color = TextTertiary,
                                    fontSize = 11.sp
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    game.tagList.forEach { tag ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(16.dp))
                                                .background(AccentLavender.copy(alpha = 0.2f))
                                                .border(1.dp, AccentLavender, RoundedCornerShape(16.dp))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = tag,
                                                    color = AccentLavender,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = "Remove $tag tag",
                                                    tint = AccentLavender,
                                                    modifier = Modifier
                                                        .size(14.dp)
                                                        .clickable {
                                                            val updated = game.tagList.filter { it != tag }
                                                            viewModel.updateGameTags(game, updated)
                                                        }
                                                        .tvFocusHighlight(shape = CircleShape)
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Quick Tag Suggestion Chips
                            Text(
                                text = stringResource(R.string.quick_suggestions),
                                color = TextTertiary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                suggestedTags.forEach { suggestion ->
                                    val isAlreadyAdded = game.tagList.contains(suggestion)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isAlreadyAdded) StatusGreen.copy(alpha = 0.2f) else DarkSurfaceElevated)
                                            .border(1.dp, if (isAlreadyAdded) StatusGreen else DarkBorder, RoundedCornerShape(12.dp))
                                            .clickable {
                                                val updated = if (isAlreadyAdded) {
                                                    game.tagList.filter { it != suggestion }
                                                } else {
                                                    game.tagList + suggestion
                                                }
                                                viewModel.updateGameTags(game, updated)
                                            }
                                            .tvFocusHighlight(shape = RoundedCornerShape(12.dp))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(
                                            text = if (isAlreadyAdded) "✓ $suggestion" else "+ $suggestion",
                                            color = if (isAlreadyAdded) StatusGreen else TextSecondary,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Custom Tag Input Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = customTagInput,
                                    onValueChange = { customTagInput = it },
                                    placeholder = { Text(stringResource(R.string.placeholder_add_custom_tag), fontSize = 11.sp, color = TextTertiary) },
                                    singleLine = true,
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AccentLavender,
                                        unfocusedBorderColor = DarkBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(48.dp)
                                        .testTag("custom_tag_input")
                                )

                                Spacer(modifier = Modifier.width(8.dp))

                                Button(
                                    onClick = {
                                        if (customTagInput.isNotBlank()) {
                                            val updated = game.tagList + customTagInput.trim()
                                            viewModel.updateGameTags(game, updated)
                                            customTagInput = ""
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = AccentLavender,
                                        contentColor = AccentPurpleDark
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(48.dp)
                                        .tvFocusHighlight(shape = RoundedCornerShape(8.dp))
                                        .testTag("add_tag_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(stringResource(R.string.btn_add), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    // Play History Title
                    Text(
                        text = stringResource(R.string.play_history),
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    // 6 Stats Grid
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HistoryStatCard(stringResource(R.string.played_label), displayPlayed, Modifier.weight(1f))
                            HistoryStatCard(stringResource(R.string.sessions_label), "${gameSessions.size}", Modifier.weight(1f))
                            HistoryStatCard(stringResource(R.string.min_ram_label), if (game.minRamRecorded > 0) "${game.minRamRecorded}%" else "--", Modifier.weight(1f))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            HistoryStatCard(stringResource(R.string.peak_temp_label), if (game.peakTempRecorded > 0f) String.format("%.0f°C", game.peakTempRecorded) else "--", Modifier.weight(1f))
                            HistoryStatCard(stringResource(R.string.latency_label), if (game.avgLatencyRecorded > 0) "${game.avgLatencyRecorded}ms" else "--", Modifier.weight(1f))
                            HistoryStatCard(stringResource(R.string.refresh_label), "${game.screenRefreshRate}Hz", Modifier.weight(1f))
                        }
                    }

                    // Recent Sessions List
                    Text(
                        text = stringResource(R.string.recent_sessions),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        if (gameSessions.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurfaceElevated)
                                    .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
                                    .padding(14.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(
                                        text = "No recorded sessions for ${game.displayName}",
                                        color = TextSecondary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "Launch game or start a test session with HUD enabled to track telemetry.",
                                        color = TextTertiary,
                                        fontSize = 10.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            gameSessions.forEach { session ->
                                val mins = (session.durationMillis / 60000L).coerceAtLeast(1)
                                val dateFormatted = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault()).format(Date(session.startTime))
                                SessionRowItem(
                                    duration = "${mins}m",
                                    timeAgo = dateFormatted,
                                    statsSummary = "RAM ${session.minFreeRamPercent}% • ${String.format("%.0f°C", session.peakBatteryTempC)} • ${session.avgLatencyMs} ms",
                                    onClick = { viewModel.openSessionReport(session) }
                                )
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
private fun HistoryStatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(8.dp))
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp)
        ) {
            Text(text = label, color = TextSecondary, fontSize = 8.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SessionRowItem(
    duration: String,
    timeAgo: String,
    statsSummary: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .tvFocusHighlight(shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = duration, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = statsSummary, color = TextSecondary, fontSize = 11.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = timeAgo, color = TextSecondary, fontSize = 10.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "View Report",
                    tint = TextSecondary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
