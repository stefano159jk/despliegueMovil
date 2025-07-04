package com.example.capachicaa.modules.place.service

import com.example.capachicaa.modules.place.model.Place
import com.example.capachicaa.modules.place.request.PlaceRequest
import retrofit2.Response
import retrofit2.http.*

interface PlaceService {

    // Obtener todos los lugares turísticos
    @GET("places")
    suspend fun getAll(): Response<List<Place>>

    // Obtener un lugar específico por ID
    @GET("places/{id}")
    suspend fun getById(@Path("id") id: Int): Response<Place>

    // Crear un nuevo lugar turístico
    @POST("places")
    suspend fun create(@Body request: PlaceRequest): Response<Place>

    // Actualizar un lugar turístico existente
    @PUT("places/{id}")
    suspend fun update(
        @Path("id") id: Int,
        @Body request: PlaceRequest
    ): Response<Place>

    // Eliminar un lugar turístico
    @DELETE("places/{id}")
    suspend fun delete(@Path("id") id: Int): Response<Void>
}
