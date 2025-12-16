package com.example.alpvp.data.dto

data class ActivityLogRequest(
    val user_id: Int,
    val activityType: String, // "WALKING", "RUNNING", "CYCLING", "WEIGHTLIFTING", "YOGA"
    val startTime: Long,
    val endTime: Long,
    val confidence: Int
)

data class ActivityLogResponse(
    val success: Boolean,
    val data: ActivityLog
)

data class ActivityLog(
    val activity_id: Int,
    val user_id: Int,
    val activityType: String,
    val startTime: Long,
    val endTime: Long,
    val confidence: Int
)

data class BulkActivityLogRequest(
    val activities: List<ActivityLogRequest>
)

data class BulkActivityLogResponse(
    val success: Boolean,
    val data: BulkResult
)

data class BulkResult(
    val count: Int,
    val activities: List<ActivityLog>
)
