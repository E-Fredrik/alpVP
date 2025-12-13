package com.example.alpvp.data.dto

data class FoodInLogRequest(
    val calories: Int,
    val food_id: Int,
    val log_id: Int,
    val quantity: Int
)