package com.example.alpvp.data.dto

data class FoodLogItem(
    val foodInLogs: List<FoodInLog>,
    val latitude: Double,
    val log_id: Int,
    val longitude: Double,
    val timestamp: Long,
    val user_id: Int
)