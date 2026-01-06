package com.example.alpvp.data.container

import android.content.Context
import com.example.alpvp.data.Repository.AuthRepository
import com.example.alpvp.data.Repository.UserRepository
import com.example.alpvp.data.Repository.FoodRepository
import com.example.alpvp.data.Repository.DashboardRepository
import com.example.alpvp.data.Repository.FriendRepository
import com.example.alpvp.data.Repository.DailySummaryRepository
import com.example.alpvp.data.Repository.ActivityLogRepository
import com.example.alpvp.data.Repository.VisitLogRepository
import com.example.alpvp.data.Repository.EmaLogRepository
import com.example.alpvp.data.Repository.PlaceRepository
import com.example.alpvp.data.Service.FoodService
import com.example.alpvp.data.Service.UserService
import com.example.alpvp.data.Service.AppService
import com.example.alpvp.data.Service.DashboardService
import com.example.alpvp.data.Repository.UserPreferencesRepository
import com.example.alpvp.data.Service.AARService
import com.example.alpvp.notification.SmartNotificationManager
import com.example.alpvp.notification.NotificationScheduler
import com.example.alpvp.data.utils.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer(private val context: Context) {
    companion object {
        val BASE_URL = "https://nudge.up.railway.app/"
        //10.0.181.207
        //val BASE_URL = "http://10.152.62.60:3000/"
    }

    // Logging interceptor for debugging
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    
    // OkHttpClient with authentication interceptor
    private val authenticatedClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor(context))
        .addInterceptor(loggingInterceptor)
        .build()

    // Retrofit instance for authenticated requests (private API)
    private val authenticatedRetrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(authenticatedClient)
        .build()

    // Retrofit instance for public requests (no auth needed)
    private val publicRetrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .client(OkHttpClient.Builder().addInterceptor(loggingInterceptor).build())
        .build()
    private val userService: UserService by lazy {
        publicRetrofit.create(UserService::class.java)
    }

    // AuthRepository for login/register
    val authRepository: AuthRepository by lazy {
        AuthRepository(userService, context)
    }

    // Food Service
    private val foodService: FoodService by lazy {
        authenticatedRetrofit.create(FoodService::class.java)
    }

    val foodRepository: FoodRepository by lazy {
        FoodRepository(foodService)
    }

    // Dashboard Service
    private val dashboardService: DashboardService by lazy {
        authenticatedRetrofit.create(DashboardService::class.java)
    }

    val dashboardRepository: DashboardRepository by lazy {
        DashboardRepository(dashboardService)
    }

    // App Service (for user profile, friends, food logs, daily summary)
    val appService: AppService by lazy {
        authenticatedRetrofit.create(AppService::class.java)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(appService)
    }




    val friendRepository: FriendRepository by lazy {
        FriendRepository(appService)
    }

    val dailySummaryRepository: DailySummaryRepository by lazy {
        DailySummaryRepository(appService)
    }

    val activityLogRepository: ActivityLogRepository by lazy {
        ActivityLogRepository(appService)
    }

    val visitLogRepository: VisitLogRepository by lazy {
        VisitLogRepository(appService)
    }

    val emaLogRepository: EmaLogRepository by lazy {
        EmaLogRepository(appService)
    }

    val placeRepository: PlaceRepository by lazy {
        PlaceRepository(appService)
    }

    // ========================================================================
    // OTHER SERVICES
    // ========================================================================
    
    // UserPreferences for token storage
    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
    }

    // AAR Service for activity and attention recognition
    val aarService: AARService by lazy {
        AARService(context, appService)
    }

    // Smart Notification Manager - uses AAR for safe notification delivery
    val smartNotificationManager: SmartNotificationManager by lazy {
        SmartNotificationManager(context)
    }

    // Notification Scheduler - handles meal time notifications
    val notificationScheduler: NotificationScheduler by lazy {
        NotificationScheduler(context)
    }
}