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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.R
import com.example.data.model.GameSession
import com.example.system.ExportReportHelper
import com.example.ui.components.GameIconBadge
import com.example.ui.components.NeonCircularGauge
import com.example.ui.theme.*
import com.example.viewmodel.ActiveScreen
import com.example.viewmodel.PlayVitalsViewModel

@Composable
fun SessionReportScreen(
    viewModel: PlayVitalsViewModel,
    session: GameSession,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val mins = (session.durationMillis / 60000L).coerceAtLeast(1)

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
                        .testTag("session_report_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.session_title),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Game Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                GameIconBadge(
                    presetIndex = (session.packageName.hashCode() % 6).let { if (it < 0) it + 6 else it },
                    title = session.gameTitle,
                    size = 52.dp
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = session.gameTitle,
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "PLAYED ${mins}M",
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Sub-components as reusable local lambdas
            val scoreSection = @Composable {
                Column {
                    Text(
                        text = stringResource(R.string.session_scores_title),
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4 Score Cards (2x2 Grid)
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ScoreCard(
                                score = session.pingScore,
                                title = stringResource(R.string.ping_stability_title),
                                description = if (session.pingScore >= 70) stringResource(R.string.ping_stable_desc) else stringResource(R.string.ping_unstable_desc),
                                color = if (session.pingScore >= 70) NeonGreen else if (session.pingScore >= 40) WarningAmber else Color(0xFF8B949E),
                                modifier = Modifier.weight(1f)
                            )
                            ScoreCard(
                                score = session.memoryScore,
                                title = stringResource(R.string.memory_title),
                                description = if (session.memoryScore >= 70) stringResource(R.string.memory_good_desc) else stringResource(R.string.memory_heavy_desc),
                                color = if (session.memoryScore >= 70) NeonGreen else WarningAmber,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            ScoreCard(
                                score = session.tempScore,
                                title = stringResource(R.string.battery_temp_title),
                                description = if (session.tempScore >= 70) stringResource(R.string.battery_temp_cool_desc) else stringResource(R.string.battery_temp_warm_desc),
                                color = if (session.tempScore >= 70) NeonGreen else WarningAmber,
                                modifier = Modifier.weight(1f)
                            )
                            ScoreCard(
                                score = session.overallScore,
                                title = stringResource(R.string.overall_score_title),
                                description = stringResource(R.string.overall_score_desc),
                                color = if (session.overallScore >= 75) NeonGreen else if (session.overallScore >= 50) WarningAmber else CriticalRed,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            val verdictSection = @Composable {
                val verdictTitle = when {
                    session.overallScore >= 80 -> stringResource(R.string.optimal_session)
                    session.overallScore >= 60 -> stringResource(R.string.mixed_session)
                    else -> stringResource(R.string.heavy_load_session)
                }
                val verdictColor = if (session.overallScore >= 75) NeonGreen else WarningAmber

                Column {
                    Text(
                        text = verdictTitle,
                        color = verdictColor,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // What the readings say
                    Text(
                        text = stringResource(R.string.what_readings_say),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            ReadingBulletItem(
                                text = if (session.minFreeRamPercent >= 40)
                                    "Free memory stayed comfortable the whole session (min ${session.minFreeRamPercent}% free)."
                                else
                                    "Free memory dipped to ${session.minFreeRamPercent}% during intense scenes."
                            )
                            ReadingBulletItem(
                                text = if (session.peakBatteryTempC <= 35f)
                                    "Battery temperature stayed normal (peak ${String.format("%.0f°C", session.peakBatteryTempC)})."
                                else
                                    "Battery temperature reached ${String.format("%.0f°C", session.peakBatteryTempC)} under sustained load."
                            )
                            ReadingBulletItem(
                                text = "Average network latency was ${session.avgLatencyMs} ms with ${session.pingJitterMs} ms jitter."
                            )
                        }
                    }
                }
            }

            val buttonsSection = @Composable {
                val context = LocalContext.current
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { ExportReportHelper.exportAndSharePdf(context, session) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .tvFocusHighlight(shape = RoundedCornerShape(10.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HighDensityCardElevated,
                                contentColor = StatusGreen
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(StatusGreen.copy(alpha = 0.5f)))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stringResource(R.string.export_pdf), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { ExportReportHelper.exportAndShareJson(context, session) },
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .tvFocusHighlight(shape = RoundedCornerShape(10.dp)),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = HighDensityCardElevated,
                                contentColor = AccentLavender
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(AccentLavender.copy(alpha = 0.5f)))
                        ) {
                            Icon(
                                imageVector = Icons.Default.FileDownload,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = stringResource(R.string.export_json), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = { viewModel.navigateTo(ActiveScreen.DASHBOARD) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .tvFocusHighlight(shape = RoundedCornerShape(10.dp)),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurfaceElevated,
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkBorder))
                    ) {
                        Text(text = stringResource(R.string.btn_done), fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    }
                }
            }

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
                        verdictSection()
                        Spacer(modifier = Modifier.height(10.dp))
                        buttonsSection()
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        scoreSection()
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(14.dp))
                scoreSection()
                Spacer(modifier = Modifier.height(20.dp))
                verdictSection()
                Spacer(modifier = Modifier.height(20.dp))
                buttonsSection()
            }
        }
    }
}

@Composable
private fun ScoreCard(
    score: Int,
    title: String,
    description: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
            .padding(14.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            NeonCircularGauge(
                value = score,
                maxValue = 100,
                label = "",
                valueSuffix = "",
                size = 72.dp,
                strokeWidth = 6.dp,
                primaryColor = color
            )
            Spacer(modifier = Modifier.height(10.dp))
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
                fontSize = 10.sp,
                lineHeight = 14.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun ReadingBulletItem(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp, end = 8.dp)
                .size(5.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(NeonGreen)
        )
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 12.sp,
            lineHeight = 17.sp
        )
    }
}
