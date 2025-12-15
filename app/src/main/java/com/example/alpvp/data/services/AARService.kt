package com.example.alpvp.data.services

import android.app.ActivityManager
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.PowerManager
import com.example.alpvp.ui.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.sqrt

/**
 * AAR Service - Detects user activity and phone usage patterns
 * Uses device sensors to determine vulnerability level
 */
class AARService(private val context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

    private val _aarState = MutableStateFlow(AARState())
    val aarState: StateFlow<AARState> = _aarState

    private var lastAcceleration = 0f
    private var accelerationHistory = mutableListOf<Float>()
    private var gyroHistory = mutableListOf<Float>()
    private var isProximityNear = false

    private var lastUpdateTime = 0L
    private val UPDATE_INTERVAL = 30000L // Update every 30 seconds for background operation

    fun startMonitoring() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        proximity?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    fun stopMonitoring() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> handleAccelerometer(event)
            Sensor.TYPE_GYROSCOPE -> handleGyroscope(event)
            Sensor.TYPE_PROXIMITY -> handleProximity(event)
        }

        // Update AAR state periodically
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastUpdateTime > UPDATE_INTERVAL) {
            updateAARState()
            lastUpdateTime = currentTime
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Not used
    }

    private fun handleAccelerometer(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = sqrt(x * x + y * y + z * z)
        accelerationHistory.add(acceleration)

        // Keep only last 10 readings
        if (accelerationHistory.size > 10) {
            accelerationHistory.removeAt(0)
        }

        lastAcceleration = acceleration
    }

    private fun handleGyroscope(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val rotation = sqrt(x * x + y * y + z * z)
        gyroHistory.add(rotation)

        // Keep only last 10 readings
        if (gyroHistory.size > 10) {
            gyroHistory.removeAt(0)
        }
    }

    private fun handleProximity(event: SensorEvent) {
        val distance = event.values[0]
        val maxRange = event.sensor.maximumRange

        // Near if distance is less than 5cm or less than half max range
        isProximityNear = distance < 5f || distance < maxRange / 2
    }

    private fun updateAARState() {
        val activity = detectUserActivity()
        val phoneUsage = detectPhoneUsage()
        val vulnerability = calculateVulnerability(activity, phoneUsage)
        val recommendation = getVulnerabilityRecommendation(vulnerability, activity)

        _aarState.value = AARState(
            userActivity = activity,
            phoneUsage = phoneUsage,
            vulnerabilityLevel = vulnerability,
            recommendation = recommendation,
            lastUpdated = System.currentTimeMillis()
        )
    }

    private fun detectUserActivity(): UserActivity {
        if (accelerationHistory.isEmpty()) return UserActivity.UNKNOWN

        val avgAcceleration = accelerationHistory.average().toFloat()
        val variance = accelerationHistory.map { (it - avgAcceleration) * (it - avgAcceleration) }.average()
        val avgGyro = if (gyroHistory.isNotEmpty()) gyroHistory.average().toFloat() else 0f

        return when {
            // High acceleration variance = moving
            variance > 15f && avgAcceleration > 15f -> UserActivity.RUNNING
            variance > 10f && avgAcceleration > 12f -> UserActivity.WALKING
            variance > 5f && avgGyro > 1.5f -> UserActivity.IN_VEHICLE
            variance > 3f && avgGyro > 0.8f -> UserActivity.ON_BICYCLE
            avgGyro > 0.5f -> UserActivity.TILTING
            variance < 2f -> UserActivity.STILL
            else -> UserActivity.UNKNOWN
        }
    }

    private fun detectPhoneUsage(): PhoneUsage {
        val isScreenOn = powerManager.isInteractive

        return when {
            // Proximity sensor near = in pocket or near face
            isProximityNear && !isScreenOn -> PhoneUsage.POCKET
            isProximityNear && isScreenOn -> PhoneUsage.ACTIVELY_USING

            // Screen states
            isScreenOn && isUserInteracting() -> PhoneUsage.ACTIVELY_USING
            isScreenOn && !isUserInteracting() -> PhoneUsage.SCREEN_ON_IDLE
            !isScreenOn -> PhoneUsage.SCREEN_OFF

            else -> PhoneUsage.UNKNOWN
        }
    }

    private fun isUserInteracting(): Boolean {
        // Check if user recently interacted (this is simplified)
        // In a real app, you'd track touch events
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningProcesses = activityManager.runningAppProcesses

        return runningProcesses?.any {
            it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
        } ?: false
    }
}

