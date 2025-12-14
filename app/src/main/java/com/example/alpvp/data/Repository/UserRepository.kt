// kotlin
package com.example.alpvp.data.Repository

import com.example.alpvp.data.Service.UserService
import com.example.alpvp.data.dto.User
import com.example.alpvp.data.dto.RegisterUserRequest
import com.example.alpvp.data.dto.UserLoginRequest
import java.io.IOException

class UserRepository(private val userService: UserService) {

    suspend fun loginUser(email: String, password: String): User {
        val request = UserLoginRequest(email = email, password = password)
        val response = try {
            userService.loginUser(request)
        } catch (e: IOException) {
            throw IOException("Network error: ${e.message}", e)
        }

        if (response.isSuccessful) {
            val body = response.body() ?: throw IllegalStateException("Empty response body")
            return body.data
        } else {
            val err = response.errorBody()?.string()
            throw Exception(err ?: "Login failed: ${response.code()}")
        }
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

        if (response.isSuccessful) {
            val body = response.body() ?: throw IllegalStateException("Empty response body")
            return body.data
        } else {
            val err = response.errorBody()?.string()
            throw Exception(err ?: "Register failed: ${response.code()}")
        }
    }
}
