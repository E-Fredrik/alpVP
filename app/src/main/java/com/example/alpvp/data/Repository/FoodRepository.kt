package com.example.alpvp.data.Repository

import com.example.alpvp.data.Service.FoodService
import com.example.alpvp.data.dto.*
import retrofit2.Response
import java.io.IOException

class FoodRepository(private val foodService: FoodService) {

    suspend fun createFood(foodItem: FoodItem): FoodItem {
        val response: Response<FoodResponse> = try {
            foodService.createFood(foodItem)
        } catch (e: IOException) {
            throw IOException("Network error: ${e.message}", e)
        }

        if (response.isSuccessful) {
            val body = response.body() ?: throw IllegalStateException("Empty response body")
//            Log.d("FoodRepository", "Created food: ${body.data}")
            return body.data
        } else {
            val err = response.errorBody()?.string()
            throw Exception(err ?: "Create food failed: ${response.code()}")
        }
    }


    suspend fun getFoodItem(id: Int): FoodItem {
        val response: Response<FoodResponse> = try {
            foodService.getFood(id)
        } catch (e: IOException) {
            throw IOException("Network error: ${e.message}", e)
        }

        if (response.isSuccessful) {
            val body = response.body() ?: throw IllegalStateException("Empty response body")
            return body.data
        } else {
            val err = response.errorBody()?.string()
            throw Exception(err ?: "Get food failed: ${response.code()}")
        }
    }

    suspend fun createFoodInLog(token: String, request: FoodInLogRequest): FoodInLogItem {
        val authHeader = "Bearer $token"
        val response: Response<FoodInLogResponse> = try {
            foodService.createFoodInLog(authHeader, request)
        } catch (e: IOException) {
            throw IOException("Network error: ${e.message}", e)
        }

        if (response.isSuccessful) {
            val body = response.body() ?: throw IllegalStateException("Empty response body")
            return body.data
        } else {
            val err = response.errorBody()?.string()
            throw Exception(err ?: "Create food-in-log failed: ${response.code()}")
        }
    }

    suspend fun createFoodLog(token: String, request: FoodLogRequest): FoodLogItem {
        val authHeader = "Bearer $token"
        val response: Response<FoodLogResponse> = try {
            foodService.createFoodLog(authHeader, request)
        } catch (e: IOException) {
            throw IOException("Network error: ${e.message}", e)
        }

        if (response.isSuccessful) {
            val body = response.body() ?: throw IllegalStateException("Empty response body")
            return body.data
        } else {
            val err = response.errorBody()?.string()
            throw Exception(err ?: "Create food log failed: ${response.code()}")
        }
    }

    suspend fun getFoodLog(token: String, id: Int): FoodLogItem {
        val authHeader = "Bearer $token"
        val response: Response<FoodLogResponse> = try {
            foodService.getFoodLog(authHeader, id)
        } catch (e: IOException) {
            throw IOException("Network error: ${e.message}", e)
        }

        if (response.isSuccessful) {
            val body = response.body() ?: throw IllegalStateException("Empty response body")
            return body.data
        } else {
            val err = response.errorBody()?.string()
            throw Exception(err ?: "Get food log failed: ${response.code()}")
        }
    }

    suspend fun getFoodLogByUser(token: String, userId: Int): List<FoodLogItem> {
        val authHeader = "Bearer $token"
        val response: Response<FoodLogsResponse> = try {
            foodService.getFoodLogsByUser(authHeader, userId)
        } catch (e: IOException) {
            throw IOException("Network error: ${e.message}", e)
        }

        if (response.isSuccessful) {
            val body = response.body() ?: return emptyList()
            return body.`data`
        } else {
            val err = response.errorBody()?.string()
            throw Exception(err ?: "Get food logs by user failed: ${response.code()}")
        }
    }

    suspend fun getFoodByName(name: String): List<FoodItem> {
        val response: Response<FoodListResponse> = try {
            foodService.getFoodByName(name)
        } catch (e: IOException) {
            throw IOException("Network error: ${e.message}", e)
        }

        if (response.isSuccessful) {
            val body = response.body() ?: throw IllegalStateException("Empty response body")
            return body.data
        } else {
            val err = response.errorBody()?.string()
            throw Exception(err ?: "Get food by name failed: ${response.code()}")
        }
    }
}
