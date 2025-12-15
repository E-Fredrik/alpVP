package com.example.alpvp.data.dto

data class NotificationSettingsResponse(
    val success: Boolean,
    val data: NotificationSettings
)

data class NotificationSettings(
    val notificationEnabled: Boolean = true,
    val breakfastTime: String? = "08:00",
    val lunchTime: String? = "12:00",
    val dinnerTime: String? = "18:00",
    val snackTime: String? = "15:00"
)
