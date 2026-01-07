package com.example.alpvp.data.Service

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

class LocationService(private val context: Context) {
    
    private val fusedLocationClient: FusedLocationProviderClient = 
        LocationServices.getFusedLocationProviderClient(context)
    
    companion object {
        private const val TAG = "LocationService"
        const val DEFAULT_LATITUDE = 0.0
        const val DEFAULT_LONGITUDE = 0.0
    }
    
    /**
     * Check if location permissions are granted
     */
    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    /**
     * Get current location with proper permission handling
     * Returns Location or null if unavailable
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getCurrentLocation(): Location? {
        if (!hasLocationPermission()) {
            Log.w(TAG, "Location permissions not granted")
            return null
        }

        return try {
            val cancellationTokenSource = CancellationTokenSource()
            
            // Try to get current location first
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).await()
            
            if (location != null) {
                Log.d(TAG, "✅ Current location obtained: ${location.latitude}, ${location.longitude}")
                location
            } else {
                Log.w(TAG, "⚠️ Current location is null, trying last known location")
                getLastKnownLocation()
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to get current location: ${e.message}, trying last known")
            getLastKnownLocation()
        }
    }
    
    /**
     * Fallback to last known location
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private suspend fun getLastKnownLocation(): Location? {
        if (!hasLocationPermission()) {
            return null
        }

        return try {
            val location = fusedLocationClient.lastLocation.await()
            if (location != null) {
                Log.d(TAG, "✅ Last known location: ${location.latitude}, ${location.longitude}")
            } else {
                Log.w(TAG, "⚠️ No last known location available")
            }
            location
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to get last known location: ${e.message}")
            null
        }
    }
    
    /**
     * Get location coordinates as Pair (latitude, longitude)
     * Returns default coordinates if location unavailable
     */
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    suspend fun getLocationCoordinates(): Pair<Double, Double> {
        val location = getCurrentLocation()
        return if (location != null) {
            Pair(location.latitude, location.longitude)
        } else {
            Log.w(TAG, "Using default coordinates (0.0, 0.0)")
            Pair(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
        }
    }
}
