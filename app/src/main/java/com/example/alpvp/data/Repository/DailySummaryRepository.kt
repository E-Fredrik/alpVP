package com.example.alpvp.data.Repository
import com.example.alpvp.ui.model.DailySummary
import com.example.alpvp.data.Service.AppService
class DailySummaryRepository(private val appService: AppService) {
    suspend fun getDailySummary(userId: Int, date: Long): DailySummary {
        return appService.getDailySummary(userId, date)
    }
}
