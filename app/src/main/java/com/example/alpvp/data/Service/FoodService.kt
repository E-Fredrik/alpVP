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
    // Auth headers are automatically added by AuthInterceptor
    
    @POST("foods")
    suspend fun createFood(
        @Body food: FoodItem
    ): Response<FoodResponse>

    @GET("foods/{id}")
    suspend fun getFood(
        @Path("id") id: Int
    ): Response<FoodResponse>

    @GET("foods/name/{name}")
    suspend fun getFoodByName(
        @Path("name") name: String
    ): Response<FoodListResponse>

    @POST("food-in-logs")
    suspend fun createFoodInLog(
        @Body request: FoodInLogRequest
    ): Response<FoodInLogResponse>

    @POST("food-logs")
    suspend fun createFoodLog(
        @Body request: FoodLogRequest
    ): Response<FoodLogResponse>

    @GET("food-logs/{id}")
    suspend fun getFoodLog(
        @Path("id") id: Int
    ): Response<FoodLogResponse>

    @GET("food-logs/user/{user_id}")
    suspend fun getFoodLogsByUser(
        @Path("user_id") userId: Int
    ): Response<FoodLogsResponse>
}
