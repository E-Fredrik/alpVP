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
    val memberSince: String
)