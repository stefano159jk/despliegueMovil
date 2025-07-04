package com.example.capachicaa.modules.payments.service

import com.example.capachicaa.modules.payments.model.Payment
import com.example.capachicaa.modules.payments.model.PaymentResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

interface PaymentService {


    @GET("payments")
    suspend fun getAll(): Response<List<Payment>>



    @POST("payments/{id}/reject")
    suspend fun reject(@Path("id") id: Int): Response<PaymentResponse>
    @Multipart
    @POST("/api/payments")
    suspend fun enviarPago(
        @Header("Authorization") token: String,
        @PartMap data: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part image: MultipartBody.Part?
    ): Response<Unit>

    @GET("entrepreneur/payments")
    suspend fun getMyPayments(@Header("Authorization") token: String): Response<List<Payment>>

    @POST("payments/{id}/confirm")
    suspend fun confirmPayment(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Response<ResponseBody>


}
