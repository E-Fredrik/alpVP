package com.example.alpvp.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.alpvp.ui.viewModel.NotificationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsView(
    notificationViewModel: NotificationViewModel,
    onBack: () -> Unit
) {
    val uiState by notificationViewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        notificationViewModel.loadAndScheduleNotifications()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notification Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState.loading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Notification Toggle Card
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Enable Notifications",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Receive reminders to log your meals",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = uiState.settings?.notificationEnabled ?: true,
                        onCheckedChange = { enabled ->
                            val settings = uiState.settings
                            notificationViewModel.updateNotificationSettings(
                                enabled = enabled,
                                breakfastTime = settings?.breakfastTime,
                                lunchTime = settings?.lunchTime,
                                dinnerTime = settings?.dinnerTime,
                                snackTime = settings?.snackTime
                            )
                        },
                        enabled = !uiState.loading
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Meal Times Section
            Text(
                "Meal Reminder Times",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Set when you'd like to receive meal logging reminders",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Meal Time Cards
            val isEnabled = uiState.settings?.notificationEnabled ?: true

            MealTimeCard(
                mealName = "Breakfast",
                time = uiState.settings?.breakfastTime ?: "08:00",
                emoji = "🌅",
                enabled = isEnabled && !uiState.loading,
                onTimeChange = { time ->
                    val settings = uiState.settings
                    notificationViewModel.updateNotificationSettings(
                        enabled = settings?.notificationEnabled ?: true,
                        breakfastTime = time,
                        lunchTime = settings?.lunchTime,
                        dinnerTime = settings?.dinnerTime,
                        snackTime = settings?.snackTime
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            MealTimeCard(
                mealName = "Lunch",
                time = uiState.settings?.lunchTime ?: "12:00",
                emoji = "☀️",
                enabled = isEnabled && !uiState.loading,
                onTimeChange = { time ->
                    val settings = uiState.settings
                    notificationViewModel.updateNotificationSettings(
                        enabled = settings?.notificationEnabled ?: true,
                        breakfastTime = settings?.breakfastTime,
                        lunchTime = time,
                        dinnerTime = settings?.dinnerTime,
                        snackTime = settings?.snackTime
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            MealTimeCard(
                mealName = "Dinner",
                time = uiState.settings?.dinnerTime ?: "18:00",
                emoji = "🌙",
                enabled = isEnabled && !uiState.loading,
                onTimeChange = { time ->
                    val settings = uiState.settings
                    notificationViewModel.updateNotificationSettings(
                        enabled = settings?.notificationEnabled ?: true,
                        breakfastTime = settings?.breakfastTime,
                        lunchTime = settings?.lunchTime,
                        dinnerTime = time,
                        snackTime = settings?.snackTime
                    )
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            MealTimeCard(
                mealName = "Snack",
                time = uiState.settings?.snackTime ?: "15:00",
                emoji = "🍎",
                enabled = isEnabled && !uiState.loading,
                onTimeChange = { time ->
                    val settings = uiState.settings
                    notificationViewModel.updateNotificationSettings(
                        enabled = settings?.notificationEnabled ?: true,
                        breakfastTime = settings?.breakfastTime,
                        lunchTime = settings?.lunchTime,
                        dinnerTime = settings?.dinnerTime,
                        snackTime = time
                    )
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Success/Error Messages
            uiState.successMessage?.let { message ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✅", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            message,
                            color = Color(0xFF2E7D32),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                LaunchedEffect(message) {
                    kotlinx.coroutines.delay(3000)
                    notificationViewModel.clearMessages()
                }
            }

            uiState.error?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("❌", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            error,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun MealTimeCard(
    mealName: String,
    time: String,
    emoji: String,
    enabled: Boolean,
    onTimeChange: (String) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled) { showTimePicker = true }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    emoji,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    mealName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    time,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit time",
                    tint = if (enabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    // Time Picker Dialog
    if (showTimePicker) {
        CustomTimePickerDialog(
            currentTime = time,
            onDismiss = { showTimePicker = false },
            onTimeSelected = { newTime ->
                onTimeChange(newTime)
                showTimePicker = false
            }
        )
    }
}

@Composable
fun CustomTimePickerDialog(
    currentTime: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    // Parse current time (format: "HH:mm")
    val timeParts = currentTime.split(":")
    val initialHour = timeParts.getOrNull(0)?.toIntOrNull() ?: 8
    val initialMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

    var selectedHour by remember { mutableStateOf(initialHour) }
    var selectedMinute by remember { mutableStateOf(initialMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Time") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(horizontalArrangement = Arrangement.Center) {
                    // Hour picker
                    NumberPickerColumn(
                        value = selectedHour,
                        onValueChange = { selectedHour = it },
                        range = 0..23
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        ":",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    // Minute picker
                    NumberPickerColumn(
                        value = selectedMinute,
                        onValueChange = { selectedMinute = it },
                        range = 0..59
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // End of picker area
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val timeString = "%02d:%02d".format(selectedHour, selectedMinute)
                onTimeSelected(timeString)
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
