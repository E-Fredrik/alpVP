package com.example.alpvp.data.repository
import com.example.alpvp.ui.model.FoodLog
import com.example.alpvp.data.Service.AppService
class FoodLogRepository(private val appService: AppService) {
    suspend fun logFood(foodLog: FoodLog): FoodLog {
        return appService.createFoodLog(foodLog)
    }
    suspend fun getUserFoodLogs(userId: Int): List<FoodLog> {
        return appService.getUserFoodLogs(userId)
    }
    suspend fun getFoodLogsByLocation(latitude: Float, longitude: Float, radius: Float = 1000f): List<FoodLog> {
        return appService.getFoodLogsByLocation(latitude, longitude, radius)
    }
}
