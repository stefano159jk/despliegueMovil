package com.example.capachicaa.modules.tours.controller

import com.example.capachicaa.modules.core.network.ApiClient
import com.example.capachicaa.modules.tours.model.Tour
import com.example.capachicaa.modules.tours.model.TourRequest
import com.example.capachicaa.modules.tours.service.TourService
import retrofit2.Response

class TourController {

    private val service = ApiClient.retrofit.create(TourService::class.java)

    // Crear un nuevo tour
    suspend fun create(request: TourRequest): Response<Tour> =
        service.createTour(request)

    // Obtener todos los tours
    suspend fun all(): Response<List<Tour>> =
        service.getTours()

    // Actualizar un tour existente
    suspend fun update(id: Int, request: TourRequest): Response<Tour> =
        service.updateTour(id, request)

    // Eliminar un tour
    suspend fun delete(id: Int): Response<Unit> =
        service.deleteTour(id)
}
