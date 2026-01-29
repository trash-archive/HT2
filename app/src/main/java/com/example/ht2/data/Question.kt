package com.example.ht2.data

data class Question(
    val id: Int,
    val text: String,
    val category: String,
    val isCustom: Boolean = false // Flag to identify user-created questions
)