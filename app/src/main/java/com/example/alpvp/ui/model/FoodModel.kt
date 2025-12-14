package com.example.alpvp.ui.model

data class FoodModel(
    val foodId: Int,
    val name: String,
    val calories: Int,
    val protein: Int = 0,
    val carbs: Int = 0,
    val fats: Int = 0,
    val servingSize: String = "100g",
    val category: String? = null
)
