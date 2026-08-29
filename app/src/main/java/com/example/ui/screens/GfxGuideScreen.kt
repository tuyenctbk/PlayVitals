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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.R
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.system.LiveDeviceStats
import com.example.ui.theme.*
import com.example.viewmodel.ActiveScreen
import com.example.viewmodel.PlayVitalsViewModel
import com.example.viewmodel.GfxRecommendation

@Composable
fun GfxGuideScreen(
    viewModel: PlayVitalsViewModel,
    liveStats: LiveDeviceStats,
    gfx: GfxRecommendation,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val totalRamGb = liveStats.totalRamBytes / (1024.0 * 1024.0 * 1024.0)

    val geminiAdvice by viewModel.geminiAdvice.collectAsState()
    val isGeneratingGemini by viewModel.isGeneratingGeminiAdvice.collectAsState()

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
                        .testTag("gfx_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "GFX GUIDE",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Honest Disclaimer Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkBorder, RoundedCornerShape(10.dp))
                    .padding(14.dp)
            ) {
                Text(
                    text = "These are suggestions for you to set inside the game yourself. This app cannot change settings inside a game, and it cannot make a game run faster.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Left column: Specs & Recommendations
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "BASED ON WHAT YOUR DEVICE REPORTS",
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurface)
                                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                GfxRow("Graphics", gfx.graphicsLevel)
                                GfxRow("Frame rate", gfx.frameRate)
                                GfxRow("Resolution", gfx.resolutionScale)
                                GfxRow("Shadows", gfx.shadows)
                            }
                        }

                        Text(
                            text = "WHY THESE SUGGESTIONS",
                            color = NeonGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(DarkSurface)
                                .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                                .padding(14.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                SpecRow("Total RAM", String.format("%.1f GB", totalRamGb.coerceAtLeast(3.8)))
                                SpecRow("CPU cores", "${liveStats.cpuCores}")
                                SpecRow("Highest screen refresh rate", "${liveStats.screenRefreshRateHz} Hz")
                                SpecRow("Screen resolution", liveStats.screenResolution)
                                SpecRow("GPU", liveStats.gpuRenderer)
                            }
                        }
                    }

                    // Right column: Gemini AI advisor & Re-run trigger
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
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
                                            text = "GEMINI AI ADVISOR",
                                            color = NeonCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 1.sp
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Exact hardware-based visual and frame-rate optimization suggestions.",
                                            color = TextSecondary,
                                            fontSize = 10.sp
                                        )
                                    }

                                    Button(
                                        onClick = { viewModel.generateGeminiHardwareAdvice() },
                                        enabled = !isGeneratingGemini,
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = HighDensityCardElevated,
                                            contentColor = NeonCyan
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(NeonCyan.copy(alpha = 0.5f))),
                                        modifier = Modifier
                                            .height(36.dp)
                                            .tvFocusHighlight(shape = RoundedCornerShape(8.dp))
                                            .testTag("generate_gemini_tips_button")
                                    ) {
                                        if (isGeneratingGemini) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(14.dp),
                                                color = NeonCyan,
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Text(stringResource(R.string.btn_ai_analyze), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                if (geminiAdvice != null) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = HighDensityBorder, thickness = 1.dp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = geminiAdvice ?: "",
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        lineHeight = 17.sp,
                                        fontWeight = FontWeight.Normal
                                    )
                                }
                            }
                        }

                        // Re-run check button
                        Button(
                            onClick = { viewModel.computeGfxRecommendations() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .tvFocusHighlight(shape = RoundedCornerShape(10.dp))
                                .testTag("run_check_again_button"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DarkSurfaceElevated,
                                contentColor = NeonGreen
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(NeonGreen))
                        ) {
                            if (gfx.isAnalyzing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = NeonGreen,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(R.string.analyzing_hardware), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            } else {
                                Text(stringResource(R.string.run_check_again), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                            }
                        }
                    }
                }
            } else {
                // Mobile Portrait Column Layout
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "BASED ON WHAT YOUR DEVICE REPORTS",
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            GfxRow("Graphics", gfx.graphicsLevel)
                            GfxRow("Frame rate", gfx.frameRate)
                            GfxRow("Resolution", gfx.resolutionScale)
                            GfxRow("Shadows", gfx.shadows)
                        }
                    }

                    Text(
                        text = "WHY THESE SUGGESTIONS",
                        color = NeonGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SpecRow("Total RAM", String.format("%.1f GB", totalRamGb.coerceAtLeast(3.8)))
                            SpecRow("CPU cores", "${liveStats.cpuCores}")
                            SpecRow("Highest screen refresh rate", "${liveStats.screenRefreshRateHz} Hz")
                            SpecRow("Screen resolution", liveStats.screenResolution)
                            SpecRow("GPU", liveStats.gpuRenderer)
                        }
                    }

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
                                        text = "GEMINI AI ADVISOR",
                                        color = NeonCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "AI analysis based on your device's exact specifications.",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }

                                Button(
                                    onClick = { viewModel.generateGeminiHardwareAdvice() },
                                    enabled = !isGeneratingGemini,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = HighDensityCardElevated,
                                        contentColor = NeonCyan
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(NeonCyan.copy(alpha = 0.5f))),
                                    modifier = Modifier
                                        .height(36.dp)
                                        .tvFocusHighlight(shape = RoundedCornerShape(8.dp))
                                        .testTag("generate_gemini_tips_button")
                                ) {
                                    if (isGeneratingGemini) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(14.dp),
                                            color = NeonCyan,
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        Text(stringResource(R.string.btn_ai_analyze), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            if (geminiAdvice != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = HighDensityBorder, thickness = 1.dp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = geminiAdvice ?: "",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    lineHeight = 17.sp,
                                    fontWeight = FontWeight.Normal
                                )
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.computeGfxRecommendations() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .tvFocusHighlight(shape = RoundedCornerShape(10.dp))
                            .testTag("run_check_again_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DarkSurfaceElevated,
                            contentColor = NeonGreen
                        ),
                        shape = RoundedCornerShape(10.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(NeonGreen))
                    ) {
                        if (gfx.isAnalyzing) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = NeonGreen,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(R.string.analyzing_hardware), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text(stringResource(R.string.run_check_again), fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun GfxRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextPrimary, fontSize = 13.sp)
        Text(text = value, color = NeonGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(text = label, color = TextSecondary, fontSize = 11.sp, modifier = Modifier.weight(1f))
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
            modifier = Modifier.weight(1.3f)
        )
    }
}
