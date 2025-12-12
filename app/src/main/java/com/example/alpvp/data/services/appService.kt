package com.example.alpvp.data.services

import com.example.alpvp.data.model.*
import retrofit2.http.*

interface AppService {
    // User endpoints
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
}

// Helper data classes for requests
data class UserGoals(val bmiGoal: Float)
data class FriendRequest(val requesterId: Int, val addresseeId: Int)
data class FriendStatusUpdate(val status: FriendStatus)