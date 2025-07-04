package com.example.capachicaa.modules.place.model

data class Place(
    val id: Int,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val activity: String?,         // Actividad opcional
    val categoryId: Int,           // ID necesario para edición
    val categoryName: String?      // Nombre visible de la categoría (relación)
)
