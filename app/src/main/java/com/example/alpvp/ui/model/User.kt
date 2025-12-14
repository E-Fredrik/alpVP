package com.example.alpvp.ui.model

data class User(
    val userId: Int,
    val username: String,
    val email: String,
    val weight: Double,
    val height: Int,
    val bmi: Double,
    val bmiGoal: Double,
    val memberSince: String
)

