package com.example.alpvp.data.Repository

import com.example.alpvp.data.dto.*
import com.example.alpvp.data.Service.AppService



class ActivityLogRepository(private val appService: AppService) {
    
    suspend fun createActivityLog(request: ActivityLogRequest): ActivityLog {
        val response = appService.createActivityLog(request)
        if (response.isSuccessful && response.body()?.success == true) {
            return response.body()!!.data
        } else {
            throw Exception("Failed to create activity log: ${response.message()}")
        }
    }

    suspend fun bulkCreateActivityLogs(activities: List<ActivityLogRequest>): BulkResult {
        val response = appService.bulkCreateActivityLogs(BulkActivityLogRequest(activities))
        if (response.isSuccessful && response.body()?.success == true) {
            return response.body()!!.data
        } else {
            throw Exception("Failed to bulk create activity logs: ${response.message()}")
        }
    }

    suspend fun getUserActivityLogs(userId: Int): List<ActivityLog> {
        val response = appService.getUserActivityLogs(userId)
        if (response.isSuccessful) {
            // This endpoint returns Response<List<ActivityLog>> (no wrapper), so use the body directly
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to get activity logs: ${response.message()}")
        }
    }

    suspend fun getCurrentActivity(userId: Int): ActivityLog? {
        val response = appService.getCurrentActivity(userId)
        return if (response.isSuccessful && response.body()?.success == true) {
            response.body()!!.data
        } else {
            null
        }
    }
}
