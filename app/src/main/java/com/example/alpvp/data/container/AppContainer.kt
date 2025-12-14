package com.example.alpvp.data.container

import android.content.Context
import com.example.alpvp.data.Repository.UserRepository
import com.example.alpvp.data.Service.FoodService
import com.example.alpvp.data.UserPreferencesRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.alpvp.data.Service.UserService
import com.example.alpvp.data.Repository.FoodRepository

class AppContainer(private val context: Context) {
    companion object {
        const val BASE_URL = "http://10.0.2.2:3000/api/"
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(BASE_URL)
        .build()

    private val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val userPreferencesRepository: UserPreferencesRepository by lazy {
        UserPreferencesRepository(context)
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
