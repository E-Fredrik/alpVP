package com.example.alpvp.data.repository

import com.example.alpvp.ui.model.*
import com.example.alpvp.data.services.appService
import kotlinx.coroutines.flow.Flow

class AppRepository(private val AppService: appService) {

    // User operations
    suspend fun getUser(userId: Int): User {
        return apiService.getUser(userId)
    }

    suspend fun updateUser(user: User): User {
        return apiService.updateUser(user)
    }

    suspend fun updateUserGoals(userId: Int, bmiGoal: Float): User {
        return apiService.updateUserGoals(userId, bmiGoal)
    }

    // Food Log operations
    suspend fun logFood(foodLog: FoodLog): FoodLog {
        return apiService.createFoodLog(foodLog)
    }

    suspend fun getUserFoodLogs(userId: Int): List<FoodLog> {
        return apiService.getUserFoodLogs(userId)
    }

    suspend fun getFoodLogsByLocation(latitude: Float, longitude: Float): List<FoodLog> {
        return apiService.getFoodLogsByLocation(latitude, longitude)
    }

    // Daily Summary operations
    suspend fun getDailySummary(userId: Int, date: Long): DailySummary {
        return apiService.getDailySummary(userId, date)
    }

    // Friend operations
    suspend fun getFriends(userId: Int): List<Friend> {
        return apiService.getUserFriends(userId)
    }

    suspend fun getFriendsFoodLogs(userId: Int): List<FoodLog> {
        return apiService.getFriendsFoodLogs(userId)
    }

    suspend fun sendFriendRequest(requesterId: Int, addresseeId: Int): Friend {
        return apiService.sendFriendRequest(requesterId, addresseeId)
    }

    suspend fun updateFriendStatus(friendId: Int, status: FriendStatus): Friend {
        return apiService.updateFriendStatus(friendId, status)
    }
}
