package com.example.alpvp.data.repository

import android.content.Context
import android.util.Log
import com.example.alpvp.data.dto.RegisterUserRequest
import com.example.alpvp.data.dto.UserLoginRequest
import com.example.alpvp.data.dto.User
import com.example.alpvp.data.Service.UserService
import com.example.alpvp.data.utils.TokenManager

class AuthRepository(
    private val userService: UserService,
    private val context: Context
) {

    suspend fun registerUser(username: String, email: String, password: String, height: Int, weight: Int, bmiGoal: Int = 22): User {
        val request = RegisterUserRequest(
            username = username,
            email = email,
            password = password,
            height = height,
            weight = weight,
            bmiGoal = bmiGoal
        )
        val response = userService.registerUser(request)
        if (response.isSuccessful && response.body() != null) {
            val user = response.body()!!.data
            // Save token after successful registration
            TokenManager.saveToken(context, user.token)
            Log.d("AuthRepository", "Token saved after registration: ${user.token.take(20)}...")
            return user
        } else {
            throw Exception("Registration failed: ${response.message()}")
        }
    }

    suspend fun loginUser(email: String, password: String): User {
        val request = UserLoginRequest(email = email, password = password)
        val response = userService.loginUser(request)
        if (response.isSuccessful && response.body() != null) {
            val user = response.body()!!.data
            // Save token after successful login
            TokenManager.saveToken(context, user.token)
            Log.d("AuthRepository", "Token saved after login: ${user.token.take(20)}...")
            return user
        } else {
            throw Exception("Login failed: ${response.message()}")
        }
    }
}

