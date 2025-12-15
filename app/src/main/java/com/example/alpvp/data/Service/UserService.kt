package com.example.alpvp.data.Service

import com.example.alpvp.data.dto.RegisterUserRequest
import com.example.alpvp.data.dto.UserLoginRequest
import com.example.alpvp.data.dto.UserLoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {
    @POST("api/register")
    suspend fun registerUser(
        @Body user: RegisterUserRequest
    ): Response<UserLoginResponse>

    @POST("api/login")
    suspend fun loginUser(
        @Body user: UserLoginRequest
    ): Response<UserLoginResponse>
}