package com.example.alpvp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.alpvp.data.Service.AppService
import com.example.alpvp.ui.components.ContextDetectionDialog
import com.example.alpvp.ui.theme.*
import com.example.alpvp.ui.viewModel.AuthViewModel
import com.example.alpvp.ui.viewModel.NotificationViewModel
import com.example.alpvp.ui.viewModel.DashboardViewModel
import com.example.alpvp.ui.viewModel.FoodViewModel
import com.example.alpvp.data.dto.NotificationSettings
import com.example.alpvp.worker.LocationCheckScheduler
import kotlinx.coroutines.delay
import AddFoodDialog
import androidx.compose.foundation.clickable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    dashboardViewModel: DashboardViewModel,
    foodViewModel: FoodViewModel,
    appService: AppService,
    onNavigateToSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Get NotificationViewModel (AndroidViewModel provided by default factory)
    val notificationViewModel: NotificationViewModel = viewModel<NotificationViewModel>()
    
    // Capture initial values outside of composition to avoid reading StateFlow.value directly in composable
    val authUiState by authViewModel.uiState.collectAsState()
    val dashboardUiState by dashboardViewModel.uiState.collectAsState()
    val bg = Brush.verticalGradient(listOf(BackgroundLight, BackgroundLightAlt))
    
    // Dialog states
    var showContextDialog by remember { mutableStateOf(false) }
    var showFoodDialog by remember { mutableStateOf(false) }
    var detectedRestaurant by remember { mutableStateOf("") }

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
                title = { 
                    Text(
                        "Profile", 
                        fontWeight = FontWeight.Bold,
                        color = SurfaceWhite
                    ) 
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ElectricBlue,
                    titleContentColor = SurfaceWhite
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
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Profile icon
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .background(ElectricBlue, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = SurfaceWhite,
                        modifier = Modifier.size(48.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // User info card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(4.dp, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        if (dashboardUiState.loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                color = ElectricBlue
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Loading profile...", color = Gray600)
                        } else {
                            Text(
                                text = userProfile?.username ?: "User",
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Gray900
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = userProfile?.email ?: "",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Gray600
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            HorizontalDivider(color = Gray200)

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
                                        fontSize = 20.sp,
                                        color = Gray900
                                    )
                                    Text("Height (cm)", color = Gray600, fontSize = 12.sp)
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "%.1f".format(userProfile?.weight ?: 0.0),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = Gray900
                                    )
                                    Text("Weight (kg)", color = Gray600, fontSize = 12.sp)
                                }

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "%.1f".format(userProfile?.bmiGoal ?: 0.0),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = Gray900
                                    )
                                    Text("BMI Goal", color = Gray600, fontSize = 12.sp)
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
                                        color = ElectricBlue
                                    )
                                    Text("Current BMI", color = Gray600, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Quick Stats Card
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📊",
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Your Progress",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Gray900
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "${userProfile?.recentFoodLogs?.size ?: 0}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = ElectricBlue
                                )
                                Text("Total Logs", color = Gray600, fontSize = 12.sp)
                            }
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val streak = calculateStreak(userProfile?.recentFoodLogs ?: emptyList())
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "$streak",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = ElectricBlue
                                    )
                                    if (streak > 0) {
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("🔥", fontSize = 16.sp)
                                    }
                                }
                                Text("Day Streak", color = Gray600, fontSize = 12.sp)
                            }
                            
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                val progress = userProfile?.let { profile ->
                                    val currentBMI = profile.weight / ((profile.height / 100.0) * (profile.height / 100.0))
                                    val diff = kotlin.math.abs(currentBMI - profile.bmiGoal)
                                    if (diff < 0.5) "✅" else "📈"
                                } ?: "📈"
                                Text(
                                    text = progress,
                                    fontSize = 20.sp
                                )
                                Text("Goal Status", color = Gray600, fontSize = 12.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Edit Profile Button (Outlined)
                OutlinedButton(
                    onClick = { /* TODO: Navigate to edit profile */ },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ElectricBlue
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, ElectricBlue)
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null, tint = ElectricBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Edit Profile", fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Settings Section
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Gray600,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                // Settings Options
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        MenuOption(
                            icon = Icons.Default.Notifications,
                            title = "Notification Settings",
                            subtitle = "Manage meal reminders",
                            onClick = onNavigateToSettings
                        )
                        HorizontalDivider(color = Gray200, modifier = Modifier.padding(horizontal = 16.dp))
                        MenuOption(
                            icon = Icons.Default.FitnessCenter,
                            title = "Update Goals",
                            subtitle = "Change BMI and calorie targets",
                            onClick = { /* TODO: Navigate to goals */ }
                        )
                        HorizontalDivider(color = Gray200, modifier = Modifier.padding(horizontal = 16.dp))
                        MenuOption(
                            icon = Icons.Default.Settings,
                            title = "App Preferences",
                            subtitle = "Theme, units, language",
                            onClick = { /* TODO: Navigate to preferences */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Support Section
                Text(
                    text = "Support",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Gray600,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                ) {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        MenuOption(
                            icon = Icons.Default.Help,
                            title = "Help & FAQ",
                            subtitle = "Get answers to common questions",
                            onClick = { /* TODO: Navigate to help */ }
                        )
                        HorizontalDivider(color = Gray200, modifier = Modifier.padding(horizontal = 16.dp))
                        MenuOption(
                            icon = Icons.Default.Info,
                            title = "About",
                            subtitle = "Version 1.0.0 • Terms & Privacy",
                            onClick = { /* TODO: Navigate to about */ }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Test Restaurant Detection button (for development)
                val context = LocalContext.current
                OutlinedButton(
                    onClick = { 
                        detectedRestaurant = "Joe's Pizza"
                        showContextDialog = true
                    },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = ElectricBlue
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
                ) {
                    Text("🧪 Test Restaurant Detection", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Logout button
                Button(
                    onClick = { 
                        LocationCheckScheduler.stopLocationChecks(context)
                        authViewModel.logout()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ErrorRed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Filled.ExitToApp, contentDescription = null, tint = SurfaceWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", color = SurfaceWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        // Context Detection Dialog
        if (showContextDialog) {
            ContextDetectionDialog(
                restaurantName = detectedRestaurant,
                duration = "20 minutes",
                onLogMeal = {
                    showContextDialog = false
                    showFoodDialog = true
                },
                onDismiss = {
                    showContextDialog = false
                }
            )
        }
        
        // Food Log Dialog
        if (showFoodDialog) {
            AddFoodDialog(
                foodViewModel = foodViewModel,
                onDismiss = {
                    showFoodDialog = false
                }
            )
        }
    }
}

// Helper Composable for Menu Options
@Composable
private fun MenuOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(ElectricBlue.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = ElectricBlue,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = Gray900
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Gray600
            )
        }
        Icon(
            Icons.Default.ChevronRight,
            contentDescription = null,
            tint = Gray400,
            modifier = Modifier.size(20.dp)
        )
    }
}

// Calculate streak from food logs
private fun calculateStreak(foodLogs: List<com.example.alpvp.data.dto.RecentFoodLog>): Int {
    if (foodLogs.isEmpty()) return 0

    // Get unique days with logs (sorted descending)
    val daysWithLogs = foodLogs
        .map { log ->
            val calendar = java.util.Calendar.getInstance()
            calendar.timeInMillis = log.timestamp
            calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
            calendar.set(java.util.Calendar.MINUTE, 0)
            calendar.set(java.util.Calendar.SECOND, 0)
            calendar.set(java.util.Calendar.MILLISECOND, 0)
            calendar.timeInMillis
        }
        .distinct()
        .sortedDescending()

    if (daysWithLogs.isEmpty()) return 0

    val today = java.util.Calendar.getInstance()
    today.set(java.util.Calendar.HOUR_OF_DAY, 0)
    today.set(java.util.Calendar.MINUTE, 0)
    today.set(java.util.Calendar.SECOND, 0)
    today.set(java.util.Calendar.MILLISECOND, 0)
    val todayTimestamp = today.timeInMillis

    val mostRecentDay = daysWithLogs.first()
    val daysDiff = ((todayTimestamp - mostRecentDay) / (24 * 60 * 60 * 1000)).toInt()

    if (daysDiff > 1) return 0 // Streak broken

    var streak = 0
    var expectedDay = todayTimestamp

    for (day in daysWithLogs) {
        if (day == expectedDay || day == expectedDay - (24 * 60 * 60 * 1000)) {
            streak++
            expectedDay = day - (24 * 60 * 60 * 1000)
        } else {
            break
        }
    }

    return streak
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsDialog(
    notificationViewModel: NotificationViewModel,
    onDismiss: () -> Unit
) {
    val scrollState = rememberScrollState()
    val uiState by notificationViewModel.uiState.collectAsState()

    // Local state for time settings (synced with ViewModel)
    var notificationEnabled by remember { mutableStateOf(true) }
    var breakfastTime by remember { mutableStateOf("08:00") }
    var lunchTime by remember { mutableStateOf("12:00") }
    var dinnerTime by remember { mutableStateOf("18:00") }
    var snackTime by remember { mutableStateOf("15:00") }

    // Time picker states
    var showBreakfastPicker by remember { mutableStateOf(false) }
    var showLunchPicker by remember { mutableStateOf(false) }
    var showDinnerPicker by remember { mutableStateOf(false) }
    var showSnackPicker by remember { mutableStateOf(false) }

    // Sync local state with ViewModel when settings are loaded
    LaunchedEffect(uiState.settings) {
        uiState.settings?.let { settings ->
            notificationEnabled = settings.notificationEnabled
            breakfastTime = settings.breakfastTime ?: "08:00"
            lunchTime = settings.lunchTime ?: "12:00"
            dinnerTime = settings.dinnerTime ?: "18:00"
            snackTime = settings.snackTime ?: "15:00"
        }
    }

    // Handle success message - auto dismiss after showing
    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            delay(1000)
            notificationViewModel.clearSuccessMessage()
            onDismiss()
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

                if (uiState.loading) {
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
                    HorizontalDivider(color = Color.LightGray)
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
                    if (uiState.error != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.error ?: "",
                            color = Color.Red,
                            fontSize = 12.sp
                        )
                    }
                    
                    // Success message
                    if (uiState.successMessage != null) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = uiState.successMessage ?: "",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
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
                                val settings = NotificationSettings(
                                    notificationEnabled = notificationEnabled,
                                    breakfastTime = breakfastTime,
                                    lunchTime = lunchTime,
                                    dinnerTime = dinnerTime,
                                    snackTime = snackTime
                                )
                                notificationViewModel.updateSettings(settings)
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4F8BFF)
                            ),
                            enabled = !uiState.loading && uiState.settings != null
                        ) {
                            if (uiState.loading) {
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
