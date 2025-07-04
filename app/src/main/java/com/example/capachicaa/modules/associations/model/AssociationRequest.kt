package com.example.capachicaa.modules.associations.model

import com.google.gson.annotations.SerializedName

data class AssociationRequest(
    @SerializedName("name")        val name: String,
    @SerializedName("region")      val region: String,
    @SerializedName("description") val description: String
)
