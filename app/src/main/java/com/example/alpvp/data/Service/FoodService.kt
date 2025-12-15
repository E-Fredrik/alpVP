// kotlin
package com.example.alpvp.data.Service

import com.example.alpvp.data.dto.FoodInLogRequest
import com.example.alpvp.data.dto.FoodInLogResponse
import com.example.alpvp.data.dto.FoodLogRequest
import com.example.alpvp.data.dto.FoodLogResponse
import com.example.alpvp.data.dto.FoodLogsResponse
import com.example.alpvp.data.dto.FoodResponse
import com.example.alpvp.data.dto.FoodItem
import com.example.alpvp.data.dto.FoodListResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FoodService {
    // Create food - requires authorization
    @POST("api/foods")
    suspend fun createFood(
        @Header("Authorization") authorization: String,
        @Body food: FoodItem
    ): Response<FoodResponse>

    // Get food by ID - public endpoint
    @GET("api/foods/{id}")
    suspend fun getFood(
        @Path("id") id: Int
    ): Response<FoodResponse>

    // Search food by name - public endpoint
    @GET("api/foods/name/{name}")
    suspend fun getFoodByName(
        @Path("name") name: String
    ): Response<FoodListResponse>

    // Requires Authorization header: pass "Bearer <token>"
    @POST("api/food-in-logs")
    suspend fun createFoodInLog(
        @Header("Authorization") authorization: String,
        @Body request: FoodInLogRequest
    ): Response<FoodInLogResponse>

    @POST("api/food-logs")
    suspend fun createFoodLog(
        @Header("Authorization") authorization: String,
        @Body request: FoodLogRequest
    ): Response<FoodLogResponse>

    @GET("api/food-logs/{id}")
    suspend fun getFoodLog(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int
    ): Response<FoodLogResponse>

    @GET("api/food-logs/user/{user_id}")
    suspend fun getFoodLogsByUser(
        @Header("Authorization") authorization: String,
        @Path("user_id") userId: Int
    ): Response<FoodLogsResponse>
}
