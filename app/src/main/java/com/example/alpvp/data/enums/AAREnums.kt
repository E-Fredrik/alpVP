package com.example.alpvp.data.enums

/**
 * Enums and helper functions related to AAR (activity and phone usage) moved here
 * so they can be reused across the app and keep UI model files lightweight.
 */

enum class UserActivity {
    STILL,           // Not moving (sitting, standing)
    WALKING,         // Walking slowly
    RUNNING,         // Running or jogging
    ON_BICYCLE,      // Cycling
    IN_VEHICLE,      // In a car, bus, etc.
    TILTING,         // Phone is tilted
    UNKNOWN          // Unable to determine
}

enum class PhoneUsage {
    ACTIVELY_USING,  // Screen on, user interacting
    SCREEN_ON_IDLE,  // Screen on but no interaction
    SCREEN_OFF,      // Screen off but app running
    POCKET,          // In pocket (detected by proximity sensor)
    FACE_DOWN,       // Phone face down on surface
    UNKNOWN          // Unable to determine
}

enum class VulnerabilityLevel {
    SAFE,            // Low risk (sitting at home, actively using phone)
    MODERATE,        // Medium risk (walking while using phone)
    HIGH,            // High risk (walking with phone down, cycling)
    CRITICAL,        // Critical risk (in vehicle, running)
    UNKNOWN          // Not enough data
}

/**
 * Determines vulnerability level based on activity and phone usage
 */
fun calculateVulnerability(activity: UserActivity, phoneUsage: PhoneUsage): VulnerabilityLevel {
    return when {
        // Critical scenarios - dangerous activities
        activity == UserActivity.IN_VEHICLE && phoneUsage == PhoneUsage.ACTIVELY_USING -> VulnerabilityLevel.CRITICAL
        activity == UserActivity.RUNNING && phoneUsage == PhoneUsage.ACTIVELY_USING -> VulnerabilityLevel.CRITICAL
        activity == UserActivity.ON_BICYCLE -> VulnerabilityLevel.CRITICAL

        // High risk - moving without full attention
        activity == UserActivity.WALKING && phoneUsage == PhoneUsage.ACTIVELY_USING -> VulnerabilityLevel.HIGH
        activity == UserActivity.WALKING && phoneUsage == PhoneUsage.SCREEN_ON_IDLE -> VulnerabilityLevel.HIGH
        activity == UserActivity.WALKING && phoneUsage == PhoneUsage.POCKET -> VulnerabilityLevel.HIGH

        // Moderate risk - potentially distracted
        activity == UserActivity.STILL && phoneUsage == PhoneUsage.ACTIVELY_USING -> VulnerabilityLevel.MODERATE
        activity == UserActivity.WALKING && phoneUsage == PhoneUsage.SCREEN_OFF -> VulnerabilityLevel.MODERATE

        // Safe scenarios
        activity == UserActivity.STILL && phoneUsage == PhoneUsage.SCREEN_OFF -> VulnerabilityLevel.SAFE
        activity == UserActivity.STILL && phoneUsage == PhoneUsage.FACE_DOWN -> VulnerabilityLevel.SAFE

        // Unknown
        else -> VulnerabilityLevel.UNKNOWN
    }
}

/**
 * Get recommendation based on vulnerability level
 */
fun getVulnerabilityRecommendation(level: VulnerabilityLevel, activity: UserActivity): String {
    return when (level) {
        VulnerabilityLevel.CRITICAL -> when (activity) {
            UserActivity.IN_VEHICLE -> "⚠️ Don't use your phone while driving! Pull over if you need to log food."
            UserActivity.RUNNING -> "⚠️ Stop running before logging food. Stay safe!"
            UserActivity.ON_BICYCLE -> "⚠️ Don't use your phone while cycling! Find a safe place to stop."
            else -> "⚠️ Critical: Stop what you're doing before using the app."
        }
        VulnerabilityLevel.HIGH -> "⚠️ You're walking. Be aware of your surroundings while logging food."
        VulnerabilityLevel.MODERATE -> "💡 You're in a good position to log your meal."
        VulnerabilityLevel.SAFE -> "✅ Safe environment detected. Log your food comfortably."
        VulnerabilityLevel.UNKNOWN -> "Analyzing your activity..."
    }
}

