package com.example.alpvp.data.repository

import android.util.Log
import com.example.alpvp.data.dto.DashboardData
import com.example.alpvp.data.dto.UserProfileData
import com.example.alpvp.data.Service.DashboardService

class DashboardRepository(private val dashboardService: DashboardService) {
    
    suspend fun getUserProfile(token: String): Result<UserProfileData> {
        return try {
            Log.d("DashboardRepository", "Fetching user profile...")
            
            // AuthInterceptor automatically adds Bearer token
            val response = dashboardService.getProfile()
            
            Log.d("DashboardRepository", "Profile response code: ${response.code()}")
            
            if (response.isSuccessful && response.body() != null) {
                Log.d("DashboardRepository", "Profile fetched successfully")
                Result.success(response.body()!!.data)
            } else {
                val error = "Failed to fetch profile: ${response.code()} - ${response.message()}"
                Log.e("DashboardRepository", error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e("DashboardRepository", "Exception fetching profile", e)
            Result.failure(e)
        }
    }

    suspend fun getDashboardData(token: String): Result<DashboardData> {
        return try {
            Log.d("DashboardRepository", "Fetching dashboard data...")
            
            val response = dashboardService.getDashboard()
            
            Log.d("DashboardRepository", "Dashboard response code: ${response.code()}")
            
            if (response.isSuccessful && response.body() != null) {
                Log.d("DashboardRepository", "Dashboard data fetched successfully")
                Result.success(response.body()!!.data)
            } else {
                val error = "Failed to fetch dashboard: ${response.code()} - ${response.message()}"
                Log.e("DashboardRepository", error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            Log.e("DashboardRepository", "Exception fetching dashboard", e)
            Result.failure(e)
        }
    }
}
