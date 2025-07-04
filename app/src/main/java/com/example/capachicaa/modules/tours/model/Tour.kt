package com.example.capachicaa.modules.tours.model

import java.io.Serializable

data class Tour(
    val id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val image: String?,
    val created_at: String?,
    val updated_at: String?
) : Serializable
