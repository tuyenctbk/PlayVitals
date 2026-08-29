package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.ui.screens.*
import com.example.ui.theme.DarkCanvas
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.ActiveScreen
import com.example.viewmodel.PlayVitalsViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: PlayVitalsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIntentExtras(intent)

        setContent {
            val hudSettings by viewModel.hudSettings.collectAsState()

            MyApplicationTheme(themeMode = hudSettings.themeMode) {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(androidx.compose.material3.MaterialTheme.colorScheme.background)
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .systemBarsPadding()
                    ) {
                        PlayVitalsAppContent(viewModel)
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntentExtras(intent)
    }

    private fun handleIntentExtras(intent: Intent?) {
        val reportPkg = intent?.getStringExtra("SHOW_SESSION_REPORT_PKG")
        if (!reportPkg.isNullOrBlank()) {
            viewModel.openSessionReportByPackage(reportPkg)
        }
    }
}

@Composable
fun PlayVitalsAppContent(viewModel: PlayVitalsViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val liveStats by viewModel.liveStats.collectAsState()
    val launcherGames by viewModel.launcherGames.collectAsState()
    val allGames by viewModel.allInstalledGames.collectAsState()
    val allSessions by viewModel.allSessions.collectAsState()
    val gameModeState by viewModel.gameModeState.collectAsState()
    val hudSettings by viewModel.hudSettings.collectAsState()
    val gfxRecommendation by viewModel.gfxRecommendation.collectAsState()
    val bestReactionMs by viewModel.bestReactionScore.collectAsState()
    val selectedGame by viewModel.selectedGame.collectAsState()
    val selectedSession by viewModel.selectedSession.collectAsState()

    val showRateShareDialog by viewModel.showRateShareDialog.collectAsState()

    // Handle back button on sub-screens
    if (currentScreen != ActiveScreen.DASHBOARD && currentScreen != ActiveScreen.ONBOARDING) {
        BackHandler {
            viewModel.navigateTo(ActiveScreen.DASHBOARD)
        }
    }

    when (currentScreen) {
        ActiveScreen.ONBOARDING -> {
            OnboardingScreen(
                viewModel = viewModel
            )
        }
        ActiveScreen.DASHBOARD -> {
            DashboardScreen(
                viewModel = viewModel,
                liveStats = liveStats,
                launcherGames = launcherGames,
                gameModeState = gameModeState,
                bestReactionMs = bestReactionMs,
                allSessions = allSessions
            )
        }
        ActiveScreen.GAME_DETAIL -> {
            selectedGame?.let { game ->
                GameDetailScreen(
                    viewModel = viewModel,
                    game = game,
                    allSessions = allSessions
                )
            } ?: run {
                viewModel.navigateTo(ActiveScreen.DASHBOARD)
            }
        }
        ActiveScreen.SESSION_REPORT -> {
            selectedSession?.let { session ->
                SessionReportScreen(
                    viewModel = viewModel,
                    session = session
                )
            } ?: run {
                viewModel.navigateTo(ActiveScreen.DASHBOARD)
            }
        }
        ActiveScreen.GAME_INSIGHTS -> {
            GameInsightsScreen(
                viewModel = viewModel,
                liveStats = liveStats,
                allGames = allGames,
                allSessions = allSessions
            )
        }
        ActiveScreen.GFX_GUIDE -> {
            GfxGuideScreen(
                viewModel = viewModel,
                liveStats = liveStats,
                gfx = gfxRecommendation
            )
        }
        ActiveScreen.GAME_MODE -> {
            GameModeScreen(
                viewModel = viewModel,
                gameModeState = gameModeState
            )
        }
        ActiveScreen.HUD_SETTINGS -> {
            HudSettingsScreen(
                viewModel = viewModel,
                hudSettings = hudSettings
            )
        }
        ActiveScreen.REFLEX_TRAINER -> {
            ReflexTrainerScreen(
                viewModel = viewModel
            )
        }
        ActiveScreen.MANAGE_GAMES -> {
            ManageGamesScreen(
                viewModel = viewModel,
                allGames = allGames
            )
        }
    }

    // Auto Smart Rate & Share Dialog Popup
    if (showRateShareDialog) {
        com.example.ui.components.RateShareDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.dismissRateShareDialog() }
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
