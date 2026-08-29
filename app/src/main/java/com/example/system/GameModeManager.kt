package com.example.system

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings

class GameModeManager(private val context: Context) {

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private var previousInterruptionFilter: Int? = null

    fun isNotificationPolicyAccessGranted(): Boolean {
        return notificationManager.isNotificationPolicyAccessGranted
    }

    fun openNotificationPolicySettings() {
        try {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }

    fun enableDoNotDisturb(): Boolean {
        if (!isNotificationPolicyAccessGranted()) {
            return false
        }
        return try {
            if (previousInterruptionFilter == null) {
                previousInterruptionFilter = notificationManager.currentInterruptionFilter
            }
            notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
            true
        } catch (e: Exception) {
            false
        }
    }

    fun restoreDoNotDisturb(): Boolean {
        if (!isNotificationPolicyAccessGranted()) {
            return false
        }
        return try {
            val filterToRestore = previousInterruptionFilter ?: NotificationManager.INTERRUPTION_FILTER_ALL
            notificationManager.setInterruptionFilter(filterToRestore)
            previousInterruptionFilter = null
            true
        } catch (e: Exception) {
            false
        }
    }
}
