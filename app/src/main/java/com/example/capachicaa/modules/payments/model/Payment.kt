package com.example.capachicaa.modules.payments.model

import com.example.capachicaa.modules.reservations.model.Reservation
import com.google.gson.annotations.SerializedName

data class Payment(
    val id: Int,

    @SerializedName("client_name")
    val clientName: String?,

    @SerializedName("product_name")
    val productName: String?,

    val status: String?,

    @SerializedName("paid_at")
    val paidAt: String?,

    val amount: Double?,

    @SerializedName("confirmed_at")
    val confirmedAt: String?,

    @SerializedName("receipt_url")
    val receiptUrl: String?,

    @SerializedName("reservation")
    val reservation: Reservation? = null


)
