package com.example.data.model

enum class ThemeMode(
    val displayName: String,
    val description: String
) {
    SYSTEM(
        displayName = "System Default",
        description = "Automatically switch between light and dark modes based on your device's system settings"
    ),
    DARK(
        displayName = "Dark Mode",
        description = "Deep obsidian dark theme with high-contrast cyberpunk highlights"
    ),
    LIGHT(
        displayName = "Light Mode",
        description = "Clean crisp light theme with dark typography and vivid accents"
    )
}
