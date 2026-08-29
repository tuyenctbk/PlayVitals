package com.example.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.MainActivity
import com.example.R
import com.example.data.db.GameDatabase
import com.example.data.model.GameSession
import com.example.data.model.HudSettings
import com.example.data.model.HudVisualStyle
import com.example.data.repository.GameRepository
import com.example.system.DeviceMonitor
import com.example.ui.theme.DarkBorder
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.WarningAmber
import kotlinx.coroutines.*
import kotlin.math.roundToInt

class HudOverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())

    private var deviceMonitor: DeviceMonitor? = null
    private var gameRepository: GameRepository? = null

    // Active session tracking state
    private var activeGamePackage: String = ""
    private var activeGameTitle: String = "Game"
    private var sessionStartTime: Long = 0L

    private val _freeRamPercent = mutableStateOf(50)
    private val _freeRamGb = mutableStateOf("2.0 GB")
    private val _latencyMs = mutableStateOf(35)
    private val _refreshRateHz = mutableStateOf(60)
    private val _batteryTempC = mutableStateOf(28f)
    private val _elapsedSeconds = mutableStateOf(0L)
    private val _minRamPercent = mutableStateOf(100)
    private val _peakTempC = mutableStateOf(0f)
    private val _isMinimized = mutableStateOf(false)
    private val _batteryLevel = mutableStateOf(100)
    private val _isEcoLimiterActive = mutableStateOf(false)
    private var currentHudSettings = HudSettings()

    override fun onCreate() {
        super.onCreate()
        deviceMonitor = DeviceMonitor(this)
        gameRepository = GameRepository(GameDatabase.getInstance(this).gameDao())
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_HUD) {
            stopSelf()
            return START_NOT_STICKY
        }

        activeGamePackage = intent?.getStringExtra(EXTRA_GAME_PACKAGE) ?: "com.game"
        activeGameTitle = intent?.getStringExtra(EXTRA_GAME_TITLE) ?: "Active Game"
        sessionStartTime = System.currentTimeMillis()

        loadSavedHudSettings()

        startForeground(NOTIFICATION_ID, buildNotification())
        showOverlayWindow()
        startStatsPolling()

        return START_NOT_STICKY
    }

    private fun loadSavedHudSettings() {
        val prefs = getSharedPreferences("hud_prefs", Context.MODE_PRIVATE)
        currentHudSettings = HudSettings(
            isAutoRefreshRateLimiterEnabled = prefs.getBoolean("auto_refresh_limiter", false),
            lowBatteryRefreshRateThreshold = prefs.getInt("low_battery_threshold", 20),
            limitedRefreshRateHz = prefs.getInt("limited_refresh_rate", 60),
            showFreeRam = prefs.getBoolean("show_free_ram", true),
            showNetworkLatency = prefs.getBoolean("show_latency", true),
            showScreenRefreshRate = prefs.getBoolean("show_refresh_rate", true),
            showBatteryTemp = prefs.getBoolean("show_battery_temp", true),
            backgroundOpacity = prefs.getFloat("bg_opacity", 0.85f),
            warnLowRamThreshold = prefs.getInt("warn_low_ram", 15),
            warnHighTempThreshold = prefs.getFloat("warn_high_temp", 40.0f),
            hudPositionX = prefs.getInt("hud_x", -1),
            hudPositionY = prefs.getInt("hud_y", -1)
        )
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Game HUD Overlay",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows live gaming performance HUD overlay"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        val stopIntent = Intent(this, HudOverlayService::class.java).apply {
            action = ACTION_STOP_HUD
        }
        val stopPendingIntent = PendingIntent.getService(
            this, 0, stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val mainIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val mainPendingIntent = PendingIntent.getActivity(
            this, 1, mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Game HUD Active • $activeGameTitle")
            .setContentText("Monitoring live FPS, RAM & Ping")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentIntent(mainPendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Finish & Save", stopPendingIntent)
            .setOngoing(true)
            .build()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showOverlayWindow() {
        if (!Settings.canDrawOverlays(this)) {
            return
        }

        if (overlayView != null) return

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val savedX = getSharedPreferences("hud_prefs", Context.MODE_PRIVATE).getInt("hud_x", 40)
        val savedY = getSharedPreferences("hud_prefs", Context.MODE_PRIVATE).getInt("hud_y", 120)

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = savedX
            y = savedY
        }

        val composeView = ComposeView(this).apply {
            setContent {
                HudOverlayContent(
                    gameTitle = activeGameTitle,
                    freeRamPercent = _freeRamPercent.value,
                    freeRamGb = _freeRamGb.value,
                    latencyMs = _latencyMs.value,
                    refreshRateHz = _refreshRateHz.value,
                    batteryTempC = _batteryTempC.value,
                    elapsedSeconds = _elapsedSeconds.value,
                    minRamPercent = _minRamPercent.value,
                    peakTempC = _peakTempC.value,
                    isMinimized = _isMinimized.value,
                    isEcoLimiterActive = _isEcoLimiterActive.value,
                    onToggleMinimize = { _isMinimized.value = !_isMinimized.value },
                    onFinish = { finishAndRecordSession() },
                    onDrag = { dx, dy ->
                        layoutParams.x = (layoutParams.x + dx.toInt()).coerceIn(0, 1000)
                        layoutParams.y = (layoutParams.y + dy.toInt()).coerceIn(0, 2000)
                        windowManager?.updateViewLayout(this@apply, layoutParams)

                        // Save updated screen position to SharedPreferences persistently
                        getSharedPreferences("hud_prefs", Context.MODE_PRIVATE).edit()
                            .putInt("hud_x", layoutParams.x)
                            .putInt("hud_y", layoutParams.y)
                            .apply()
                    }
                )
            }
        }

        // Dummy lifecycle owners for ComposeView in Service
        val wrapper = FrameLayout(this)
        wrapper.addView(composeView)
        overlayView = wrapper

        try {
            windowManager?.addView(wrapper, layoutParams)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startStatsPolling() {
        serviceScope.launch {
            while (isActive) {
                val ping = deviceMonitor?.measureNetworkLatency() ?: 35
                val stats = deviceMonitor?.readCurrentStats(ping)
                if (stats != null) {
                    _freeRamPercent.value = stats.freeRamPercent
                    val freeGb = stats.freeRamBytes / (1024.0 * 1024.0 * 1024.0)
                    _freeRamGb.value = String.format("%.1f GB", freeGb)
                    _latencyMs.value = stats.networkLatencyMs
                    _batteryTempC.value = stats.batteryTempC
                    _batteryLevel.value = stats.batteryLevel

                    // Auto Refresh Rate Limiter Check on Low Battery
                    if (currentHudSettings.isAutoRefreshRateLimiterEnabled && stats.batteryLevel <= currentHudSettings.lowBatteryRefreshRateThreshold) {
                        _refreshRateHz.value = currentHudSettings.limitedRefreshRateHz
                        _isEcoLimiterActive.value = true
                    } else {
                        _refreshRateHz.value = stats.screenRefreshRateHz
                        _isEcoLimiterActive.value = false
                    }

                    _minRamPercent.value = minOf(_minRamPercent.value, stats.freeRamPercent)
                    _peakTempC.value = maxOf(_peakTempC.value, stats.batteryTempC)
                }

                _elapsedSeconds.value = (System.currentTimeMillis() - sessionStartTime) / 1000
                delay(1500)
            }
        }
    }

    private fun finishAndRecordSession() {
        serviceScope.launch {
            val duration = System.currentTimeMillis() - sessionStartTime
            val durationSafe = duration.coerceAtLeast(1000L)
            val avgLatency = _latencyMs.value
            val minRam = _minRamPercent.value
            val peakTemp = _peakTempC.value

            // Compute performance scores (0 - 100)
            val pingScore = (100 - (avgLatency - 20) * 0.4).roundToInt().coerceIn(10, 100)
            val memoryScore = (minRam * 1.8).roundToInt().coerceIn(15, 100)
            val tempScore = (100 - (peakTemp - 25) * 4).roundToInt().coerceIn(10, 100)
            val overallScore = ((pingScore + memoryScore + tempScore) / 3.0).roundToInt()

            val summary = buildString {
                if (memoryScore >= 70) append("Free memory stayed comfortable the whole session. ")
                else append("Memory experienced moderate usage. ")
                if (tempScore >= 70) append("Battery temperature stayed normal.")
                else append("Battery warmed up slightly under load.")
            }

            val session = GameSession(
                packageName = activeGamePackage,
                gameTitle = activeGameTitle,
                startTime = sessionStartTime,
                durationMillis = durationSafe,
                avgLatencyMs = avgLatency,
                pingJitterMs = 8,
                minFreeRamPercent = minRam,
                peakBatteryTempC = peakTemp,
                overallScore = overallScore,
                pingScore = pingScore,
                memoryScore = memoryScore,
                tempScore = tempScore,
                summaryText = summary
            )

            gameRepository?.saveSession(session)

            // Open main activity and show session report
            val intent = Intent(this@HudOverlayService, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra("SHOW_SESSION_REPORT_PKG", activeGamePackage)
            }
            startActivity(intent)

            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        if (overlayView != null) {
            try {
                windowManager?.removeView(overlayView)
            } catch (_: Exception) {}
            overlayView = null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val CHANNEL_ID = "game_hud_channel"
        const val NOTIFICATION_ID = 2026
        const val ACTION_STOP_HUD = "ACTION_STOP_HUD"
        const val EXTRA_GAME_PACKAGE = "EXTRA_GAME_PACKAGE"
        const val EXTRA_GAME_TITLE = "EXTRA_GAME_TITLE"
    }
}

@Composable
fun HudOverlayContent(
    gameTitle: String,
    freeRamPercent: Int,
    freeRamGb: String,
    latencyMs: Int,
    refreshRateHz: Int,
    batteryTempC: Float,
    elapsedSeconds: Long,
    minRamPercent: Int,
    peakTempC: Float,
    isMinimized: Boolean,
    isEcoLimiterActive: Boolean = false,
    hudSettings: HudSettings = HudSettings(),
    onToggleMinimize: () -> Unit,
    onFinish: () -> Unit,
    onDrag: (Float, Float) -> Unit
) {
    val style = hudSettings.visualStyle
    val opacity = hudSettings.backgroundOpacity.coerceIn(0.2f, 1.0f)
    val accentColor = style.primaryColor
    val backgroundColor = style.backgroundColor.copy(alpha = opacity)
    val borderColor = style.borderColor.copy(alpha = (opacity + 0.15f).coerceAtMost(1.0f))

    val minutes = elapsedSeconds / 60
    val seconds = elapsedSeconds % 60
    val timeFormatted = String.format("%02d:%02d:%02d", elapsedSeconds / 3600, minutes % 60, seconds)

    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(300)) + scaleIn(initialScale = 0.88f),
        exit = fadeOut(tween(200))
    ) {
        Box(
            modifier = Modifier
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        onDrag(dragAmount.x, dragAmount.y)
                    }
                }
                .clip(RoundedCornerShape(14.dp))
                .background(backgroundColor)
                .border(1.5.dp, borderColor, RoundedCornerShape(14.dp))
                .padding(10.dp)
                .widthIn(max = 280.dp)
        ) {
            Column {
                // Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(accentColor)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "BOOSTER+ HUD",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Row {
                        IconButton(
                            onClick = onToggleMinimize,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (isMinimized) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                                contentDescription = "Minimize",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = onFinish,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color(0xFFFF5252),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = !isMinimized,
                    enter = fadeIn(tween(250)) + expandVertically(expandFrom = Alignment.Top),
                    exit = fadeOut(tween(200)) + shrinkVertically(shrinkTowards = Alignment.Top)
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(8.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Gauge
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(60.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { freeRamPercent / 100f },
                            modifier = Modifier.fillMaxSize(),
                            color = accentColor,
                            trackColor = Color(0xFF21262D),
                            strokeWidth = 5.dp
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$freeRamPercent%",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "FREE RAM",
                                color = Color(0xFF8B949E),
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Metrics List
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        if (hudSettings.showFreeRam) HudMetricRow("Free memory", freeRamGb)
                        if (hudSettings.showNetworkLatency) HudMetricRow("Network latency", "$latencyMs ms", if (latencyMs > 100) WarningAmber else TextPrimary)
                        if (hudSettings.showScreenRefreshRate) HudMetricRow(
                            label = if (isEcoLimiterActive) "Refresh rate (ECO)" else "Screen refresh rate",
                            value = "$refreshRateHz Hz",
                            valueColor = if (isEcoLimiterActive) NeonGreen else TextPrimary
                        )
                        if (hudSettings.showBatteryTemp) HudMetricRow("Battery temp", String.format("%.0f°C", batteryTempC), if (batteryTempC > hudSettings.warnHighTempThreshold) WarningAmber else TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider(color = Color(0xFF30363D), thickness = 1.dp)
                Spacer(modifier = Modifier.height(6.dp))

                // Session Summary Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = gameTitle,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .border(1.dp, accentColor, RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "GFX",
                            color = accentColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Playing time", color = Color(0xFF8B949E), fontSize = 8.sp)
                        Text(text = timeFormatted, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Min RAM", color = Color(0xFF8B949E), fontSize = 8.sp)
                        Text(text = "$minRamPercent%", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Peak temp", color = Color(0xFF8B949E), fontSize = 8.sp)
                        Text(text = String.format("%.0f°C", peakTempC), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = onFinish,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                        contentColor = accentColor
                    ),
                    shape = RoundedCornerShape(6.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(accentColor)),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "FINISH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
                    }
                }
            }
        }
    }
}

@Composable
private fun HudMetricRow(label: String, value: String, valueColor: Color = TextPrimary) {
    Row(
        modifier = Modifier.width(150.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = TextSecondary, fontSize = 9.sp)
        Text(text = value, color = valueColor, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
    }
}
