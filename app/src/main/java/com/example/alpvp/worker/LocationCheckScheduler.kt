package com.example.alpvp.worker

import android.content.Context
import android.util.Log
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object LocationCheckScheduler {
    
    private const val TAG = "LocationCheckScheduler"
    private const val WORK_NAME = LocationCheckWorker.WORK_NAME
    
    /**
     * Start periodic location checks (every 30 minutes)
     */
    fun startLocationChecks(context: Context) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        
        val workRequest = PeriodicWorkRequestBuilder<LocationCheckWorker>(
            30, TimeUnit.MINUTES,
            15, TimeUnit.MINUTES // Flex interval
        )
            .setConstraints(constraints)
            .build()
        
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP, // Keep existing if already scheduled
            workRequest
        )
        
        Log.d(TAG, "Location checks scheduled (every 30 minutes)")
    }
    
    /**
     * Stop periodic location checks
     */
    fun stopLocationChecks(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        Log.d(TAG, "Location checks cancelled")
    }
    
    /**
     * Check if location checks are currently scheduled
     */
    fun isScheduled(context: Context): Boolean {
        val workInfos = WorkManager.getInstance(context)
            .getWorkInfosForUniqueWork(WORK_NAME)
            .get()
        return workInfos.any { !it.state.isFinished }
    }
}
