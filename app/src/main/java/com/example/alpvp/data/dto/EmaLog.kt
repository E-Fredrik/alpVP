package com.example.alpvp.data.dto

data class EmaLogRequest(
    val user_id: Int,
    val moodScore: Int, // 1-10
    val context: String?,
    val timestamp: Long,
    val latitude: Double?,
    val longitude: Double?,
    val geofenceRadius: Int?
)

data class EmaLogResponse(
    val success: Boolean,
    val data: EmaLog
)

data class EmaLog(
    val ema_id: Int,
    val user_id: Int,
    val moodScore: Int,
    val context: String?,
    val timestamp: Long,
    val latitude: Double?,
    val longitude: Double?,
    val geofenceRadius: Int?
)
