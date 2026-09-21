package com.example.system

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.data.model.GameItem

class PlayVitalsHelper(private val context: Context) {

    companion object {
        private const val CATEGORY_GAME = "android.intent.category.GAME"
    }

    private val packageManager: PackageManager = context.packageManager

    fun scanInstalledGames(): List<GameItem> {
        return getAllInstalledLaunchableApps().filter { it.isAutoDetected }
    }

    fun getAllInstalledLaunchableApps(): List<GameItem> {
        val allApps = mutableListOf<GameItem>()
        val categories = listOf(
            Intent.CATEGORY_LAUNCHER,
            Intent.CATEGORY_LEANBACK_LAUNCHER,
            CATEGORY_GAME
        )

        val myPackage = context.packageName
        val seen = mutableSetOf<String>()

        for (category in categories) {
            val intent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(category)
            }
            val resolveInfos = try {
                packageManager.queryIntentActivities(intent, 0)
            } catch (e: Exception) {
                emptyList()
            }

            for (ri in resolveInfos) {
                val pkg = ri.activityInfo.packageName
                if (pkg == myPackage || seen.contains(pkg)) continue
                seen.add(pkg)

                val label = ri.loadLabel(packageManager).toString()
                val isGame = try {
                    val appInfo = packageManager.getApplicationInfo(pkg, 0)
                    isApplicationGame(appInfo) || category == CATEGORY_GAME
                } catch (e: Exception) {
                    category == CATEGORY_GAME
                }

                allApps.add(
                    GameItem(
                        packageName = pkg,
                        title = label,
                        isAutoDetected = isGame,
                        isInLauncher = isGame,
                        iconPresetIndex = (pkg.hashCode() % 6).let { if (it < 0) it + 6 else it }
                    )
                )
            }
        }

        return allApps
    }

    private fun isApplicationGame(app: ApplicationInfo): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            app.category == ApplicationInfo.CATEGORY_GAME
        } else {
            @Suppress("DEPRECATION")
            (app.flags and ApplicationInfo.FLAG_IS_GAME) != 0
        }
    }

    fun launchGame(packageName: String): Boolean {
        return try {
            val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
