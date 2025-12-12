package com.example.alpvp.data.container

import com.example.alpvp.data.Repository.UserRepository
import com.example.alpvp.data.Service.FoodService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue
import kotlin.lazy
import com.example.alpvp.data.Service.UserService
import com.example.alpvp.data.Repository.FoodRepository



class AppContainer {
    companion object {
        val BASE_URL = "https://localhost:3000/api/"
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    private val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(userService)
    }

    private val foodService: FoodService by lazy {
        retrofit.create(FoodService::class.java)
    }

    val foodRepository: FoodRepository by lazy {
        FoodRepository(foodService)
    }

}