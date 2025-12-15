package com.example.alpvp.data.repository

import com.example.alpvp.data.dto.RegisterUserRequest
import com.example.alpvp.data.dto.UserLoginRequest
import com.example.alpvp.data.dto.UserLoginResponse
import com.example.alpvp.data.Service.UserService
import retrofit2.Response

class AuthRepository(private val userService: UserService) {

    suspend fun registerUser(user: RegisterUserRequest): Response<UserLoginResponse> {
        return userService.registerUser(user)
    }

    suspend fun loginUser(user: UserLoginRequest): Response<UserLoginResponse> {
        return userService.loginUser(user)
    }
}

