package com.example.alpvp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.alpvp.ui.viewModel.AuthViewModel
import com.example.alpvp.data.dto.NotificationSettings
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    dashboardViewModel: com.example.alpvp.ui.viewModel.DashboardViewModel,
    appService: com.example.alpvp.data.services.AppService,
    modifier: Modifier = Modifier
) {
    val authUiState by authViewModel.uiState.collectAsStateWithLifecycle()
    val dashboardUiState by dashboardViewModel.uiState.collectAsStateWithLifecycle()
    val bg = Brush.verticalGradient(listOf(Color(0xFFF3F7FB), Color(0xFFEFF4FB)))

    // Load profile data if not already loaded
    LaunchedEffect(authUiState.token) {
        authUiState.token?.let { token ->
            if (dashboardUiState.userProfile == null && !dashboardUiState.loading) {
                dashboardViewModel.loadDashboardData(token)
            }
        }
    }

    val userProfile = dashboardUiState.userProfile

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF4F8BFF),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(bg)
                .padding(padding)
                .padding(20.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile icon
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(Color(0xFF4F8BFF), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User info card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        if (dashboardUiState.loading) {
                            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading profile...", color = Color.Gray)
                        } else {
                            Text(
                                text = userProfile?.username ?: "User",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = userProfile?.email ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Divider(color = Color.LightGray)

                            Spacer(modifier = Modifier.height(16.dp))

                            // Stats
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${userProfile?.height ?: 0}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Text("Height (cm)", color = Color.Gray, fontSize = 12.sp)
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "%.1f".format(userProfile?.weight ?: 0.0),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Text("Weight (kg)", color = Color.Gray, fontSize = 12.sp)
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "%.1f".format(userProfile?.bmiGoal ?: 0.0),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                    )
                                    Text("BMI Goal", color = Color.Gray, fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Current BMI
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "%.1f".format(userProfile?.bmi ?: 0.0),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        color = Color(0xFF4F8BFF)
                                    )
                                    Text("Current BMI", color = Color.Gray, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Notification Settings State
                var showNotificationDialog by remember { mutableStateOf(false) }

                // Notification Settings button
                Button(
                    onClick = {
                        showNotificationDialog = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F8BFF)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Notification Settings", color = Color.White, fontWeight = FontWeight.Bold)
                }

                // Show notification settings dialog
                if (showNotificationDialog) {
                    NotificationSettingsDialog(
                        authViewModel = authViewModel,
                        appService = appService,
                        onDismiss = { showNotificationDialog = false }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logout button
                Button(
                    onClick = { authViewModel.logout() },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4F4F)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsDialog(
    authViewModel: AuthViewModel,
    appService: com.example.alpvp.data.services.AppService,
    onDismiss: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // State for notification settings
    var notificationEnabled by remember { mutableStateOf(true) }
    var breakfastTime by remember { mutableStateOf("08:00") }
    var lunchTime by remember { mutableStateOf("12:00") }
    var dinnerTime by remember { mutableStateOf("18:00") }
    var snackTime by remember { mutableStateOf("15:00") }

    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Time picker states
    var showBreakfastPicker by remember { mutableStateOf(false) }
    var showLunchPicker by remember { mutableStateOf(false) }
    var showDinnerPicker by remember { mutableStateOf(false) }
    var showSnackPicker by remember { mutableStateOf(false) }

    // Load current notification settings
    LaunchedEffect(Unit) {
        try {
            val response = appService.getNotificationSettings()
                if (response.isSuccessful && response.body()?.success == true) {
                    val settings = response.body()?.data
                    notificationEnabled = settings?.notificationEnabled ?: true
                    breakfastTime = settings?.breakfastTime ?: "08:00"
                    lunchTime = settings?.lunchTime ?: "12:00"
                    dinnerTime = settings?.dinnerTime ?: "18:00"
                    snackTime = settings?.snackTime ?: "15:00"
                }
            isLoading = false
        } catch (e: Exception) {
            errorMessage = "Failed to load settings: ${e.message}"
            isLoading = false
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 600.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header
                Text(
                    text = "Notification Settings",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    // Enable notifications toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Enable Notifications",
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        )
                        Switch(
                            checked = notificationEnabled,
                            onCheckedChange = { notificationEnabled = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4F8BFF)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color.LightGray)
                    Spacer(modifier = Modifier.height(16.dp))

                    // Time settings (only show if notifications are enabled)
                    if (notificationEnabled) {
                        Text(
                            text = "Meal Reminder Times",
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Breakfast time
                        TimeSettingRow(
                            label = "Breakfast",
                            time = breakfastTime,
                            onClick = { showBreakfastPicker = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Lunch time
                        TimeSettingRow(
                            label = "Lunch",
                            time = lunchTime,
                            onClick = { showLunchPicker = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dinner time
                        TimeSettingRow(
                            label = "Dinner",
                            time = dinnerTime,
                            onClick = { showDinnerPicker = true }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Snack time
                        TimeSettingRow(
                            label = "Snack",
                            time = snackTime,
                            onClick = { showSnackPicker = true }
                        )
                    }

                    // Error message
                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Cancel button
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cancel")
                        }

                        // Save button
                        Button(
                            onClick = {
                                scope.launch {
                                    isSaving = true
                                    errorMessage = null
                                    try {
                                        val settings = NotificationSettings(
                                            notificationEnabled = notificationEnabled,
                                            breakfastTime = breakfastTime,
                                            lunchTime = lunchTime,
                                            dinnerTime = dinnerTime,
                                            snackTime = snackTime
                                        )
                                        val response = appService.updateNotificationSettings(settings)
                                        if (response.isSuccessful && response.body()?.success == true) {
                                            onDismiss()
                                        } else {
                                            errorMessage = "Failed to save settings"
                                        }
                                    } catch (e: Exception) {
                                        errorMessage = "Error: ${e.message}"
                                    }
                                    isSaving = false
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4F8BFF)
                            ),
                            enabled = !isSaving
                        ) {
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }
    }

    // Time pickers
    if (showBreakfastPicker) {
        TimePickerDialog(
            initialTime = breakfastTime,
            onDismiss = { showBreakfastPicker = false },
            onTimeSelected = {
                breakfastTime = it
                showBreakfastPicker = false
            }
        )
    }

    if (showLunchPicker) {
        TimePickerDialog(
            initialTime = lunchTime,
            onDismiss = { showLunchPicker = false },
            onTimeSelected = {
                lunchTime = it
                showLunchPicker = false
            }
        )
    }

    if (showDinnerPicker) {
        TimePickerDialog(
            initialTime = dinnerTime,
            onDismiss = { showDinnerPicker = false },
            onTimeSelected = {
                dinnerTime = it
                showDinnerPicker = false
            }
        )
    }

    if (showSnackPicker) {
        TimePickerDialog(
            initialTime = snackTime,
            onDismiss = { showSnackPicker = false },
            onTimeSelected = {
                snackTime = it
                showSnackPicker = false
            }
        )
    }
}

@Composable
fun TimeSettingRow(
    label: String,
    time: String,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = Color(0xFF1E3A8A)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.AccessTime,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(label, fontWeight = FontWeight.Medium)
            }
            Text(
                time,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4F8BFF)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    initialTime: String,
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit
) {
    val timeParts = initialTime.split(":")
    val initialHour = timeParts.getOrNull(0)?.toIntOrNull() ?: 12
    val initialMinute = timeParts.getOrNull(1)?.toIntOrNull() ?: 0

    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Select Time",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E3A8A)
                )

                Spacer(modifier = Modifier.height(16.dp))

                TimePicker(
                    state = timePickerState,
                    colors = TimePickerDefaults.colors(
                        clockDialColor = Color(0xFFF3F7FB),
                        selectorColor = Color(0xFF4F8BFF),
                        clockDialSelectedContentColor = Color.White,
                        clockDialUnselectedContentColor = Color(0xFF1E3A8A)
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val hour = timePickerState.hour.toString().padStart(2, '0')
                            val minute = timePickerState.minute.toString().padStart(2, '0')
                            onTimeSelected("$hour:$minute")
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4F8BFF)
                        )
                    ) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
