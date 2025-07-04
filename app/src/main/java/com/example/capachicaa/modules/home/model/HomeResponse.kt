package com.example.capachicaa.modules.home.model

data class HomeResponse(
    val id: Int,
    val title: String,
    val subtitle: String,
    val description: String,
    val videoUrl: String,
    val image: String,
    val updatedAt: String
)
