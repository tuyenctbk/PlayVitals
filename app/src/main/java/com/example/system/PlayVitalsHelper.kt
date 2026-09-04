package com.example.system

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import com.example.data.model.GameItem

class PlayVitalsHelper(private val context: Context) {

    private val packageManager: PackageManager = context.packageManager

    fun scanInstalledGames(): List<GameItem> {
        val detectedGames = mutableListOf<GameItem>()
        val installedApps = try {
            packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        } catch (e: Exception) {
            emptyList()
        }

        val myPackage = context.packageName

        for (app in installedApps) {
            if (app.packageName == myPackage) continue

            val isGame = isApplicationGame(app)
            val launchIntent = packageManager.getLaunchIntentForPackage(app.packageName)

            if (launchIntent != null && isGame) {
                val label = app.loadLabel(packageManager).toString()
                detectedGames.add(
                    GameItem(
                        packageName = app.packageName,
                        title = label,
                        isAutoDetected = true,
                        isInLauncher = true,
                        iconPresetIndex = (app.packageName.hashCode() % 6).let { if (it < 0) it + 6 else it }
                    )
                )
            }
        }

        return detectedGames
    }

    fun getAllInstalledLaunchableApps(): List<GameItem> {
        val allApps = mutableListOf<GameItem>()
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val resolveInfos = try {
            packageManager.queryIntentActivities(mainIntent, 0)
        } catch (e: Exception) {
            emptyList()
        }

        val myPackage = context.packageName
        val seen = mutableSetOf<String>()

        for (ri in resolveInfos) {
            val pkg = ri.activityInfo.packageName
            if (pkg == myPackage || seen.contains(pkg)) continue
            seen.add(pkg)

            val label = ri.loadLabel(packageManager).toString()
            val isGame = try {
                val appInfo = packageManager.getApplicationInfo(pkg, 0)
                isApplicationGame(appInfo)
            } catch (e: Exception) {
                false
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
