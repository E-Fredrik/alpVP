package com.example.alpvp.ui.model

data class FriendFoodLogs(
    val friendId: Int,
    val friendName: String,
    val foodLogs: List<FriendFoodLogItem> = emptyList(),
    val totalCaloriesToday: Int = 0
)

data class FriendFoodLogItem(
    val logId: Int,
    val foodName: String,
    val calories: Int,
    val quantity: Int,
    val timestamp: Long
)