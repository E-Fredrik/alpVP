package com.example.alpvp.data.dto

data class FoodLogRequest(
    val foods: List<FoodInLogRequest>,
    val latitude: Double,
    val longitude: Double,
    val timestamp: Long,
    val user_id: Int
)