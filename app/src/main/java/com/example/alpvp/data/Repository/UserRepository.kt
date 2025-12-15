// kotlin
package com.example.alpvp.data.Repository

import com.example.alpvp.data.Service.UserService
import com.example.alpvp.data.dto.User
import com.example.alpvp.data.dto.RegisterUserRequest
import com.example.alpvp.data.dto.UserLoginRequest
import java.io.IOException

import com.example.alpvp.ui.model.User
import com.example.alpvp.data.services.AppService
import com.example.alpvp.data.services.UserGoals

    suspend fun loginUser(email: String, password: String): User {
        val request = UserLoginRequest(email = email, password = password)
        val response = try {
            userService.loginUser(request)
        } catch (e: IOException) {
            throw IOException("Network error: ${e.message}", e)
        }
class UserRepository(private val appService: AppService) {

    suspend fun getUser(userId: Int): User {
        return appService.getUser(userId)
    }

    suspend fun registerUser(
        username: String,
        email: String,
        password: String,
        height: Int,
        weight: Int,
        bmiGoal: Int
    ): User {
        val request = RegisterUserRequest(
            bmiGoal = bmiGoal,
            email = email,
            height = height,
            password = password,
            username = username,
            weight = weight
        )

        val response = try {
            userService.registerUser(request)
        } catch (e: IOException) {
            throw IOException("Network error: ${e.message}", e)
        }
    suspend fun updateUser(userId: Int, user: User): User {
        return appService.updateUser(userId, user)
    }

    suspend fun updateUserGoals(userId: Int, bmiGoal: Float): User {
        return appService.updateUserGoals(userId, UserGoals(bmiGoal))
    }
}
