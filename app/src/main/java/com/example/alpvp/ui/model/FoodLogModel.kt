package com.example.alpvp.ui.model

data class FoodLogModel(
    val logId: Int,
    val userId: Int,
    val timestamp: Long,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val foodInLogs: List<FoodInLogModel> = emptyList(),
    val totalCalories: Int = 0,
    val totalProtein: Int = 0,
    val totalCarbs: Int = 0,
    val totalFats: Int = 0
)
