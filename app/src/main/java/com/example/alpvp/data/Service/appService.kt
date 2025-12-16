package com.example.alpvp.data.Service

import com.example.alpvp.ui.model.*
import com.example.alpvp.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AppService {

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: Int): com.example.alpvp.ui.model.User

    @PUT("users/{userId}")
    suspend fun updateUser(@Path("userId") userId: Int, @Body user: com.example.alpvp.ui.model.User): com.example.alpvp.ui.model.User

    @PATCH("users/{userId}/goals")
    suspend fun updateUserGoals(
        @Path("userId") userId: Int,
        @Body goals: UserGoals
    ): com.example.alpvp.ui.model.User

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
    suspend fun getNotificationSettings(): Response<NotificationSettingsResponse>

    @PUT("api/notification-settings")
    suspend fun updateNotificationSettings(
        @Body settings: NotificationSettings
    ): Response<NotificationSettingsResponse>

    // Activity log
    @POST("api/activity-logs")
    suspend fun createActivityLog(
        @Body request: ActivityLogRequest
    ): Response<ActivityLogResponse>

    @POST("api/activity-logs/bulk")
    suspend fun bulkCreateActivityLogs(
        @Body request: BulkActivityLogRequest
    ): Response<BulkActivityLogResponse>

    @GET("api/activity-logs/user/{userId}")
    suspend fun getUserActivityLogs(
        @Path("userId") userId: Int
    ): Response<List<ActivityLog>>

    @GET("api/activity-logs/user/{userId}/current")
    suspend fun getCurrentActivity(
        @Path("userId") userId: Int
    ): Response<ActivityLogResponse>

    // Visit Log Endpoints
    @POST("api/visit-logs")
    suspend fun createVisitLog(
        @Body request: VisitLogRequest
    ): Response<VisitLogResponse>

    @GET("api/visit-logs/user/{userId}")
    suspend fun getUserVisitLogs(
        @Path("userId") userId: Int
    ): Response<List<VisitLog>>

    // EMA Log Endpoints
    @POST("api/ema-logs")
    suspend fun createEmaLog(
        @Body request: EmaLogRequest
    ): Response<EmaLogResponse>

    @GET("api/ema-logs/user/{userId}")
    suspend fun getUserEmaLogs(
        @Path("userId") userId: Int
    ): Response<List<EmaLog>>

    // Place Endpoints
    @GET("api/places")
    suspend fun getAllPlaces(): Response<PlacesResponse>

    @GET("api/places/nearby")
    suspend fun getNearbyPlaces(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radiusKm: Double = 1.0
    ): Response<PlacesResponse>

    @GET("api/places/category/{category}")
    suspend fun getPlacesByCategory(
        @Path("category") category: String
    ): Response<PlacesResponse>

    // Notification Endpoints
    @GET("api/notifications/location-check/{userId}")
    suspend fun checkLocationTriggers(
        @Path("userId") userId: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double
    ): Response<NotificationTriggersResponse>
}

// Helper data classes for requests
data class UserGoals(val bmiGoal: Float)
data class FriendRequest(val requesterId: Int, val addresseeId: Int)
data class FriendStatusUpdate(val status: FriendStatus)
