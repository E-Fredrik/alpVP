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
    val caloriesGoal = 2200
    val caloriesRemaining = caloriesGoal - todayCalories

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
