package com.example.ui.theme

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.data.model.ThemeMode

private val HighDensityDarkColorScheme = darkColorScheme(
    primary = AccentLavender,
    onPrimary = AccentPurpleDark,
    primaryContainer = AccentPurple,
    onPrimaryContainer = AccentPurpleLight,
    secondary = StatusGreen,
    onSecondary = Color(0xFF003816),
    secondaryContainer = Color(0xFF00522B),
    onSecondaryContainer = Color(0xFF69FF9E),
    tertiary = WarningOrange,
    onTertiary = Color(0xFF422C00),
    tertiaryContainer = Color(0xFF5F4100),
    onTertiaryContainer = Color(0xFFFFDE9F),
    background = ColorHighDensityCanvas,
    onBackground = Color(0xFFE6E1E5),
    surface = ColorHighDensityCard,
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = ColorHighDensityCardElevated,
    onSurfaceVariant = Color(0xFF938F99),
    outline = ColorHighDensityBorder,
    outlineVariant = Color(0xFF49454F),
    error = CriticalRed,
    onError = Color.White
)

private val HighDensityLightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF16A34A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCFCE7),
    onSecondaryContainer = Color(0xFF14532D),
    tertiary = Color(0xFFEA580C),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFEDD5),
    onTertiaryContainer = Color(0xFF7C2D12),
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    outlineVariant = Color(0xFFE2E8F0),
    error = CriticalRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> darkTheme
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val colorScheme = if (useDarkTheme) HighDensityDarkColorScheme else HighDensityLightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

/**
 * A reusable Modifier that scales and adds a high-contrast glowing border 
 * when the element is focused via D-pad navigation or keyboard.
 */
fun Modifier.tvFocusHighlight(
    shape: Shape = RoundedCornerShape(12.dp),
    focusedBorderColor: Color = Color(0xFFD0BCFF),
    focusedScale: Float = 1.05f
): Modifier = composed {
    var isFocused by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isFocused) focusedScale else 1.0f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "tv_scale"
    )

    this
        .onFocusChanged { isFocused = it.isFocused }
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .border(
            width = if (isFocused) 2.dp else 0.dp,
            color = if (isFocused) focusedBorderColor else Color.Transparent,
            shape = shape
        )
}


