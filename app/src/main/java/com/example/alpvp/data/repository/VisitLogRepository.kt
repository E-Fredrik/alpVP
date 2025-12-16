package com.example.alpvp.data.repository

import com.example.alpvp.data.dto.*
import com.example.alpvp.data.Service.AppService

class VisitLogRepository(private val appService: AppService) {
    
    suspend fun createVisitLog(request: VisitLogRequest): VisitLog {
        val response = appService.createVisitLog(request)
        if (response.isSuccessful && response.body()?.success == true) {
            return response.body()!!.data
        } else {
            throw Exception("Failed to create visit log: ${response.message()}")
        }
    }

    suspend fun getUserVisitLogs(userId: Int): List<VisitLog> {
        val response = appService.getUserVisitLogs(userId)
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to get visit logs: ${response.message()}")
        }
    }
}
