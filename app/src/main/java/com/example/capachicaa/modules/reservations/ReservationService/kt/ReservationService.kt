package com.example.capachicaa.modules.reservations.service


import com.example.capachicaa.modules.reservations.model.Reservation
import com.example.capachicaa.modules.reservations.model.ReservationCreateResponse
import com.example.capachicaa.modules.reservations.model.ReservationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST


interface ReservationService {
    @GET("reservations")
    suspend fun getAll(): Response<List<Reservation>>

    @POST("reservations")
    suspend fun createReservation(
        @Header("Authorization") token: String,
        @Body request: ReservationRequest
    ): Response<ReservationCreateResponse>

    @GET("reservations/my")
    suspend fun getMyReservations(
        @Header("Authorization") token: String
    ): Response<List<Reservation>>






}
