package com.example.alpvp.data.repository

import com.example.alpvp.data.dto.*
import com.example.alpvp.data.Service.AppService

class PlaceRepository(private val appService: AppService) {
    
    suspend fun getAllPlaces(): List<Place> {
        val response = appService.getAllPlaces()
        if (response.isSuccessful && response.body()?.success == true) {
            return response.body()!!.data
        } else {
            throw Exception("Failed to get places: ${response.message()}")
        }
    }

    suspend fun getNearbyPlaces(latitude: Double, longitude: Double, radiusKm: Double = 1.0): List<Place> {
        val response = appService.getNearbyPlaces(latitude, longitude, radiusKm)
        if (response.isSuccessful && response.body()?.success == true) {
            return response.body()!!.data
        } else {
            throw Exception("Failed to get nearby places: ${response.message()}")
        }
    }

    suspend fun getPlacesByCategory(category: String): List<Place> {
        val response = appService.getPlacesByCategory(category)
        if (response.isSuccessful && response.body()?.success == true) {
            return response.body()!!.data
        } else {
            throw Exception("Failed to get places by category: ${response.message()}")
        }
    }
}
