package com.example.alpvp.data.repository

import com.example.alpvp.ui.model.User
import com.example.alpvp.data.services.AppService
import com.example.alpvp.data.services.UserGoals

class UserRepository(private val appService: AppService) {

    suspend fun getUser(userId: Int): User {
        return appService.getUser(userId)
    }

    suspend fun updateUser(userId: Int, user: User): User {
        return appService.updateUser(userId, user)
    }

    suspend fun updateUserGoals(userId: Int, bmiGoal: Float): User {
        return appService.updateUserGoals(userId, UserGoals(bmiGoal))
    }
}

