package com.example.capachicaa.modules.reservations.model

data class ReservationRequest(
    val product_id: Int,
    val quantity: Int,
    val reservation_date: String,
    val operation_code: String,
    val message: String?,
    val receipt: String? // Puedes usar multipart más adelante para el archivo
)