package com.example.viewmodel

import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.GameDatabase
import com.example.data.model.GameItem
import com.example.data.model.GameSession
import com.example.data.model.HudSettings
import com.example.data.model.ReflexScore
import com.example.data.repository.FirestoreSyncRepository
import com.example.data.repository.GameRepository
import com.example.service.HudOverlayService
import com.example.system.DeviceMonitor
import com.example.system.PlayVitalsHelper
import com.example.system.GameModeManager
import com.example.system.LiveDeviceStats
import com.example.system.OptimizationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.random.Random

enum class ActiveScreen {
    ONBOARDING,
    DASHBOARD,
    GAME_DETAIL,
    SESSION_REPORT,
    GAME_INSIGHTS,
    GFX_GUIDE,
    GAME_MODE,
    HUD_SETTINGS,
    REFLEX_TRAINER,
    MANAGE_GAMES
}

enum class ReflexDrillType {
    REACTION_SPEED,
    TARGET_BLITZ
}

enum class ReactionState {
    IDLE,
    WAITING_FOR_GREEN,
    READY_TAP_NOW,
    EARLY_TAP,
    RESULT
}

data class TargetPosition(
    val id: Int,
    val xPercent: Float,
    val yPercent: Float,
    val isHit: Boolean = false
)

data class GfxRecommendation(
    val graphicsLevel: String = "Smooth",
    val frameRate: String = "60 fps",
    val resolutionScale: String = "Native",
    val shadows: String = "Soft",
    val hardwareTier: String = "Mid-High Range",
    val isAnalyzing: Boolean = false
)

data class GameModeState(
    val isGameModeEnabled: Boolean = false,
    val clearMemoryEnabled: Boolean = true,
    val doNotDisturbEnabled: Boolean = false,
    val keepScreenAwakeEnabled: Boolean = true,
    val lastOptimizationResult: OptimizationResult? = null
)

class PlayVitalsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: GameRepository
    private val deviceMonitor: DeviceMonitor
    private val launcherHelper: PlayVitalsHelper
    private val gameModeManager: GameModeManager
    private val firestoreSyncRepository: FirestoreSyncRepository

    // Background scan state
    private val _isScanningPackageManager = MutableStateFlow(false)
    val isScanningPackageManager: StateFlow<Boolean> = _isScanningPackageManager.asStateFlow()

    private val _scannedGameCount = MutableStateFlow(0)
    val scannedGameCount: StateFlow<Int> = _scannedGameCount.asStateFlow()

    // Cloud Sync State
    private val _isCloudSyncing = MutableStateFlow(false)
    val isCloudSyncing: StateFlow<Boolean> = _isCloudSyncing.asStateFlow()

    private val _cloudSyncMessage = MutableStateFlow<String?>(null)
    val cloudSyncMessage: StateFlow<String?> = _cloudSyncMessage.asStateFlow()

    // Gemini Hardware Advisor State
    private val _geminiAdvice = MutableStateFlow<String?>(null)
    val geminiAdvice: StateFlow<String?> = _geminiAdvice.asStateFlow()

    private val _isGeneratingGeminiAdvice = MutableStateFlow(false)
    val isGeneratingGeminiAdvice: StateFlow<Boolean> = _isGeneratingGeminiAdvice.asStateFlow()

    // Navigation State
    private val _currentScreen = MutableStateFlow(ActiveScreen.DASHBOARD)
    val currentScreen: StateFlow<ActiveScreen> = _currentScreen.asStateFlow()

    // Smart Rate & Share Dialog State
    private val _showRateShareDialog = MutableStateFlow(false)
    val showRateShareDialog: StateFlow<Boolean> = _showRateShareDialog.asStateFlow()

    private val _selectedGame = MutableStateFlow<GameItem?>(null)
    val selectedGame: StateFlow<GameItem?> = _selectedGame.asStateFlow()

    private val _selectedSession = MutableStateFlow<GameSession?>(null)
    val selectedSession: StateFlow<GameSession?> = _selectedSession.asStateFlow()

    // Live Stats
    private val _liveStats = MutableStateFlow(LiveDeviceStats())
    val liveStats: StateFlow<LiveDeviceStats> = _liveStats.asStateFlow()

    // Game Mode State
    private val _gameModeState = MutableStateFlow(GameModeState())
    val gameModeState: StateFlow<GameModeState> = _gameModeState.asStateFlow()

    // HUD Settings
    private val _hudSettings = MutableStateFlow(HudSettings())
    val hudSettings: StateFlow<HudSettings> = _hudSettings.asStateFlow()

    // GFX Recommendations
    private val _gfxRecommendation = MutableStateFlow(GfxRecommendation())
    val gfxRecommendation: StateFlow<GfxRecommendation> = _gfxRecommendation.asStateFlow()

    // Reflex Trainer States
    private val _reflexDrillType = MutableStateFlow(ReflexDrillType.REACTION_SPEED)
    val reflexDrillType: StateFlow<ReflexDrillType> = _reflexDrillType.asStateFlow()

    // Reaction Drill State
    private val _reactionState = MutableStateFlow(ReactionState.IDLE)
    val reactionState: StateFlow<ReactionState> = _reactionState.asStateFlow()
    private val _reactionTimeResultMs = MutableStateFlow<Long?>(null)
    val reactionTimeResultMs: StateFlow<Long?> = _reactionTimeResultMs.asStateFlow()
    private var reactionStartTimeNano: Long = 0L

    // Target Blitz State
    private val _blitzTargets = MutableStateFlow<List<TargetPosition>>(emptyList())
    val blitzTargets: StateFlow<List<TargetPosition>> = _blitzTargets.asStateFlow()
    private val _blitzRemainingCount = MutableStateFlow(12)
    val blitzRemainingCount: StateFlow<Int> = _blitzRemainingCount.asStateFlow()
    private val _blitzTimeElapsedMs = MutableStateFlow(0L)
    val blitzTimeElapsedMs: StateFlow<Long> = _blitzTimeElapsedMs.asStateFlow()
    private val _isBlitzActive = MutableStateFlow(false)
    val isBlitzActive: StateFlow<Boolean> = _isBlitzActive.asStateFlow()
    private val _blitzFinalScore = MutableStateFlow<Long?>(null)
    val blitzFinalScore: StateFlow<Long?> = _blitzFinalScore.asStateFlow()

    // In-App Simulated Active Session (for play experience & instant report testing)
    private val _isSimulatedGamingSession = MutableStateFlow(false)
    val isSimulatedGamingSession: StateFlow<Boolean> = _isSimulatedGamingSession.asStateFlow()
    private val _simulatedElapsedSeconds = MutableStateFlow(0L)
    val simulatedElapsedSeconds: StateFlow<Long> = _simulatedElapsedSeconds.asStateFlow()
    private var sessionSimStartTime: Long = 0L

    // Data streams from Room
    val launcherGames: StateFlow<List<GameItem>>
    val allInstalledGames: StateFlow<List<GameItem>>
    val allSessions: StateFlow<List<GameSession>>
    val bestReactionScore: StateFlow<Long?>

    init {
        val database = GameDatabase.getInstance(application)
        repository = GameRepository(database.gameDao())
        deviceMonitor = DeviceMonitor(application)
        launcherHelper = PlayVitalsHelper(application)
        gameModeManager = GameModeManager(application)
        firestoreSyncRepository = FirestoreSyncRepository(application)

        launcherGames = repository.launcherGames.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allInstalledGames = repository.allGames.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        allSessions = repository.allSessions.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
        )
        bestReactionScore = repository.bestReactionScore.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5000), null
        )

        performBackgroundGameScan()
        loadSavedHudSettings()
        checkInitialOnboardingAndPrompts()
        startLiveStatsPolling()
        computeGfxRecommendations()
    }

    private fun checkInitialOnboardingAndPrompts() {
        val prefs = getApplication<Application>().getSharedPreferences("app_user_feedback_prefs", android.content.Context.MODE_PRIVATE)
        val hasCompletedOnboarding = prefs.getBoolean("has_completed_onboarding", false)
        if (!hasCompletedOnboarding) {
            _currentScreen.value = ActiveScreen.ONBOARDING
        }

        val appLaunchCount = prefs.getInt("app_launch_count", 0) + 1
        prefs.edit().putInt("app_launch_count", appLaunchCount).apply()

        checkSmartRateShareTrigger()
    }

    fun completeOnboarding() {
        val prefs = getApplication<Application>().getSharedPreferences("app_user_feedback_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean("has_completed_onboarding", true).apply()
        _currentScreen.value = ActiveScreen.DASHBOARD
    }

    fun openOnboarding() {
        _currentScreen.value = ActiveScreen.ONBOARDING
    }

    fun checkSmartRateShareTrigger() {
        val prefs = getApplication<Application>().getSharedPreferences("app_user_feedback_prefs", android.content.Context.MODE_PRIVATE)
        val hasRatedOrShared = prefs.getBoolean("has_rated_or_shared", false)
        val neverAskAgain = prefs.getBoolean("never_ask_again", false)
        val lastPrompt = prefs.getLong("last_prompt_timestamp", 0L)
        val launchCount = prefs.getInt("app_launch_count", 0)
        val boostCount = prefs.getInt("boost_count", 0)
        val sessionCount = prefs.getInt("completed_session_count", 0)

        if (hasRatedOrShared || neverAskAgain) return

        val now = System.currentTimeMillis()
        val hoursSinceLastPrompt = (now - lastPrompt) / (1000 * 60 * 60)

        // Trigger if not asked within 24h AND user hit any smart milestone (2+ sessions OR 3+ boosts OR 3+ launches)
        if (hoursSinceLastPrompt >= 24 && (sessionCount >= 2 || boostCount >= 3 || launchCount >= 3)) {
            _showRateShareDialog.value = true
            prefs.edit().putLong("last_prompt_timestamp", now).apply()
        }
    }

    fun recordBoostAction() {
        val prefs = getApplication<Application>().getSharedPreferences("app_user_feedback_prefs", android.content.Context.MODE_PRIVATE)
        val current = prefs.getInt("boost_count", 0) + 1
        prefs.edit().putInt("boost_count", current).apply()
        checkSmartRateShareTrigger()
    }

    fun recordSessionCompleteAction() {
        val prefs = getApplication<Application>().getSharedPreferences("app_user_feedback_prefs", android.content.Context.MODE_PRIVATE)
        val current = prefs.getInt("completed_session_count", 0) + 1
        prefs.edit().putInt("completed_session_count", current).apply()
        checkSmartRateShareTrigger()
    }

    fun openRateShareDialogManually() {
        _showRateShareDialog.value = true
    }

    fun dismissRateShareDialog() {
        _showRateShareDialog.value = false
    }

    fun onRateAppClicked(context: android.content.Context) {
        val prefs = context.getSharedPreferences("app_user_feedback_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean("has_rated_or_shared", true).apply()
        _showRateShareDialog.value = false

        try {
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("market://details?id=${context.packageName}"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    fun onShareAppClicked(context: android.content.Context) {
        val prefs = context.getSharedPreferences("app_user_feedback_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean("has_rated_or_shared", true).apply()
        _showRateShareDialog.value = false

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, context.getString(com.example.R.string.share_app_message))
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, context.getString(com.example.R.string.share_app_btn))
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(shareIntent)
    }

    fun onRemindRateLater() {
        val prefs = getApplication<Application>().getSharedPreferences("app_user_feedback_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putLong("last_prompt_timestamp", System.currentTimeMillis()).apply()
        _showRateShareDialog.value = false
    }

    fun onNeverAskRateAgain() {
        val prefs = getApplication<Application>().getSharedPreferences("app_user_feedback_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean("never_ask_again", true).apply()
        _showRateShareDialog.value = false
    }

    private fun loadSavedHudSettings() {
        val prefs = getApplication<Application>().getSharedPreferences("hud_prefs", android.content.Context.MODE_PRIVATE)
        _hudSettings.value = HudSettings(
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

    fun performBackgroundGameScan() {
        viewModelScope.launch(Dispatchers.IO) {
            _isScanningPackageManager.value = true
            try {
                val detected = launcherHelper.getAllInstalledLaunchableApps()
                _scannedGameCount.value = detected.count { it.isAutoDetected }
                repository.insertGames(detected)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isScanningPackageManager.value = false
            }
        }
    }

    fun generateGeminiHardwareAdvice() {
        viewModelScope.launch(Dispatchers.IO) {
            _isGeneratingGeminiAdvice.value = true
            val advisor = com.example.system.GeminiHardwareAdvisor()
            val advice = advisor.getHardwareOptimizationTips(_liveStats.value)
            _geminiAdvice.value = advice
            _isGeneratingGeminiAdvice.value = false
        }
    }

    fun backupRoomToCloud() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!firestoreSyncRepository.isSignedIn) {
                _cloudSyncMessage.value = "Sign in with Google required for Backup"
                return@launch
            }
            _isCloudSyncing.value = true
            _cloudSyncMessage.value = "Backing up local Room sessions & tags..."
            val success = firestoreSyncRepository.syncFullBackupToCloud(allSessions.value, launcherGames.value)
            _isCloudSyncing.value = false
            _cloudSyncMessage.value = if (success) "Room Database backup uploaded successfully!" else "Backup failed"
        }
    }

    fun restoreRoomFromCloud() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!firestoreSyncRepository.isSignedIn) {
                _cloudSyncMessage.value = "Sign in with Google required for Restore"
                return@launch
            }
            _isCloudSyncing.value = true
            _cloudSyncMessage.value = "Restoring Room database from cloud..."
            val backup = firestoreSyncRepository.restoreFullBackupFromCloud()
            if (backup != null) {
                val (restoredSessions, restoredTagsMap) = backup
                for (s in restoredSessions) {
                    repository.saveSession(s)
                }
                for ((pkg, tags) in restoredTagsMap) {
                    val existing = allInstalledGames.value.firstOrNull { it.packageName == pkg }
                    if (existing != null) {
                        repository.insertOrUpdateGame(existing.copy(tagsCsv = tags))
                    }
                }
                _cloudSyncMessage.value = "Restored ${restoredSessions.size} sessions & tag mappings!"
            } else {
                _cloudSyncMessage.value = "No cloud backup found for this account"
            }
            _isCloudSyncing.value = false
        }
    }

    fun syncGameSessionsAndSettingsWithCloud() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!firestoreSyncRepository.isSignedIn) {
                _cloudSyncMessage.value = "Sign in with Google required for Cloud Sync"
                return@launch
            }

            _isCloudSyncing.value = true
            _cloudSyncMessage.value = "Syncing with Firebase Firestore..."

            var successCount = 0
            for (s in allSessions.value) {
                val synced = firestoreSyncRepository.syncSessionToCloud(s)
                if (synced) successCount++
            }

            val settingsSynced = firestoreSyncRepository.syncSettingsToCloud(_hudSettings.value)

            val cloudSessions = firestoreSyncRepository.fetchCloudSessions()
            if (cloudSessions.isNotEmpty()) {
                for (cs in cloudSessions) {
                    repository.saveSession(cs)
                }
            }

            _isCloudSyncing.value = false
            _cloudSyncMessage.value = if (settingsSynced) "Cloud sync complete ($successCount sessions synced)" else "Sync finished"
        }
    }

    private fun startLiveStatsPolling() {
        viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                val ping = deviceMonitor.measureNetworkLatency()
                val current = deviceMonitor.readCurrentStats(ping)
                _liveStats.value = current
                delay(2000)
            }
        }
    }

    // Navigation methods
    fun navigateTo(screen: ActiveScreen) {
        _currentScreen.value = screen
    }

    fun openGameDetail(game: GameItem) {
        _selectedGame.value = game
        _currentScreen.value = ActiveScreen.GAME_DETAIL
    }

    fun openSessionReport(session: GameSession) {
        _selectedSession.value = session
        _currentScreen.value = ActiveScreen.SESSION_REPORT
    }

    fun openSessionReportByPackage(pkg: String) {
        viewModelScope.launch {
            val sessions = allSessions.value.filter { it.packageName == pkg }
            if (sessions.isNotEmpty()) {
                _selectedSession.value = sessions.first()
                _currentScreen.value = ActiveScreen.SESSION_REPORT
            }
        }
    }

    // Game Actions
    fun launchGame(game: GameItem) {
        val app = getApplication<Application>()
        _selectedGame.value = game

        // If HUD is enabled and has permission, launch floating service
        if (game.isHudEnabled && _hudSettings.value.isHudMasterEnabled) {
            try {
                val serviceIntent = Intent(app, HudOverlayService::class.java).apply {
                    putExtra(HudOverlayService.EXTRA_GAME_PACKAGE, game.packageName)
                    putExtra(HudOverlayService.EXTRA_GAME_TITLE, game.displayName)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    app.startForegroundService(serviceIntent)
                } else {
                    app.startService(serviceIntent)
                }
            } catch (_: Exception) {}
        }

        // Try launching real installed app
        val launched = launcherHelper.launchGame(game.packageName)
        if (!launched) {
            // Start in-app simulated session so the user can test the HUD & session report!
            startSimulatedGamingSession(game)
        }
    }

    fun toggleGameInLauncher(game: GameItem, isInLauncher: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setGameInLauncher(game.packageName, isInLauncher)
        }
    }

    fun toggleGameHud(game: GameItem, isEnabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.setGameHudEnabled(game.packageName, isEnabled)
            _selectedGame.value = _selectedGame.value?.copy(isHudEnabled = isEnabled)
        }
    }

    fun updateGameTags(game: GameItem, newTags: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            val formattedTags = newTags
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinct()
                .joinToString(",")
            val updated = game.copy(tagsCsv = formattedTags)
            repository.insertOrUpdateGame(updated)
            if (_selectedGame.value?.packageName == game.packageName) {
                _selectedGame.value = updated
            }
        }
    }

    fun refreshInstalledGames() {
        viewModelScope.launch(Dispatchers.IO) {
            val detected = launcherHelper.getAllInstalledLaunchableApps()
            repository.insertGames(detected)
        }
    }

    // Game Mode Operations
    fun toggleGameMode(enabled: Boolean) {
        _gameModeState.value = _gameModeState.value.copy(isGameModeEnabled = enabled)
        if (enabled) {
            if (_gameModeState.value.clearMemoryEnabled) {
                cleanBackgroundProcesses()
            }
            if (_gameModeState.value.doNotDisturbEnabled) {
                gameModeManager.enableDoNotDisturb()
            }
        } else {
            gameModeManager.restoreDoNotDisturb()
        }
    }

    fun toggleClearMemory(enabled: Boolean) {
        _gameModeState.value = _gameModeState.value.copy(clearMemoryEnabled = enabled)
    }

    fun toggleDoNotDisturb(enabled: Boolean) {
        _gameModeState.value = _gameModeState.value.copy(doNotDisturbEnabled = enabled)
        if (_gameModeState.value.isGameModeEnabled) {
            if (enabled) gameModeManager.enableDoNotDisturb()
            else gameModeManager.restoreDoNotDisturb()
        }
    }

    fun toggleKeepScreenAwake(enabled: Boolean) {
        _gameModeState.value = _gameModeState.value.copy(keepScreenAwakeEnabled = enabled)
    }

    fun cleanBackgroundProcesses() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = deviceMonitor.optimizeBackgroundProcesses()
            _gameModeState.value = _gameModeState.value.copy(lastOptimizationResult = result)
            // Refresh stats
            val current = deviceMonitor.readCurrentStats(_liveStats.value.networkLatencyMs)
            _liveStats.value = current
        }
    }

    // HUD Settings
    fun updateHudSettings(updater: (HudSettings) -> HudSettings) {
        val newSettings = updater(_hudSettings.value)
        _hudSettings.value = newSettings
        saveHudSettingsToPrefs(newSettings)
    }

    private fun saveHudSettingsToPrefs(settings: HudSettings) {
        val prefs = getApplication<Application>().getSharedPreferences("hud_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit()
            .putBoolean("auto_refresh_limiter", settings.isAutoRefreshRateLimiterEnabled)
            .putInt("low_battery_threshold", settings.lowBatteryRefreshRateThreshold)
            .putInt("limited_refresh_rate", settings.limitedRefreshRateHz)
            .putBoolean("show_free_ram", settings.showFreeRam)
            .putBoolean("show_latency", settings.showNetworkLatency)
            .putBoolean("show_refresh_rate", settings.showScreenRefreshRate)
            .putBoolean("show_battery_temp", settings.showBatteryTemp)
            .putFloat("bg_opacity", settings.backgroundOpacity)
            .putInt("warn_low_ram", settings.warnLowRamThreshold)
            .putFloat("warn_high_temp", settings.warnHighTempThreshold)
            .apply()
    }

    // GFX Recommendations
    fun computeGfxRecommendations() {
        viewModelScope.launch(Dispatchers.IO) {
            _gfxRecommendation.value = _gfxRecommendation.value.copy(isAnalyzing = true)
            delay(600) // Brief calculation animation

            val ramBytes = _liveStats.value.totalRamBytes
            val ramGb = ramBytes / (1024.0 * 1024.0 * 1024.0)
            val cores = _liveStats.value.cpuCores
            val refreshHz = _liveStats.value.screenRefreshRateHz

            val (graphics, fps, res, shadows, tier) = when {
                ramGb >= 7.0 && cores >= 8 -> {
                    GfxRecommendation("Ultra / Extreme", if (refreshHz >= 90) "${refreshHz} fps" else "60 fps", "Native", "Dynamic (High)", "Flagship Tier")
                }
                ramGb >= 4.0 && cores >= 6 -> {
                    GfxRecommendation("Balanced / HD", "60 fps", "Native", "Soft", "Mid-High Tier")
                }
                else -> {
                    GfxRecommendation("Smooth", "30 fps", "Lower one step", "Off", "Standard Tier")
                }
            }

            _gfxRecommendation.value = GfxRecommendation(
                graphicsLevel = graphics,
                frameRate = fps,
                resolutionScale = res,
                shadows = shadows,
                hardwareTier = tier,
                isAnalyzing = false
            )
        }
    }

    // In-App Simulated Gaming Session (for testing reports & in-app HUD)
    private fun startSimulatedGamingSession(game: GameItem) {
        sessionSimStartTime = System.currentTimeMillis()
        _isSimulatedGamingSession.value = true
        _simulatedElapsedSeconds.value = 0L

        viewModelScope.launch {
            while (_isSimulatedGamingSession.value) {
                delay(1000)
                _simulatedElapsedSeconds.value = (System.currentTimeMillis() - sessionSimStartTime) / 1000
            }
        }
    }

    fun finishSimulatedGamingSession() {
        val game = _selectedGame.value ?: return
        _isSimulatedGamingSession.value = false
        val duration = System.currentTimeMillis() - sessionSimStartTime
        val durationSafe = duration.coerceAtLeast(30000L) // at least 30s for rich realistic report

        val latency = _liveStats.value.networkLatencyMs
        val minRam = _liveStats.value.freeRamPercent
        val peakTemp = _liveStats.value.batteryTempC

        val pingScore = (100 - (latency - 20) * 0.4).roundToInt().coerceIn(20, 100)
        val memScore = (minRam * 1.8).roundToInt().coerceIn(30, 100)
        val tempScore = (100 - (peakTemp - 25) * 4).roundToInt().coerceIn(25, 100)
        val overall = ((pingScore + memScore + tempScore) / 3.0).roundToInt()

        val summary = buildString {
            if (minRam >= 40) append("Free memory stayed comfortable the whole session. ")
            else append("Memory tightened slightly during intense rendering. ")
            if (peakTemp <= 32f) append("Battery temperature stayed normal.")
            else append("Battery warmed up under graphics load.")
        }

        val session = GameSession(
            packageName = game.packageName,
            gameTitle = game.displayName,
            startTime = sessionSimStartTime,
            durationMillis = durationSafe,
            avgLatencyMs = latency,
            pingJitterMs = Random.nextInt(4, 12),
            minFreeRamPercent = minRam,
            peakBatteryTempC = peakTemp,
            overallScore = overall,
            pingScore = pingScore,
            memoryScore = memScore,
            tempScore = tempScore,
            summaryText = summary
        )

        viewModelScope.launch(Dispatchers.IO) {
            repository.saveSession(session)
            if (firestoreSyncRepository.isSignedIn) {
                firestoreSyncRepository.syncSessionToCloud(session)
            }
            _selectedSession.value = session
            _currentScreen.value = ActiveScreen.SESSION_REPORT
        }
    }

    // Reflex Trainer: Reaction Speed Drill
    fun setReflexDrillType(type: ReflexDrillType) {
        _reflexDrillType.value = type
        resetReflexDrills()
    }

    fun resetReflexDrills() {
        _reactionState.value = ReactionState.IDLE
        _reactionTimeResultMs.value = null
        _isBlitzActive.value = false
        _blitzFinalScore.value = null
        _blitzRemainingCount.value = 12
    }

    fun startReactionDrill() {
        _reactionState.value = ReactionState.WAITING_FOR_GREEN
        _reactionTimeResultMs.value = null

        viewModelScope.launch {
            // Random delay between 1.8s and 4.5s
            val delayMs = Random.nextLong(1800, 4500)
            delay(delayMs)
            if (_reactionState.value == ReactionState.WAITING_FOR_GREEN) {
                reactionStartTimeNano = SystemClock.elapsedRealtimeNanos()
                _reactionState.value = ReactionState.READY_TAP_NOW
            }
        }
    }

    fun handleReactionTap() {
        when (_reactionState.value) {
            ReactionState.WAITING_FOR_GREEN -> {
                // Tapped too early
                _reactionState.value = ReactionState.EARLY_TAP
            }
            ReactionState.READY_TAP_NOW -> {
                val endNanos = SystemClock.elapsedRealtimeNanos()
                val elapsedMs = (endNanos - reactionStartTimeNano) / 1_000_000
                _reactionTimeResultMs.value = elapsedMs
                _reactionState.value = ReactionState.RESULT

                val rank = when {
                    elapsedMs < 200 -> "Godlike"
                    elapsedMs < 250 -> "Pro"
                    elapsedMs < 320 -> "Solid"
                    else -> "Casual"
                }

                viewModelScope.launch(Dispatchers.IO) {
                    repository.saveReflexScore(
                        ReflexScore(
                            drillType = "REACTION_SPEED",
                            scoreValueMs = elapsedMs,
                            rankBadge = rank
                        )
                    )
                }
            }
            ReactionState.EARLY_TAP, ReactionState.RESULT, ReactionState.IDLE -> {
                startReactionDrill()
            }
        }
    }

    // Reflex Trainer: Target Blitz Drill (12 Targets)
    fun startTargetBlitzDrill() {
        _blitzRemainingCount.value = 12
        _blitzTimeElapsedMs.value = 0L
        _blitzFinalScore.value = null
        _isBlitzActive.value = true

        spawnNextTarget()

        viewModelScope.launch {
            val startTime = SystemClock.elapsedRealtime()
            while (_isBlitzActive.value && _blitzRemainingCount.value > 0) {
                delay(20)
                _blitzTimeElapsedMs.value = SystemClock.elapsedRealtime() - startTime
            }
        }
    }

    private fun spawnNextTarget() {
        val nextX = Random.nextFloat() * 0.7f + 0.15f
        val nextY = Random.nextFloat() * 0.6f + 0.2f
        _blitzTargets.value = listOf(
            TargetPosition(id = 12 - _blitzRemainingCount.value + 1, xPercent = nextX, yPercent = nextY)
        )
    }

    fun hitBlitzTarget() {
        if (!_isBlitzActive.value) return
        val currentRemaining = _blitzRemainingCount.value - 1
        _blitzRemainingCount.value = currentRemaining

        if (currentRemaining <= 0) {
            _isBlitzActive.value = false
            val totalTime = _blitzTimeElapsedMs.value
            _blitzFinalScore.value = totalTime
            _blitzTargets.value = emptyList()

            viewModelScope.launch(Dispatchers.IO) {
                repository.saveReflexScore(
                    ReflexScore(
                        drillType = "TARGET_BLITZ",
                        scoreValueMs = totalTime,
                        rankBadge = if (totalTime < 4500) "Legendary" else if (totalTime < 7000) "Pro" else "Trainee"
                    )
                )
            }
        } else {
            spawnNextTarget()
        }
    }
}
