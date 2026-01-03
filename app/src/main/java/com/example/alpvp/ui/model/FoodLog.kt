package com.example.alpvp.ui.model

data class FoodLog(
    val foodName: String,
    val calories: Int,
    val timestamp: Long,
    val quantity: Int = 1
)