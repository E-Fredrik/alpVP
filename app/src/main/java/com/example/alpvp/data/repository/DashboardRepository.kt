package com.example.alpvp.data.repository

import com.example.alpvp.data.dto.DashboardData
import com.example.alpvp.data.dto.UserProfileData
import com.example.alpvp.data.services.DashboardService

class DashboardRepository(private val dashboardService: DashboardService) {
    
    suspend fun getUserProfile(token: String): Result<UserProfileData> {
        return try {
            val response = dashboardService.getProfile("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to fetch profile: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDashboardData(token: String): Result<DashboardData> {
        return try {
            val response = dashboardService.getDashboard("Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to fetch dashboard: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
