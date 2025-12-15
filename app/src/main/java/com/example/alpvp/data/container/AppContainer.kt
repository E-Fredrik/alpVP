package com.example.alpvp.data.container

import com.example.alpvp.data.repository.AuthRepository
import com.example.alpvp.data.repository.UserRepository
import com.example.alpvp.data.repository.FoodRepository
import com.example.alpvp.data.repository.DashboardRepository
import com.example.alpvp.data.repository.FoodLogRepository
import com.example.alpvp.data.repository.FriendRepository
import com.example.alpvp.data.repository.DailySummaryRepository
import com.example.alpvp.data.Service.FoodService
import com.example.alpvp.data.Service.UserService
import com.example.alpvp.data.services.DashboardService
import com.example.alpvp.data.services.AppService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
    companion object {
        val BASE_URL = "http://10.152.62.60:3000/api/"
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    // Authentication Service (for login/register)
    private val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(userService)
    }

    private val foodService: FoodService by lazy {
        retrofit.create(FoodService::class.java)
    }

    val foodRepository: FoodRepository by lazy {
        FoodRepository(foodService)
    }

    private val dashboardService: DashboardService by lazy {
        retrofit.create(DashboardService::class.java)
    }

    val dashboardRepository: DashboardRepository by lazy {
        DashboardRepository(dashboardService)
    }

    // General App Service (for user profile, friends, food logs, daily summary)
    private val appService: AppService by lazy {
        retrofit.create(AppService::class.java)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(appService)
    }

    val foodLogRepository: FoodLogRepository by lazy {
        FoodLogRepository(appService)
    }

    val friendRepository: FriendRepository by lazy {
        FriendRepository(appService)
    }

    val dailySummaryRepository: DailySummaryRepository by lazy {
        DailySummaryRepository(appService)
    }
}