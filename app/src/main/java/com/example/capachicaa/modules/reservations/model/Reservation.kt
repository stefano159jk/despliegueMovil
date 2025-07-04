package com.example.capachicaa.modules.reservations.model

import com.example.capachicaa.modules.products.model.Product
import com.example.capachicaa.modules.auth.model.User // ← importa el que ya tienes
import com.google.gson.annotations.SerializedName

data class Reservation(
    val id: Int,

    @SerializedName("product_name")
    val productName: String? = null,

    val quantity: Int? = null,

    @SerializedName("reservation_date")
    val reservationDate: String? = null,

    val status: String? = null,

    @SerializedName("total_amount")
    val totalAmount: Double? = null,

    @SerializedName("receipt_url")
    val receiptUrl: String? = null,

    // ✅ Relaciones reales
    val product: Product? = null,
    val user: User? = null
)
