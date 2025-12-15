package com.example.alpvp.data.services

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.alpvp.MainActivity
import com.example.alpvp.ui.model.VulnerabilityLevel

/**
 * Smart Notification Manager
 * Uses AAR to determine when it's safe to send notifications
 */
class SmartNotificationManager(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)

    companion object {
        private const val CHANNEL_ID = "food_reminders"
        private const val CHANNEL_NAME = "Food Logging Reminders"
        private const val NOTIFICATION_ID_FOOD_REMINDER = 1001
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to log your meals"
                enableVibration(true)
            }

            val systemNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            systemNotificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Sends a food reminder notification ONLY if user is in safe state
     * @param vulnerabilityLevel Current AAR vulnerability level
     * @param title Notification title
     * @param message Notification message
     * @return true if notification was sent, false if blocked due to safety
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun sendFoodReminderIfSafe(
        vulnerabilityLevel: VulnerabilityLevel,
        title: String,
        message: String
    ): Boolean {
        // Block notifications if user is in unsafe state
        if (vulnerabilityLevel == VulnerabilityLevel.CRITICAL ||
            vulnerabilityLevel == VulnerabilityLevel.HIGH) {
            Log.d("SmartNotification",
                "Blocked notification - user in ${vulnerabilityLevel.name} state")
            return false
        }

        // Safe to send notification
        sendFoodReminder(title, message)
        return true
    }

    /**
     * Sends a food reminder notification (internal - use sendFoodReminderIfSafe)
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun sendFoodReminder(title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("open_food_log", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .addAction(
                android.R.drawable.ic_menu_add,
                "Log Food",
                pendingIntent
            )
            .build()

        try {
            notificationManager.notify(NOTIFICATION_ID_FOOD_REMINDER, notification)
            Log.d("SmartNotification", "Notification sent: $title")
        } catch (e: SecurityException) {
            Log.e("SmartNotification", "Permission denied for notifications", e)
        }
    }

    /**
     * Schedule a delayed notification attempt
     * If user is currently unsafe, this will retry when they become safe
     */
    fun scheduleDelayedReminder(
        title: String,
        message: String,
        delayMinutes: Int = 5
    ) {
        // This would use WorkManager to schedule the notification
        // For now, just log it
        android.util.Log.d("SmartNotification",
            "Scheduled reminder for $delayMinutes minutes: $title")
    }

    /**
     * Cancel all food reminder notifications
     */
    fun cancelFoodReminders() {
        notificationManager.cancel(NOTIFICATION_ID_FOOD_REMINDER)
    }
}

