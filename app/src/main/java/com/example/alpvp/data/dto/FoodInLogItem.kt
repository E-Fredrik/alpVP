package com.example.alpvp.data.dto

data class FoodInLogItem(
    val calories: Int,
    val food: Food,
    val food_id: Int,
    val id: Int,
    val log: Log,
    val log_id: Int,
    val quantity: Int
)