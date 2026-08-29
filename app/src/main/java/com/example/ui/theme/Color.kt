package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Standard static color primitives (so they are always accessible)
val ColorHighDensityCanvas = Color(0xFF1C1B1F)
val ColorHighDensityCard = Color(0xFF2B2930)
val ColorHighDensityCardElevated = Color(0xFF36343B)
val ColorHighDensityBorder = Color(0xFF49454F)

val AccentLavender = Color(0xFFD0BCFF)
val AccentPurple = Color(0xFF381E72)
val AccentPurpleDark = Color(0xFF21005D)
val AccentPurpleLight = Color(0xFFEADDFF)
val AccentPurpleContainer = Color(0xFFE8DEF8)

val StatusGreen = Color(0xFF4ADE80)
val WarningOrange = Color(0xFFFB923C)
val CriticalRed = Color(0xFFFF5252)

val NeonGreen = StatusGreen
val NeonGreenDim = Color(0xFF22C55E)
val NeonGreenSubtle = Color(0x264ADE80)
val NeonCyan = AccentLavender
val WarningAmber = WarningOrange

// Gradient stops
val GlowPurpleStart = AccentLavender
val GlowPurpleEnd = AccentPurple
val GlowGreenStart = StatusGreen
val GlowGreenEnd = AccentLavender

// Adaptive Color Aliases resolving via MaterialTheme
val HighDensityCanvas: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background

val HighDensityCard: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

val HighDensityCardElevated: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceVariant

val HighDensityBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outline

val DarkCanvas: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.background

val DarkSurface: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surface

val DarkSurfaceElevated: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.surfaceVariant

val DarkBorder: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.outline

val TextPrimary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurface

val TextSecondary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurfaceVariant

val TextTertiary: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)

val TextNeon: Color
    @Composable
    @ReadOnlyComposable
    get() = MaterialTheme.colorScheme.primary

