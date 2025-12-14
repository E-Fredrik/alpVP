package com.example.alpvp.data.repository
import com.example.alpvp.ui.model.DailySummary
import com.example.alpvp.data.services.AppService
class DailySummaryRepository(private val appService: AppService) {
    suspend fun getDailySummary(userId: Int, date: Long): DailySummary {
        return appService.getDailySummary(userId, date)
    }
}
