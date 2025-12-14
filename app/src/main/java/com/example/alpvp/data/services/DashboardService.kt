package com.example.alpvp.data.services

import com.example.alpvp.data.dto.DashboardResponse
import com.example.alpvp.data.dto.UserProfileResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface DashboardService {
    @GET("private/profile")
    suspend fun getProfile(
        @Header("Authorization") authorization: String
    ): Response<UserProfileResponse>

    @GET("private/dashboard")
    suspend fun getDashboard(
        @Header("Authorization") authorization: String
    ): Response<DashboardResponse>
}
