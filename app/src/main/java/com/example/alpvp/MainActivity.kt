package com.example.alpvp

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.alpvp.data.container.AppContainer
import com.example.alpvp.notification.NotificationScheduler
import com.example.alpvp.ui.route.AppRouting
import com.example.alpvp.ui.theme.AlpVPTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
            loadAndScheduleNotifications()
        } else {
            Log.d("MainActivity", "Notification permission denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Request notification permission for Android 13+
        askNotificationPermission()
        
        setContent {
            AlpVPTheme {
                AppRouting()
            }
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                checkExactAlarmPermission()
            }
        } else {
            checkExactAlarmPermission()
        }
    }

    private fun checkExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w("MainActivity", "⚠️ SCHEDULE_EXACT_ALARM permission not granted!")
                Log.w("MainActivity", "Notifications may not work precisely. Please enable in settings.")
                // Still try to load notifications
                loadAndScheduleNotifications()
            } else {
                Log.d("MainActivity", "✅ SCHEDULE_EXACT_ALARM permission granted")
                loadAndScheduleNotifications()
            }
        } else {
            loadAndScheduleNotifications()
        }
    }

    private fun loadAndScheduleNotifications() {
        val appContainer = AppContainer(applicationContext)
        lifecycleScope.launch {
            try {
                val response = appContainer.appService.getNotificationSettings()
                if (response.isSuccessful && response.body() != null) {
                    val settings = response.body()!!.data
                    
                    if (settings.notificationEnabled) {
                        val scheduler = NotificationScheduler(applicationContext)
                        scheduler.scheduleNotifications(
                            breakfastTime = settings.breakfastTime,
                            lunchTime = settings.lunchTime,
                            dinnerTime = settings.dinnerTime,
                            snackTime = settings.snackTime
                        )
                        Log.d("MainActivity", "✅ Notifications scheduled")
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Failed to schedule", e)
            }
        }
    }
}
