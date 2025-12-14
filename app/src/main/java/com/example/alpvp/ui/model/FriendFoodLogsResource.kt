package com.example.alpvp.ui.model

data class FriendFoodLogsResource(
    val friendFoodLogs: List<FriendFoodLog> = listOf(
        FriendFoodLog(userId = 1, userName = "Alice Johnson", timestamp = now - 5 * 60_000L, foodName = "Chicken Salad", calories = 420, protein = 32),
        FriendFoodLog(userId = 2, userName = "Bob Smith", timestamp = now - 40 * 60_000L, foodName = "Protein Shake", calories = 250, protein = 30),
        FriendFoodLog(userId = 3, userName = "Carlos Rivera", timestamp = now - 3 * 60 * 60_000L, foodName = "Steak and Veg", calories = 680, protein = 55),
        FriendFoodLog(userId = 4, userName = "Dana Lee", timestamp = now - 26 * 60 * 60_000L, foodName = "Oatmeal", calories = 180, protein = 6),
        FriendFoodLog(userId = 5, userName = "Eve Thompson", timestamp = now - 2 * 60_000L, foodName = "Avocado Toast", calories = 320, protein = 8)

)
