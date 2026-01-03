package com.example.alpvp.data.Service

import com.example.alpvp.ui.model.*
import com.example.alpvp.data.dto.*
import retrofit2.Response
import retrofit2.http.*

interface AppService {

    @GET("users/{userId}")
    suspend fun getUser(@Path("userId") userId: Int): com.example.alpvp.ui.model.User

    @PUT("users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: Int,
        @Body user: com.example.alpvp.ui.model.User
    ): com.example.alpvp.ui.model.User

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