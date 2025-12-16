package com.example.alpvp.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.alpvp.MainActivity
import com.example.alpvp.R
import com.example.alpvp.data.container.AppContainer
import com.example.alpvp.data.dto.NotificationTrigger
//import com.example.alpvp.ui.model.VulnerabilityLevel
import com.google.android.gms.location.LocationServices
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await

/**
 * Smart Notification Manager
 * Uses AAR and location to determine when it's safe to send notifications
 */
class SmartNotificationManager(private val context: Context) {

    private val notificationManager = NotificationManagerCompat.from(context)
    private val container = AppContainer(context)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    companion object {
        private const val CHANNEL_ID = "food_reminders"
        private const val CHANNEL_NAME = "Food Logging Reminders"
        private const val NOTIFICATION_ID_FOOD_REMINDER = 1001
        const val CHANNEL_ID_LOCATION = "location_based"
        const val CHANNEL_NAME_LOCATION = "Location-Based Notifications"
    }

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val foodChannel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminders to log your meals"
                enableVibration(true)
            }

            val locationChannel = NotificationChannel(
                CHANNEL_ID_LOCATION,
                CHANNEL_NAME_LOCATION,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Location-based activity reminders"
                enableVibration(true)
            }

            val systemNotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            systemNotificationManager.createNotificationChannel(foodChannel)
            systemNotificationManager.createNotificationChannel(locationChannel)
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


    /**
     * Sends a food reminder notification (internal - use sendFoodReminderIfSafe)
     */
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
        android.util.Log.d(
            "SmartNotification",
            "Scheduled reminder for $delayMinutes minutes: $title"
        )
    }

    /**
     * Cancel all food reminder notifications
     */
    fun cancelAllNotifications() {
        notificationManager.cancel(NOTIFICATION_ID_FOOD_REMINDER)
    }

    /**
     * Check location and send notifications based on backend triggers
     */
//    suspend fun checkLocationAndNotify(userId: Int) {
//        // Check location permission
//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            return
//        }
//
//        try {
//            // Get current location
//            val location: Location = fusedLocationClient.lastLocation. ?: return
//
//            // Call backend to check for notification triggers
//            val response = container.appService.checkLocationTriggers(
//                userId = userId,
//                latitude = location.latitude,
//                longitude = location.longitude
//            )
//
//            if (response.isSuccessful && response.body()?.success == true) {
//                val triggers = response.body()!!.data.triggers
//
//                // Show notifications for each trigger
//                triggers.forEach { trigger ->
//                    showLocationNotification(trigger)
//                }
//            }
//        } catch (e: Exception) {
//            Log.e("SmartNotification", "Failed to check location triggers", e)
//        }
//    }

    /**
     * Show notification based on location trigger
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showLocationNotification(trigger: NotificationTrigger) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            trigger.placeId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_LOCATION)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(trigger.placeName)
            .setContentText(trigger.message)
            .setPriority(
                when (trigger.priority) {
                    "HIGH" -> NotificationCompat.PRIORITY_HIGH
                    "MEDIUM" -> NotificationCompat.PRIORITY_DEFAULT
                    else -> NotificationCompat.PRIORITY_LOW
                }
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .apply {
                if (trigger.shouldVibrate) {
                    setVibrate(longArrayOf(0, 500, 200, 500))
                }
            }
            .build()

        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            notificationManager.notify(trigger.placeId, notification)
            Log.d("SmartNotification", "Location notification sent: ${trigger.placeName}")
        }
    }

    /**
     * Start periodic location monitoring
     */
//    fun startLocationMonitoring(userId: Int) {
//        CoroutineScope(Dispatchers.IO).launch {
//            while (true) {
//                checkLocationAndNotify(userId)
//                kotlinx.coroutines.delay(5 * 60 * 1000) // Check every 5 minutes
//            }
//        }
//    }
}
