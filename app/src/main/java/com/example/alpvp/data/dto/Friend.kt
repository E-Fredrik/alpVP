package com.example.alpvp.data.dto

data class FriendActivity(
    val friendId: Int,
    val friendName: String,
    val activityType: String,
    val timestamp: Long
)

data class FriendFoodLog(
    val friendId: Int,
    val friendName: String,
    val foodName: String,
    val calories: Int,
    val quantity: Int,
    val timestamp: Long
)