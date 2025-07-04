package com.example.capachicaa.modules.place.request

data class PlaceRequest(
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val activity: String?,
    val categoryId: Int
)
