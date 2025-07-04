package com.example.capachicaa.modules.entrepreneurs.model

import com.example.capachicaa.modules.categories.model.Category

data class Entrepreneur(
    val id: Int,
    val username: String,
    val email: String,
    val businessName: String,
    val phone: String = "-",
    val district: String = "-",
    val description: String? = null,
    val associationId: Int? = null,
    val placeId: Int? = null,
    val lat: Double? = null,
    val lng: Double? = null,
    val categories: List<Category> = emptyList(),
    val user:          User?,
    val status: String = "inactivo"

)
