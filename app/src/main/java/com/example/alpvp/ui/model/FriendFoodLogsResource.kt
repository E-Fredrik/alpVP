package com.example.alpvp.ui.model

data class FriendFoodLogsResource(
    val friendFoodLogs: List<FriendFoodLogs> = emptyList(),
    val lastUpdated: Long = System.currentTimeMillis()
)
