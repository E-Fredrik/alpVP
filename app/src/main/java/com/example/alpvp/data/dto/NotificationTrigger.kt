package com.example.alpvp.data.dto

data class LocationCheckRequest(
    val userId: Int,
    val latitude: Double,
    val longitude: Double
)

data class NotificationTriggersResponse(
    val success: Boolean,
    val data: NotificationTriggersData
)

data class NotificationTriggersData(
    val triggers: List<NotificationTrigger>,
    val metadata: NotificationMetadata
)

data class NotificationTrigger(
    val placeId: Int,
    val placeName: String,
    val category: String,
    val notificationType: String, // "FOOD_LOG", "ACTIVITY_LOG", "EMA_LOG", "VISIT_LOG"
    val message: String,
    val priority: String, // "HIGH", "MEDIUM", "LOW"
    val distance: Int,
    val shouldVibrate: Boolean,
    val icon: String
)

data class NotificationMetadata(
    val count: Int,
    val shouldNotify: Boolean,
    val userLocation: UserLocation,
    val timestamp: Long
)

data class UserLocation(
    val latitude: Double,
    val longitude: Double
)
