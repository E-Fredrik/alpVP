package com.example.alpvp.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Restaurant
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
import com.example.alpvp.data.dto.UserProfileData
import com.example.alpvp.data.dto.DashboardData
import com.example.alpvp.data.dto.RecentFoodLog
import com.example.alpvp.data.dto.FoodInRecentLog
import com.example.alpvp.data.dto.WeeklyProgressItem
import com.example.alpvp.data.dto.FriendFoodLog
import java.util.Calendar

@Composable
fun DashboardScreen(
    dashboardViewModel: DashboardViewModel,
    onOpenFood: () -> Unit = {}
) {
    val uiState by dashboardViewModel.uiState.collectAsState()

    DashboardScreenContent(
        uiState = uiState,
        onOpenFood = onOpenFood
    )
}

@Composable
private fun DashboardScreenContent(
    uiState: DashboardUiState,
    onOpenFood: () -> Unit = {}
) {
    // Local helpers
    fun getBMIStatus(): String {
        val profile = uiState.userProfile ?: return "BMI: --"
        val heightInMeters = profile.height / 100.0
        val weightInKg = profile.weight
        val currentBMI = weightInKg / (heightInMeters * heightInMeters)
        return "BMI: %.1f (Goal: %.1f)".format(currentBMI, profile.bmiGoal)
    }

    val todayCalories = uiState.dashboardData?.todayCalories ?: 0
    // Try to obtain a calorie goal from the server-side data if available. If not, leave null and
    // show consumed calories instead of a remaining amount.
    val caloriesGoal: Int? = uiState.dashboardData?.let { /* no calorie goal in DashboardData */ null }
    val caloriesRemaining = caloriesGoal?.let { it - todayCalories }
    // display text for remaining or overage; defined early so UI blocks can reference it
    val remainingText = caloriesRemaining?.let { if (it >= 0) "${it}" else "Over by ${-it}" } ?: "-"

    // helper for same-day check (works on lower API levels)
    fun isSameDay(ts: Long): Boolean {
        val now = Calendar.getInstance()
        val then = Calendar.getInstance()
        then.timeInMillis = ts
        return now.get(Calendar.YEAR) == then.get(Calendar.YEAR) &&
            now.get(Calendar.DAY_OF_YEAR) == then.get(Calendar.DAY_OF_YEAR)
    }

    // Convert recent food logs from API to UI model
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
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 32.dp, bottom = 24.dp)
        ) {
            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineLarge,
                color = Gray900
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = getBMIStatus(),
                style = MaterialTheme.typography.bodyMedium,
                color = Gray600
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
                    Text("Failed to load summary", color = MaterialTheme.colorScheme.error)
                } else {
                    CircularProgress(
                        current = todayCalories,
                        goal = caloriesGoal,
                        progressColor = ElectricBlue
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Two-column layout: left = Remaining calories, right = % of daily goal with progress bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Remaining calories — `remainingText` computed above
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = remainingText,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Gray900
                        )
                        Text(
                            text = "Remaining",
                            fontSize = 12.sp,
                            color = Gray500
                        )
                    }

                    // Right: Meals summary — more actionable than a redundant small chart
                    // Compute meals today, avg calories per meal, largest meal and last meal
                    val logs = uiState.userProfile?.recentFoodLogs ?: emptyList()
                    val todayLogs = logs.filter { log -> isSameDay(log.timestamp) }
                    val mealsToday = todayLogs.size

                    val mealCaloriesList = todayLogs.map { log -> log.foods.sumOf { it.calories } }
                    val totalCaloriesFromLogs = mealCaloriesList.sum()
                    val avgCaloriesPerMeal = if (mealsToday > 0) (totalCaloriesFromLogs / mealsToday) else 0

                    val largestMealIndex = mealCaloriesList.indices.maxByOrNull { mealCaloriesList[it] }
                    val largestMealText = largestMealIndex?.let { idx ->
                        val log = todayLogs[idx]
                        val name = log.foods.firstOrNull()?.foodName ?: "Meal"
                        "${name} • ${mealCaloriesList[idx]} cal"
                    } ?: "-"

                    val lastLog = todayLogs.maxByOrNull { it.timestamp } ?: logs.maxByOrNull { it.timestamp }
                    val lastMealText = lastLog?.let { log ->
                        val name = log.foods.firstOrNull()?.foodName ?: "Meal"
                        val minutes = ((System.currentTimeMillis() - log.timestamp) / 60000).toInt()
                        val timeAgo = when {
                            minutes < 60 -> "$minutes min ago"
                            minutes < 120 -> "1 hour ago"
                            else -> "${minutes / 60} hours ago"
                        }
                        "${name} • ${log.foods.sumOf { it.calories }} cal • $timeAgo"
                    } ?: "-"

                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.fillMaxWidth(0.6f)
                    ) {
                        // Actionable recommendation based on remaining calories
                        val recommendation = if (caloriesGoal != null && caloriesRemaining != null) {
                            when {
                                caloriesRemaining < 0 -> "You've exceeded your daily goal by ${-caloriesRemaining} cal. Consider a light walk or a lighter dinner."
                                caloriesRemaining <= 300 -> "You're close to your goal. Keep portions light for the next meal."
                                else -> "You're ${caloriesRemaining} cal under your goal — consider a balanced snack if needed."
                            }
                        } else {
                            // No calorie goal known — provide general consumption feedback
                            when {
                                todayCalories == 0 -> "No calories logged yet. Add your first meal to get started."
                                todayCalories < 800 -> "You've logged ${todayCalories} cal so far — keep balanced choices."
                                else -> "You've logged ${todayCalories} cal so far today."
                            }
                        }

                        Text(
                            text = recommendation,
                            fontSize = 12.sp,
                            color = Gray700,
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Meal timeline (today's meals) — small chips with time and calories
                        val timelineLogs = todayLogs.takeLast(4)
                        if (timelineLogs.isNotEmpty()) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                timelineLogs.forEach { log ->
                                    val minutes = ((System.currentTimeMillis() - log.timestamp) / 60000).toInt()
                                    val timeLabel = when {
                                        minutes < 60 -> "${minutes}m ago"
                                        minutes < 120 -> "1h ago"
                                        else -> "${minutes / 60}h ago"
                                    }
                                    val calSum = log.foods.sumOf { it.calories }
                                    Surface(
                                        shape = RoundedCornerShape(12.dp),
                                        color = BackgroundLight,
                                        tonalElevation = 0.dp
                                    ) {
                                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = timeLabel, fontSize = 12.sp, color = Gray700)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(text = "${calSum} cal", fontSize = 12.sp, color = Gray600)
                                        }
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        // Summary: meals count, avg cal, largest and last meal
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.End) {
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Meals: ${mealsToday}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Gray900
                                )
                                Text(
                                    text = "Avg: ${avgCaloriesPerMeal} cal",
                                    fontSize = 12.sp,
                                    color = Gray500,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Largest: $largestMealText",
                            fontSize = 12.sp,
                            color = Gray600,
                            modifier = Modifier.align(Alignment.End)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Last: $lastMealText",
                            fontSize = 12.sp,
                            color = Gray600,
                            modifier = Modifier.align(Alignment.End)
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

        // Daily Streak
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
                            text = "12 Day Streak",
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
