package com.example.alpvp.data.repository

import com.example.alpvp.data.dto.RegisterUserRequest
import com.example.alpvp.data.dto.UserLoginRequest
import com.example.alpvp.data.dto.UserLoginResponse
import com.example.alpvp.data.Service.UserService

class AuthRepository(private val userService: UserService) {

    suspend fun registerUser(username: String, email: String, password: String, height: Int, weight: Int, bmiGoal: Int = 22): UserLoginResponse {
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
            return response.body()!!
        } else {
            throw Exception("Registration failed: ${response.message()}")
        }
    }

    suspend fun loginUser(email: String, password: String): UserLoginResponse {
        val request = UserLoginRequest(email = email, password = password)
        val response = userService.loginUser(request)
        if (response.isSuccessful && response.body() != null) {
            return response.body()!!
        } else {
            throw Exception("Login failed: ${response.message()}")
        }
    }
}

