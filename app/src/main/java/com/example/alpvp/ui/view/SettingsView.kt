package com.example.alpvp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.sp
import com.example.alpvp.ui.viewModel.NotificationViewModel
import com.example.alpvp.ui.theme.*
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
                title = { 
                    Text(
                        "Notification Settings",
                        color = SurfaceWhite,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = SurfaceWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ElectricBlue,
                    navigationIconContentColor = SurfaceWhite
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight)
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (uiState.loading) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = ElectricBlue)
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Notification Toggle Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
                shape = RoundedCornerShape(16.dp)
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
                            fontWeight = FontWeight.Bold,
                            color = Gray900
                        )
                        Text(
                            "Receive reminders to log your meals",
                            style = MaterialTheme.typography.bodySmall,
                            color = Gray600
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
                        enabled = !uiState.loading,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SurfaceWhite,
                            checkedTrackColor = ElectricBlue,
                            uncheckedThumbColor = Gray400,
                            uncheckedTrackColor = Gray200
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Meal Times Section
            Text(
                "Meal Reminder Times",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Gray900
            )
            Text(
                "Set when you'd like to receive meal logging reminders",
                style = MaterialTheme.typography.bodySmall,
                color = Gray600
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
                        containerColor = SuccessGreenLight
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("✅", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            message,
                            color = SuccessGreen,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
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
                        containerColor = ErrorRedLight
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("❌", style = MaterialTheme.typography.titleLarge)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            error,
                            color = ErrorRed,
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
            .clickable(enabled = enabled) { showTimePicker = true },
        colors = CardDefaults.cardColors(
            containerColor = if (enabled) SurfaceWhite else Gray100
        ),
        shape = RoundedCornerShape(16.dp)
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
                    fontWeight = FontWeight.Medium,
                    color = if (enabled) Gray900 else Gray500
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    time,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (enabled) ElectricBlue else Gray400
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit time",
                    tint = if (enabled) ElectricBlue else Gray400,
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

@Composable
fun NumberPickerColumn(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    
    // Calculate the index for the current value
    val currentIndex = value - range.first
    
    // Auto-scroll to center the selected item when value changes
    LaunchedEffect(value) {
        listState.scrollToItem(
            index = currentIndex,
            scrollOffset = 0
        )
    }

    // Snap to nearest item when scrolling stops
    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            // Get the first visible item after scroll stops
            val firstVisibleIndex = listState.firstVisibleItemIndex
            val firstVisibleOffset = listState.firstVisibleItemScrollOffset
            
            // Calculate which item is closest to center
            val itemHeight = 50 // Each item is 50.dp
            
            val snapIndex = if (firstVisibleOffset > itemHeight / 2) {
                firstVisibleIndex + 1
            } else {
                firstVisibleIndex
            }
            
            // Snap to the calculated index
            val targetIndex = snapIndex.coerceIn(0, range.count() - 1)
            val targetValue = range.first + targetIndex
            
            if (targetValue != value) {
                onValueChange(targetValue)
            }
            
            // Smooth scroll to center the item
            listState.animateScrollToItem(targetIndex, scrollOffset = 0)
        }
    }

    Box(
        modifier = modifier
            .width(80.dp)
            .height(150.dp),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 50.dp),
            userScrollEnabled = true
        ) {
            items(range.count()) { index ->
                val number = range.first + index
                val isSelected = number == value
                
                TextButton(
                    onClick = { 
                        onValueChange(number)
                    },
                    modifier = Modifier.height(50.dp)
                ) {
                    Text(
                        text = "%02d".format(number),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) ElectricBlue else Gray500,
                        fontSize = if (isSelected) 28.sp else 20.sp
                    )
                }
            }
        }
        
        // Selection indicator - top line
        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 50.dp),
            thickness = 1.dp,
            color = Gray400
        )
        
        // Selection indicator - bottom line
        HorizontalDivider(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
            thickness = 1.dp,
            color = Gray400
        )
    }
}
