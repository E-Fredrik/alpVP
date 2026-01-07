package com.example.alpvp.worker

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.alpvp.data.container.AppContainer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocationCheckWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val container = AppContainer(applicationContext)
    private val locationService = container.locationService
    private val placeRepository = container.placeRepository
    private val notificationManager = container.smartNotificationManager

    companion object {
        private const val TAG = "LocationCheckWorker"
        const val WORK_NAME = "location_check_work"
        private const val MAX_DISTANCE_METERS = 500 // 500m radius
    }

    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    override suspend fun doWork(): Result {
        return try {
            Log.d(TAG, "Starting location check...")

            // Get current GPS location
            val (latitude, longitude) = locationService.getLocationCoordinates()

            if (latitude == 0.0 && longitude == 0.0) {
                Log.w(TAG, "No valid GPS location, skipping check")
                return Result.success()
            }

            Log.d(TAG, "Current location: $latitude, $longitude")

            // Option A: Check backend for nearby restaurants
            val backendRestaurants = try {
                placeRepository.getNearbyRestaurants(latitude, longitude, 1.0) // 1km radius
            } catch (e: Exception) {
                Log.e(TAG, "Failed to get backend restaurants: ${e.message}")
                emptyList()
            }

            // Filter restaurants within geofence radius
            val nearbyRestaurants = backendRestaurants.filter { backendPlace ->
                val distance = calculateDistance(
                    latitude,
                    longitude,
                    backendPlace.latitude,
                    backendPlace.longitude
                )
                distance <= backendPlace.geofenceRadius
            }

            if (nearbyRestaurants.isNotEmpty()) {
                // Found restaurant in backend database
                val closest = nearbyRestaurants.minByOrNull { backendPlace ->
                    calculateDistance(
                        latitude,
                        longitude,
                        backendPlace.latitude,
                        backendPlace.longitude
                    )
                }!!

                val distance = calculateDistance(
                    latitude,
                    longitude,
                    closest.latitude,
                    closest.longitude
                ).toInt()
                Log.d(TAG, "Backend restaurant found: ${closest.name} at ${distance}m")

                notificationManager.sendRestaurantReminderNotification(closest.name, distance)
                return Result.success()
            }

            // Option C: Fallback to Google Places API
            Log.d(TAG, "No backend restaurants found, trying Google Places API...")
            val googleRestaurants = checkGooglePlacesForRestaurants(latitude, longitude)

            if (googleRestaurants.isNotEmpty()) {
                val restaurantName = googleRestaurants.first()
                Log.d(TAG, "Google Places restaurant found: $restaurantName")

                notificationManager.sendRestaurantReminderNotification(
                    restaurantName,
                    MAX_DISTANCE_METERS
                )
                return Result.success()
            }

            Log.d(TAG, "No restaurants found nearby")
            Result.success()

        } catch (e: Exception) {
            Log.e(TAG, "Error checking location: ${e.message}", e)
            Result.retry()
        }
    }

    private suspend fun checkGooglePlacesForRestaurants(
        latitude: Double,
        longitude: Double
    ): List<String> = withContext(Dispatchers.IO) {
        return@withContext try {
            val client = OkHttpClient()
            val apiKey = "AIzaSyBMJjZxphnOH4wbnQLagTaXs9K-nKj-MnM"

            // Use Places API Nearby Search (HTTP)
            val url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" +
                    "location=$latitude,$longitude" +
                    "&radius=$MAX_DISTANCE_METERS" +
                    "&type=restaurant" +
                    "&key=$apiKey"

            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (!response.isSuccessful || responseBody == null) {
                Log.e(TAG, "Google Places API request failed: ${response.code}")
                return@withContext emptyList<String>()
            }

            val jsonObject = JSONObject(responseBody)
            val status = jsonObject.getString("status")

            if (status != "OK" && status != "ZERO_RESULTS") {
                Log.e(TAG, "Google Places API error: $status")
                return@withContext emptyList<String>()
            }

            val results =
                jsonObject.optJSONArray("results") ?: return@withContext emptyList<String>()
            val restaurants = mutableListOf<String>()

            for (i in 0 until results.length()) {
                val place = results.getJSONObject(i)
                val name = place.optString("name")
                if (name.isNotEmpty()) {
                    restaurants.add(name)
                }
            }

            Log.d(TAG, "Found ${restaurants.size} restaurants via Google Places")
            restaurants

        } catch (e: Exception) {
            Log.e(TAG, "Error querying Google Places: ${e.message}")
            emptyList()
        }
    }

    /**
     * Calculate distance between two coordinates in meters
     */
    private fun calculateDistance(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double
    ): Double {
        val earthRadius = 6371000.0 // meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return earthRadius * c
    }
}
