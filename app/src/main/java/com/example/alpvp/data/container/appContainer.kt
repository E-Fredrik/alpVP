package com.example.alpvp.data.container

import com.google.gson.GsonBuilder
import com.example.alpvp.data.repository.AppRepository
import com.example.alpvp.data.network.ApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
    companion object {
        private const val BASE_URL = "https://your-backend-api.com/" // Replace with your actual API URL
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()

    private val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val appRepository: AppRepository by lazy {
        AppRepository(apiService)
    }
}