package com.example.capachicaa.modules.entrepreneurs.model

data class EntrepreneurRequest(
    val username: String,
    val email: String,
    val password: String? = null,
    val businessName: String,
    val phone: String,
    val district: String,
    val description: String?,
    val associationId: Int?,
    val placeId: Int?,
    val lat: Double?,
    val lng: Double?,
    val categories: List<Int>,
    val status: String
)
