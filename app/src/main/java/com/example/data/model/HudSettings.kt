package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class HudVisualStyle(
    val displayName: String,
    val description: String,
    val primaryColorHex: Long,
    val backgroundColorHex: Long,
    val borderColorHex: Long
) {
    CYBERPUNK_NEON(
        displayName = "Cyberpunk Neon",
        description = "Vibrant glowing green accents on a dark cybernetic canvas",
        primaryColorHex = 0xFF00E676,
        backgroundColorHex = 0xFF0D1117,
        borderColorHex = 0xFF00E676
    ),
    MINIMAL_DARK(
        displayName = "Minimal Dark",
        description = "Clean, low-profile matte dark styling with subtle borders",
        primaryColorHex = 0xFFE6E1E5,
        backgroundColorHex = 0xFF141218,
        borderColorHex = 0xFF49454F
    ),
    VELVET_PURPLE(
        displayName = "Velvet Purple",
        description = "High contrast lavender & deep violet theme for maximum visibility",
        primaryColorHex = 0xFFD0BCFF,
        backgroundColorHex = 0xFF1A0B2E,
        borderColorHex = 0xFFD0BCFF
    ),
    GLASS_CYAN(
        displayName = "Glass Cyan",
        description = "Futuristic translucent HUD with luminous cyan accents",
        primaryColorHex = 0xFF00E5FF,
        backgroundColorHex = 0xFF0A192F,
        borderColorHex = 0xFF00E5FF
    ),
    AMBER_OVERDRIVE(
        displayName = "Amber Overdrive",
        description = "Warm amber & industrial dark orange palette for intense gaming",
        primaryColorHex = 0xFFFFAB00,
        backgroundColorHex = 0xFF231809,
        borderColorHex = 0xFFFFAB00
    );

    val primaryColor: Color get() = Color(primaryColorHex)
    val backgroundColor: Color get() = Color(backgroundColorHex)
    val borderColor: Color get() = Color(borderColorHex)
}

data class HudSettings(
    val isHudMasterEnabled: Boolean = true,
    val visualStyle: HudVisualStyle = HudVisualStyle.CYBERPUNK_NEON,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val showFreeRam: Boolean = true,
    val showBatteryTemp: Boolean = true,
    val showBatteryLevel: Boolean = true,
    val showNetworkLatency: Boolean = true,
    val showScreenRefreshRate: Boolean = true,
    val backgroundOpacity: Float = 0.85f,
    val textSizeScale: Float = 1.0f,
    val warnLowRamThreshold: Int = 15, // Warning when Free RAM < 15%
    val warnHighTempThreshold: Float = 40.0f, // Warning when Battery Temp > 40°C
    val isAutoRefreshRateLimiterEnabled: Boolean = false,
    val lowBatteryRefreshRateThreshold: Int = 20, // Battery % limit trigger
    val limitedRefreshRateHz: Int = 60, // Target eco refresh rate
    val hudPositionX: Int = -1, // Persistent HUD screen position X
    val hudPositionY: Int = -1  // Persistent HUD screen position Y
)
