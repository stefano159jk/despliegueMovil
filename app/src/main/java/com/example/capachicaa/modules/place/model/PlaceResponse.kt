package com.example.capachicaa.modules.place.model

import com.google.gson.annotations.SerializedName

data class PlaceResponse(
    val id: Int,
    val name: String,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val activity: String,
    @SerializedName("category_name") val categoryName: String
)
