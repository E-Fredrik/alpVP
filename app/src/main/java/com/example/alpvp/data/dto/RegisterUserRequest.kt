package com.example.alpvp.data.dto

data class RegisterUserRequest(
    val bmiGoal: Int,
    val email: String,
    val height: Int,
    val password: String,
    val username: String,
    val weight: Int
)