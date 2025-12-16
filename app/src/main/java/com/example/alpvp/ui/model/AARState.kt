package com.example.alpvp.ui.model

import com.example.alpvp.data.enums.PhoneUsage
import com.example.alpvp.data.enums.UserActivity
import com.example.alpvp.data.enums.VulnerabilityLevel

data class AARState(
    val userActivity: UserActivity = UserActivity.UNKNOWN,
    val phoneUsage: PhoneUsage = PhoneUsage.UNKNOWN,
    val vulnerabilityLevel: VulnerabilityLevel = VulnerabilityLevel.UNKNOWN,
    val confidence: Int = 0,
    val recommendation: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)
