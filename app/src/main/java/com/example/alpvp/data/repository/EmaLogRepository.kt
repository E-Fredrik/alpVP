package com.example.alpvp.data.Repository

import com.example.alpvp.data.dto.*
import com.example.alpvp.data.Service.AppService

class EmaLogRepository(private val appService: AppService) {
    
    suspend fun createEmaLog(request: EmaLogRequest): EmaLog {
        val response = appService.createEmaLog(request)
        if (response.isSuccessful && response.body()?.success == true) {
            return response.body()!!.data
        } else {
            throw Exception("Failed to create EMA log: ${response.message()}")
        }
    }

    suspend fun getUserEmaLogs(userId: Int): List<EmaLog> {
        val response = appService.getUserEmaLogs(userId)
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Failed to get EMA logs: ${response.message()}")
        }
    }
}
