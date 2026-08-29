package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.WarningAmber

@Composable
fun GameIconBadge(
    presetIndex: Int,
    title: String,
    modifier: Modifier = Modifier,
    size: Dp = 54.dp,
    onClick: (() -> Unit)? = null
) {
    val (bgColor, iconVector) = when (presetIndex % 6) {
        0 -> Pair(Color(0xFFE65100), Icons.Default.PlayArrow) // Amber / Ember Drift
        1 -> Pair(Color(0xFF4E342E), Icons.Default.Adjust) // Target / Bullseye
        2 -> Pair(Color(0xFF00695C), Icons.Default.Hexagon) // Hexagon / Cyber
        3 -> Pair(Color(0xFF1565C0), Icons.Default.Bolt) // Lightning bolt
        4 -> Pair(Color(0xFF6A1B9A), Icons.Default.SportsEsports) // Gamepad
        else -> Pair(Color(0xFF2E7D32), Icons.Default.RocketLaunch) // Rocket
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Icon(
            imageVector = iconVector,
            contentDescription = title,
            tint = Color.White,
            modifier = Modifier.size(size * 0.55f)
        )
    }
}
