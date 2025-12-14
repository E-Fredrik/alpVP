// kotlin
package com.example.alpvp.data.Service

import com.example.alpvp.data.dto.FoodInLogRequest
import com.example.alpvp.data.dto.FoodInLogResponse
import com.example.alpvp.data.dto.FoodLogRequest
import com.example.alpvp.data.dto.FoodLogResponse
import com.example.alpvp.data.dto.FoodResponse
import com.example.alpvp.data.dto.FoodItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface FoodService {
    @POST("createfood")
    suspend fun createFood(
        @Body food: FoodItem
    ): Response<FoodResponse>

    @GET("getfood/{id}")
    suspend fun getFood(
        @Path("id") id: Int
    ): Response<FoodResponse>

    // Requires Authorization header: pass "Bearer <token>"
    @POST("foodinlogs")
    suspend fun createFoodInLog(
        @Header("Authorization") authorization: String,
        @Body request: FoodInLogRequest
    ): Response<FoodInLogResponse>

    // Requires Authorization header: pass "Bearer <token>"
    @POST("foodlogs")
    suspend fun createFoodLog(
        @Header("Authorization") authorization: String,
        @Body request: FoodLogRequest
    ): Response<FoodLogResponse>

    // Requires Authorization header: pass "Bearer <token>"
    @GET("foodlogs/{id}")
    suspend fun getFoodLog(
        @Header("Authorization") authorization: String,
        @Path("id") id: Int
    ): Response<FoodLogResponse>

    @GET("foodlogs/user/{user_id}")
    suspend fun getFoodLogsByUser(
        @Header("Authorization") authorization: String,
        @Path("user_id") userId: Int
    ): Response<List<FoodLogResponse>>

    @GET("getfoodbyname/{name}")
    suspend fun getFoodByName(
        @Path("name") name: String
    ): Response<List<FoodResponse>>
}
