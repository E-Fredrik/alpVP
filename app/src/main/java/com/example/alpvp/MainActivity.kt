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
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.alpvp.ui.route.AppRouting
import com.example.alpvp.ui.theme.AlpVPTheme
import com.example.alpvp.ui.viewModel.NotificationViewModel

class MainActivity : ComponentActivity() {

    private val notificationViewModel: NotificationViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("MainActivity", "Notification permission granted")
            notificationViewModel.loadAndScheduleNotifications()
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
                // Wrap the app routing and add a debug button overlayed at bottom-end
                AppContent(notificationViewModel = notificationViewModel)
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
                notificationViewModel.loadAndScheduleNotifications()
            } else {
                Log.d("MainActivity", "✅ SCHEDULE_EXACT_ALARM permission granted")
                notificationViewModel.loadAndScheduleNotifications()
            }
        } else {
            notificationViewModel.loadAndScheduleNotifications()
        }
    }
}

@Composable
private fun AppContent(notificationViewModel: NotificationViewModel) {
    Box(modifier = Modifier.fillMaxSize()) {
        AppRouting()

        // Debug button pinned to bottom-end for quick testing of trySmartNotify
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.End
        ) {
            Button(onClick = {
                notificationViewModel.trySmartNotify(
                    title = "Debug: Test",
                    message = "This is a debug notification"
                )
            }) {
                Text(text = "Debug Notify")
            }
        }
    }
}
