package com.example.alpvp.data.dto

data class VisitLogRequest(
    val user_id: Int,
    val place_id: Int,
    val entryTime: Long,
    val exitTime: Long,
    val durationMins: Int
)

data class VisitLogResponse(
    val success: Boolean,
    val data: VisitLog
)

data class VisitLog(
    val visit_id: Int,
    val user_id: Int,
    val place_id: Int,
    val entryTime: Long,
    val exitTime: Long,
    val durationMins: Int,
    val place: Place
)
