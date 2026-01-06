package com.example.alpvp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.example.alpvp.ui.components.CircularProgress
import com.example.alpvp.ui.theme.*
import com.example.alpvp.ui.model.FoodLog
import com.example.alpvp.ui.viewModel.DashboardViewModel
import com.example.alpvp.ui.viewModel.DashboardUiState
import com.example.alpvp.ui.viewModel.AARViewModel
import com.example.alpvp.data.dto.UserProfileData
import com.example.alpvp.data.dto.DashboardData
import com.example.alpvp.data.dto.RecentFoodLog
import com.example.alpvp.data.dto.FoodInRecentLog
import com.example.alpvp.data.dto.WeeklyProgressItem
import com.example.alpvp.data.dto.FriendFoodLog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel,
    aarViewModel: AARViewModel,
    onOpenFood: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val uiState by dashboardViewModel.uiState.collectAsState()

    
    LaunchedEffect(uiState.userProfile?.userId) {
        uiState.userProfile?.userId?.let { userId ->
            aarViewModel.startMonitoring(userId)
        }
    }

    
    DisposableEffect(Unit) {
        onDispose {
            aarViewModel.stopMonitoring()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Dashboard",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Gray900
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundLight
                )
            )
        }
    ) { padding ->
        DashboardScreenContent(
            uiState = uiState,
            onOpenFood = onOpenFood,
            paddingValues = padding
        )
    }
}


private fun calculateStreak(foodLogs: List<RecentFoodLog>): Int {
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

@Composable
private fun DashboardScreenContent(
    uiState: DashboardUiState,
    onOpenFood: () -> Unit = {},
    paddingValues: PaddingValues = PaddingValues(0.dp)
) {
    
    fun getBMIStatus(): String {
        val profile = uiState.userProfile ?: return "BMI: --"
        val heightInMeters = profile.height / 100.0
        val weightInKg = profile.weight
        val currentBMI = weightInKg / (heightInMeters * heightInMeters)
        return "BMI: %.1f (Goal: %.1f)".format(currentBMI, profile.bmiGoal)
    }

    // Use dailySummary if available (more accurate), otherwise fallback to dashboardData
    val todayCalories = uiState.dailySummary?.totalCaloriesIn ?: uiState.dashboardData?.todayCalories ?: 0

    // Calculate calorie goal based on user's BMI goal
    val caloriesGoal = uiState.userProfile?.let { profile ->
        val heightInMeters = profile.height / 100.0
        val currentBMI = profile.weight / (heightInMeters * heightInMeters)

        // Calculate target weight based on BMI goal
        val targetWeight = profile.bmiGoal * heightInMeters * heightInMeters

        // Use Mifflin-St Jeor Equation to calculate BMR (assuming average age 30, male)
        // BMR = 10 * weight(kg) + 6.25 * height(cm) - 5 * age + 5 (for males)
        // For simplicity, we'll use target weight and assume sedentary lifestyle
        val bmr = 10 * targetWeight + 6.25 * profile.height - 5 * 30 + 5

        // TDEE = BMR * activity factor (1.2 for sedentary)
        val tdee = bmr * 1.2

        // If current BMI > goal BMI, create deficit; if current BMI < goal BMI, create surplus
        val adjustment = when {
            currentBMI > profile.bmiGoal -> -500 // 500 calorie deficit for weight loss
            currentBMI < profile.bmiGoal -> 300  // 300 calorie surplus for weight gain
            else -> 0 // Maintenance
        }

        (tdee + adjustment).toInt()
    } ?: 2200 // Default if no profile data

    val caloriesRemaining = caloriesGoal - todayCalories

    
    val recentFoodLogs = uiState.userProfile?.recentFoodLogs?.flatMap { log ->
        log.foods.map { food ->
            FoodLog(
                foodName = food.foodName,
                calories = food.calories,
                timestamp = log.timestamp,
                quantity = food.quantity
            )
        }
    } ?: emptyList()

    val lastMeal = recentFoodLogs.maxByOrNull { it.timestamp }

    fun getTimeAgo(timestamp: Long): String {
        val minutes = ((System.currentTimeMillis() - timestamp) / 60000).toInt()
        return when {
            minutes < 60 -> "$minutes min ago"
            minutes < 120 -> "1 hour ago"
            else -> "${minutes / 60} hours ago"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight)
            .padding(paddingValues)
            .verticalScroll(rememberScrollState())
    ) {
        // BMI Status
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            Text(
                text = getBMIStatus(),
                style = MaterialTheme.typography.bodyLarge,
                color = Gray600,
                fontWeight = FontWeight.Medium
            )
        }

        // Calorie Progress Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp),
            shape = RoundedCornerShape(24.dp),
            color = SurfaceWhite,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Today's Calories",
                    style = MaterialTheme.typography.titleLarge,
                    color = Gray900
                )
                Spacer(modifier = Modifier.height(24.dp))

                if (uiState.loading) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Loading summary...", color = Gray600)
                } else if (uiState.error != null) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Failed to load summary",
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            uiState.error ?: "Unknown error",
                            color = Gray600,
                            fontSize = 12.sp
                        )
                    }
                } else {
                    CircularProgress(
                        current = todayCalories,
                        goal = caloriesGoal,
                        progressColor = ElectricBlue
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$todayCalories",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gray900
                        )
                        Text(
                            text = "Eaten",
                            fontSize = 12.sp,
                            color = Gray500
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$caloriesRemaining",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gray900
                        )
                        Text(
                            text = "Remaining",
                            fontSize = 12.sp,
                            color = Gray500
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$caloriesGoal",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gray900
                        )
                        Text(
                            text = "Goal",
                            fontSize = 12.sp,
                            color = Gray500
                        )
                    }
                }
            }
        }

        // Last Meal Card
        lastMeal?.let { meal ->
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(24.dp),
                color = SurfaceWhite,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(BackgroundLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "🍽️", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Last meal",
                            fontSize = 12.sp,
                            color = Gray500
                        )
                        Text(
                            text = meal.foodName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Gray900
                        )
                        Text(
                            text = getTimeAgo(meal.timestamp),
                            fontSize = 12.sp,
                            color = Gray500
                        )
                    }
                    Text(
                        text = "${meal.calories} cal",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = CoralRed
                    )
                }
            }
        }

        // Quick Action Button
        Button(
            onClick = onOpenFood,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CoralRed
            )
        ) {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Log Food", fontSize = 16.sp)
        }

        // Daily Streak - Calculate from actual data
        val streakDays = calculateStreak(uiState.userProfile?.recentFoodLogs ?: emptyList())
        if (streakDays > 0) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp),
                shape = RoundedCornerShape(24.dp),
                color = SurfaceWhite,
                shadowElevation = 1.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🔥", fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = "$streakDays Day Streak",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Gray900
                            )
                            Text(
                                text = "Keep it up!",
                                fontSize = 14.sp,
                                color = Gray600
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
    AlpVPTheme {
        // Create mock data
        val mockUserProfile = UserProfileData(
            userId = 1,
            username = "John Doe",
            email = "john@example.com",
            weight = 75.0,
            height = 175,
            bmi = 24.5,
            bmiGoal = 23.0,
            memberSince = "2024-01-15",
            recentFoodLogs = listOf(
                RecentFoodLog(
                    logId = 1,
                    timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                    foods = listOf(
                        FoodInRecentLog(
                            foodName = "Grilled Chicken Salad",
                            calories = 350,
                            quantity = 1
                        ),
                        FoodInRecentLog(
                            foodName = "Apple",
                            calories = 95,
                            quantity = 1
                        )
                    )
                ),
                RecentFoodLog(
                    logId = 2,
                    timestamp = System.currentTimeMillis() - 7200000, // 2 hours ago
                    foods = listOf(
                        FoodInRecentLog(
                            foodName = "Protein Shake",
                            calories = 200,
                            quantity = 1
                        )
                    )
                )
            )
        )

        val mockDashboardData = DashboardData(
            todayCalories = 1450,
            todayActivities = 3,
            todayVisits = 5,
            weeklyProgress = listOf(
                WeeklyProgressItem("2024-12-08", 1800, 2),
                WeeklyProgressItem("2024-12-09", 2100, 3),
                WeeklyProgressItem("2024-12-10", 1950, 2),
                WeeklyProgressItem("2024-12-11", 2200, 4),
                WeeklyProgressItem("2024-12-12", 1850, 3),
                WeeklyProgressItem("2024-12-13", 2050, 3),
                WeeklyProgressItem("2024-12-14", 1450, 3)
            ),
            recentFriendActivities = listOf(),
            recentFriendFoodLogs = listOf(
                FriendFoodLog(
                    friendId = 2,
                    friendName = "Sarah Smith",
                    foodName = "Avocado Toast",
                    calories = 320,
                    quantity = 1,
                    timestamp = System.currentTimeMillis() - 1800000
                )
            )
        )

        val mockUiState = DashboardUiState(
            loading = false,
            userProfile = mockUserProfile,
            dashboardData = mockDashboardData,
            error = null
        )

        DashboardScreenContent(
            uiState = mockUiState,
            onOpenFood = {}
        )
    }
}
