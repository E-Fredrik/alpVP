package com.example.alpvp.data.services

import com.example.alpvp.ui.model.*
import retrofit2.http.*

interface AppService {

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: Int): User

    @PUT("users/{userId}")
    suspend fun updateUser(@Path("userId") userId: Int, @Body user: User): User

    @PATCH("users/{userId}/goals")
    suspend fun updateUserGoals(
        @Path("userId") userId: Int,
        @Body goals: UserGoals
    ): User

    // Food Log endpoints
    @POST("food-logs")
    suspend fun createFoodLog(@Body foodLog: FoodLog): FoodLog

    @GET("users/{userId}/food-logs")
    suspend fun getUserFoodLogs(@Path("userId") userId: Int): List<FoodLog>

    @GET("food-logs/nearby")
    suspend fun getFoodLogsByLocation(
        @Query("latitude") latitude: Float,
        @Query("longitude") longitude: Float,
        @Query("radius") radius: Float = 1000f
    ): List<FoodLog>

    // Daily Summary endpoints
    @GET("users/{userId}/daily-summary")
    suspend fun getDailySummary(
        @Path("userId") userId: Int,
        @Query("date") date: Long
    ): DailySummary

    // Friend endpoints
    @GET("users/{userId}/friends")
    suspend fun getUserFriends(@Path("userId") userId: Int): List<Friend>

    @GET("users/{userId}/friends/food-logs")
    suspend fun getFriendsFoodLogs(@Path("userId") userId: Int): List<FoodLog>

    @POST("friends/request")
    suspend fun sendFriendRequest(@Body request: FriendRequest): Friend

    @PATCH("friends/{friendId}/status")
    suspend fun updateFriendStatus(
        @Path("friendId") friendId: Int,
        @Body status: FriendStatusUpdate
    ): Friend
    
    // Notification Settings endpoints
    @GET("api/notification-settings")
    suspend fun getNotificationSettings(): retrofit2.Response<com.example.alpvp.data.dto.NotificationSettingsResponse>
    
    @PUT("api/notification-settings")
    suspend fun updateNotificationSettings(
        @Body settings: com.example.alpvp.data.dto.NotificationSettings
    ): retrofit2.Response<com.example.alpvp.data.dto.NotificationSettingsResponse>

    // ============================================================================
    // Activity Log Endpoints
    // ============================================================================
    @POST("api/activity-logs")
    suspend fun createActivityLog(
        @Body request: com.example.alpvp.data.dto.ActivityLogRequest
    ): retrofit2.Response<com.example.alpvp.data.dto.ActivityLogResponse>

    @POST("api/activity-logs/bulk")
    suspend fun bulkCreateActivityLogs(
        @Body request: com.example.alpvp.data.dto.BulkActivityLogRequest
    ): retrofit2.Response<com.example.alpvp.data.dto.BulkActivityLogResponse>

    @GET("api/activity-logs/user/{userId}")
    suspend fun getUserActivityLogs(
        @Path("userId") userId: Int
    ): retrofit2.Response<com.example.alpvp.data.dto.ApiResponse<List<com.example.alpvp.data.dto.ActivityLog>>>

    @GET("api/activity-logs/user/{userId}/current")
    suspend fun getCurrentActivity(
        @Path("userId") userId: Int
    ): retrofit2.Response<com.example.alpvp.data.dto.ActivityLogResponse>

    // ============================================================================
    // Visit Log Endpoints
    // ============================================================================
    @POST("api/visit-logs")
    suspend fun createVisitLog(
        @Body request: com.example.alpvp.data.dto.VisitLogRequest
    ): retrofit2.Response<com.example.alpvp.data.dto.VisitLogResponse>

    @GET("api/visit-logs/user/{userId}")
    suspend fun getUserVisitLogs(
        @Path("userId") userId: Int
    ): retrofit2.Response<com.example.alpvp.data.dto.ApiResponse<List<com.example.alpvp.data.dto.VisitLog>>>

    // ============================================================================
    // EMA Log Endpoints
    // ============================================================================
    @POST("api/ema-logs")
    suspend fun createEmaLog(
        @Body request: com.example.alpvp.data.dto.EmaLogRequest
    ): retrofit2.Response<com.example.alpvp.data.dto.EmaLogResponse>

    @GET("api/ema-logs/user/{userId}")
    suspend fun getUserEmaLogs(
        @Path("userId") userId: Int
    ): retrofit2.Response<com.example.alpvp.data.dto.ApiResponse<List<com.example.alpvp.data.dto.EmaLog>>>

    // ============================================================================
    // Place Endpoints
    // ============================================================================
    @GET("api/places")
    suspend fun getAllPlaces(): retrofit2.Response<com.example.alpvp.data.dto.PlacesResponse>

    @GET("api/places/nearby")
    suspend fun getNearbyPlaces(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radiusKm: Double = 1.0
    ): retrofit2.Response<com.example.alpvp.data.dto.PlacesResponse>

    @GET("api/places/category/{category}")
    suspend fun getPlacesByCategory(
        @Path("category") category: String
    ): retrofit2.Response<com.example.alpvp.data.dto.PlacesResponse>

    // ============================================================================
    // Notification Endpoints
    // ============================================================================
    @GET("api/notifications/location-check/{userId}")
    suspend fun checkLocationTriggers(
        @Path("userId") userId: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): retrofit2.Response<com.example.alpvp.data.dto.NotificationTriggersResponse>
}

// Helper data classes for requests
data class UserGoals(val bmiGoal: Float)
data class FriendRequest(val requesterId: Int, val addresseeId: Int)
data class FriendStatusUpdate(val status: FriendStatus)