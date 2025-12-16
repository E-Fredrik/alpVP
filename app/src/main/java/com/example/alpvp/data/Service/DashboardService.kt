package com.example.alpvp.data.services

import com.example.alpvp.data.dto.DashboardResponse
import com.example.alpvp.data.dto.UserProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DashboardService {
    @GET("api/profile")
    suspend fun getProfile(): Response<UserProfileResponse>

    @GET("api/dashboard")
    suspend fun getDashboard(): Response<DashboardResponse>
}