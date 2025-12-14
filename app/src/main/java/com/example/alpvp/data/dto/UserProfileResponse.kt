package com.example.alpvp.data.dto

data class UserProfileResponse(
    val data: UserProfileData
)

data class UserProfileData(
    val userId: Int,
    val username: String,
    val email: String,
    val weight: Double,
    val height: Int,
    val bmi: Double,
    val bmiGoal: Double,
    val memberSince: String,
    val recentFoodLogs: List<RecentFoodLog>
)

data class RecentFoodLog(
    val logId: Int,
    val timestamp: Long,
    val foods: List<FoodInRecentLog>
)

data class FoodInRecentLog(
    val foodName: String,
    val calories: Int,
    val quantity: Int
)
