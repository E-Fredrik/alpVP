package com.example.alpvp.ui.model

data class FoodLogModel(
    val logId: Int,
    val userId: Int,
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val foodInLogs: List<FoodInLogItemModel>
)
