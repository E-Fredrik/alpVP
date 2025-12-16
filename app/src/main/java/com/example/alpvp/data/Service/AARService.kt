package com.example.alpvp.data.services

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.alpvp.data.dto.ActivityLogRequest
import com.example.alpvp.data.dto.BulkActivityLogRequest
import com.example.alpvp.ui.model.AARState
import com.example.alpvp.ui.model.UserActivity
import com.example.alpvp.ui.model.VulnerabilityLevel
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * Activity and Attention Recognition (AAR) Service
 * Detects user activities and automatically syncs them to backend
 */
class AARService(
    private val context: Context,
    private val appService: AppService
) {
    private val activityRecognitionClient: ActivityRecognitionClient = 
        ActivityRecognition.getClient(context)
    
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    
    private val _aarState = MutableStateFlow(AARState())
    val aarState: StateFlow<AARState> = _aarState

    // Activity buffering for backend sync
    private val activityBuffer = mutableListOf<ActivityLogRequest>()
    private var lastSyncTime = System.currentTimeMillis()
    private var currentUserId: Int? = null
    
    companion object {
        private const val TAG = "AARService"
        private const val UPDATE_INTERVAL_MS = 30000L // 30 seconds
        private const val BUFFER_SIZE = 10
        private const val SYNC_INTERVAL_MS = 30 * 60 * 1000L // 30 minutes
        
        // Activity thresholds
        private const val VULNERABILITY_CONFIDENCE_THRESHOLD = 75
        private const val ACTIVITY_CONFIDENCE_THRESHOLD = 50
    }

    /**
     * Start monitoring user activities
     * @param userId Current user's ID for backend syncing
     */
    fun startMonitoring(userId: Int) {
        this.currentUserId = userId
        
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.e(TAG, "Activity recognition permission not granted")
            return
        }

        try {
            // Request activity updates
            val pendingIntent = createActivityDetectionPendingIntent()
            activityRecognitionClient.requestActivityUpdates(
                UPDATE_INTERVAL_MS,
                pendingIntent
            ).addOnSuccessListener {
                Log.d(TAG, "✅ Activity recognition started for user $userId")
            }.addOnFailureListener { e ->
                Log.e(TAG, "❌ Failed to start activity recognition: ${e.message}")
            }
            
            // Also request activity transitions for more accurate detection
            requestActivityTransitionUpdates()
        } catch (e: Exception) {
            Log.e(TAG, "Error starting monitoring: ${e.message}")
        }
    }

    fun stopMonitoring() {
        try {
            val pendingIntent = createActivityDetectionPendingIntent()
            activityRecognitionClient.removeActivityUpdates(pendingIntent)
            
            // Sync any remaining activities before stopping
            scope.launch {
                syncActivitiesToBackend()
            }
            
            Log.d(TAG, "Activity recognition stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping monitoring: ${e.message}")
        }
    }

    private fun createActivityDetectionPendingIntent(): PendingIntent {
        val intent = android.content.Intent(context, ActivityRecognitionReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun requestActivityTransitionUpdates() {
        val transitions = mutableListOf<ActivityTransition>()
        
        // Monitor transitions for key activities
        val activitiesToMonitor = listOf(
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.STILL
        )
        
        for (activity in activitiesToMonitor) {
            transitions.add(
                ActivityTransition.Builder()
                    .setActivityType(activity)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                    .build()
            )
            transitions.add(
                ActivityTransition.Builder()
                    .setActivityType(activity)
                    .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                    .build()
            )
        }

        val request = ActivityTransitionRequest(transitions)
        val pendingIntent = createActivityDetectionPendingIntent()
        
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            activityRecognitionClient.requestActivityTransitionUpdates(request, pendingIntent)
                .addOnSuccessListener {
                    Log.d(TAG, "Activity transitions monitoring enabled")
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed to enable activity transitions: ${e.message}")
                }
        }
    }

    /**
     * Called when activity is detected
     * Updates state and buffers activity for backend sync
     */
    fun onActivityDetected(activity: DetectedActivity) {
        val userActivity = mapToUserActivity(activity)
        val confidence = activity.confidence
        
        Log.d(TAG, "Activity detected: ${userActivity.name} (${confidence}% confidence)")
        
        // Update local state
        _aarState.value = _aarState.value.copy(
            userActivity = userActivity,
            confidence = confidence,
            vulnerabilityLevel = calculateVulnerabilityLevel(userActivity, confidence),
            lastUpdated = System.currentTimeMillis()
        )
        
        // Log to backend (skip STILL and UNKNOWN to reduce noise)
        if (confidence >= ACTIVITY_CONFIDENCE_THRESHOLD && 
            userActivity != UserActivity.STILL && 
            userActivity != UserActivity.UNKNOWN
        ) {
            logActivityToBackend(userActivity, confidence)
        }
    }

    private fun mapToUserActivity(activity: DetectedActivity): UserActivity {
        return when (activity.type) {
            DetectedActivity.STILL -> UserActivity.STILL
            DetectedActivity.WALKING -> UserActivity.WALKING
            DetectedActivity.RUNNING -> UserActivity.RUNNING
            DetectedActivity.ON_BICYCLE -> UserActivity.ON_BICYCLE
            DetectedActivity.IN_VEHICLE -> UserActivity.IN_VEHICLE
            DetectedActivity.TILTING -> UserActivity.TILTING
            else -> UserActivity.UNKNOWN
        }
    }

    private fun calculateVulnerabilityLevel(
        activity: UserActivity,
        confidence: Int
    ): VulnerabilityLevel {
        // High vulnerability during activities that require attention
        if (confidence >= VULNERABILITY_CONFIDENCE_THRESHOLD) {
            return when (activity) {
                UserActivity.RUNNING -> VulnerabilityLevel.HIGH
                UserActivity.ON_BICYCLE -> VulnerabilityLevel.HIGH
                UserActivity.IN_VEHICLE -> VulnerabilityLevel.CRITICAL
                UserActivity.WALKING -> VulnerabilityLevel.MEDIUM
                UserActivity.STILL -> VulnerabilityLevel.LOW
                else -> VulnerabilityLevel.MEDIUM
            }
        }
        return VulnerabilityLevel.LOW
    }

    /**
     * Buffer activity for backend sync
     */
    private fun logActivityToBackend(activity: UserActivity, confidence: Int) {
        val userId = currentUserId ?: run {
            Log.e(TAG, "Cannot log activity - userId not set")
            return
        }
        
        val now = System.currentTimeMillis()
        
        // Map UserActivity to backend activity type string
        val activityType = when (activity) {
            UserActivity.WALKING -> "WALKING"
            UserActivity.RUNNING -> "RUNNING"
            UserActivity.ON_BICYCLE -> "CYCLING"
            UserActivity.IN_VEHICLE -> "IN_VEHICLE"
            else -> return // Skip other types
        }
        
        // Add to buffer
        activityBuffer.add(
            ActivityLogRequest(
                user_id = userId,
                activityType = activityType,
                startTime = now,
                endTime = now + 300000, // 5 minutes estimate
                confidence = confidence
            )
        )
        
        Log.d(TAG, "Buffered activity: $activityType (buffer size: ${activityBuffer.size})")
        
        // Sync if buffer is full OR 30 minutes have passed
        val shouldSync = activityBuffer.size >= BUFFER_SIZE || 
                        (now - lastSyncTime) > SYNC_INTERVAL_MS
        
        if (shouldSync) {
            scope.launch {
                syncActivitiesToBackend()
            }
        }
    }

    /**
     * Sync buffered activities to backend
     */
    private suspend fun syncActivitiesToBackend() {
        if (activityBuffer.isEmpty()) return
        
        val activitiesToSync = activityBuffer.toList()
        
        try {
            Log.d(TAG, "Syncing ${activitiesToSync.size} activities to backend...")
            
            val response = appService.bulkCreateActivityLogs(
                BulkActivityLogRequest(activitiesToSync)
            )
            
            if (response.isSuccessful && response.body()?.success == true) {
                val count = response.body()?.data?.count ?: activitiesToSync.size
                Log.d(TAG, "✅ Successfully synced $count activities to backend")
                
                // Clear buffer on success
                activityBuffer.clear()
                lastSyncTime = System.currentTimeMillis()
            } else {
                Log.e(TAG, "❌ Failed to sync activities: ${response.message()}")
                // Keep buffer for retry
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Exception syncing activities: ${e.message}")
            // Keep buffer for retry
        }
    }
}

/**
 * BroadcastReceiver to handle activity recognition results
 */
class ActivityRecognitionReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: android.content.Intent) {
        if (com.google.android.gms.location.ActivityRecognitionResult.hasResult(intent)) {
            val result = com.google.android.gms.location.ActivityRecognitionResult.extractResult(intent)
            result?.let {
                val mostProbableActivity = it.mostProbableActivity
                // Forward to AARService - you'll need to implement a singleton or DI pattern
                Log.d("ActivityReceiver", "Received activity: ${mostProbableActivity.type} (${mostProbableActivity.confidence}%)")
            }
        }
    }
}
