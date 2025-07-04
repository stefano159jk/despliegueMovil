package com.example.capachicaa.modules.tours.service

import com.example.capachicaa.modules.tours.model.Tour
import com.example.capachicaa.modules.tours.model.TourRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface TourService {

    /* Crear Tour */
    @Headers("Accept: application/json")
    @POST("tours")
    suspend fun createTour(@Body request: TourRequest): Response<Tour>

    /* Obtener lista de Tours */
    @GET("tours")
    suspend fun getTours(): Response<List<Tour>>

    @PUT("tours/{id}")
    suspend fun updateTour(
        @Path("id")   id: Int,
        @Body         req: TourRequest
    ): Response<Tour>

    /* Delete */
    @DELETE("tours/{id}")
    suspend fun deleteTour(@Path("id") id: Int): Response<Unit>

}
