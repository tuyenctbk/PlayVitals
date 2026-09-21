package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

// Standard static color primitives (Obsidian Cyber aesthetic)
val ColorHighDensityCanvas = Color(0xFF0A0B10)
val ColorHighDensityCard = Color(0xFF141722)
val ColorHighDensityCardElevated = Color(0xFF1D2232)
val ColorHighDensityBorder = Color(0xFF2B3248)

val AccentLavender = Color(0xFFD0BCFF)
val AccentPurple = Color(0xFF6366F1)
val AccentPurpleDark = Color(0xFF1E1B4B)
val AccentPurpleLight = Color(0xFFE0E7FF)
val AccentPurpleContainer = Color(0xFF312E81)

val StatusGreen = Color(0xFF00E676)
val WarningOrange = Color(0xFFFF9100)
val CriticalRed = Color(0xFFFF1744)

val NeonGreen = StatusGreen
val NeonGreenDim = Color(0xFF00C853)
val NeonGreenSubtle = Color(0x2600E676)
val NeonCyan = Color(0xFF00F0FF)
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

