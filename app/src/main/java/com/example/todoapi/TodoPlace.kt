package com.example.todoapi

data class TodoPlace(
    val completed: Boolean,
    val id: Int,
    val title: String,
    val userId: Int
)