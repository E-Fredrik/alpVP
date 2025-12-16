package com.example.alpvp.data.dto

data class PlacesResponse(
    val success: Boolean,
    val data: List<Place>
)

data class Place(
    val place_id: Int,
    val name: String,
    val category: String, // "RESTAURANT", "PARK", "GYM", "STORE", "OTHER"
    val latitude: Double,
    val longitude: Double,
    val geofenceRadius: Int
)
