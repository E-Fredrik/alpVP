package com.example.alpvp.ui.model

data class FoodInLogModel(
    val logId: Int,
    val foodId: Int,
    val foodName: String,
    val quantity: Int,
    val calories: Int,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fats: Int = 0
)
