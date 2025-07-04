package com.example.capachicaa.modules.reservations.controller

import android.content.Context
import com.example.capachicaa.modules.core.network.ApiClient
import com.example.capachicaa.modules.reservations.model.Reservation
import com.example.capachicaa.modules.reservations.model.ReservationCreateResponse
import com.example.capachicaa.modules.reservations.model.ReservationRequest
import com.example.capachicaa.modules.reservations.model.ReservationResponse
import com.example.capachicaa.modules.reservations.service.ReservationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ReservationController(private val context: Context) {

    private val service = ApiClient.getRetrofit(context).create(ReservationService::class.java)

    suspend fun getAll() = withContext(Dispatchers.IO) {
        service.getAll()
    }

    suspend fun createReservation(token: String, request: ReservationRequest): Response<ReservationCreateResponse> =
        withContext(Dispatchers.IO) {
            service.createReservation("Bearer $token", request)
        }
    suspend fun getMyReservations(token: String): Response<List<Reservation>> =
        withContext(Dispatchers.IO) {
            service.getMyReservations("Bearer $token")
        }




}
