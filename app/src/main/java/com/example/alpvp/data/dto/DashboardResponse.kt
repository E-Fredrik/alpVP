package com.example.alpvp.data.dto

data class DashboardResponse(
    val data: DashboardData
)

data class DashboardData(
    val todayCalories: Int,
    val todayActivities: Int,
    val todayVisits: Int,
    val weeklyProgress: List<WeeklyProgressItem>,
    val recentFriendActivities: List<FriendActivity>,
    val recentFriendFoodLogs: List<FriendFoodLog>
)

data class WeeklyProgressItem(
    val date: String,
    val calories: Int,
    val activities: Int
)

data class FriendActivity(
    val friendId: Int,
    val friendName: String,
    val activityType: String,
    val timestamp: Long
)

data class FriendFoodLog(
    val friendId: Int,
    val friendName: String,
    val foodName: String,
    val calories: Int,
    val quantity: Int,
    val timestamp: Long
)
