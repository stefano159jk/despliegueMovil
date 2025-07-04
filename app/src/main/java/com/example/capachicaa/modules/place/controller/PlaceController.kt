package com.example.capachicaa.modules.place.controller

import com.example.capachicaa.modules.core.network.ApiClient
import com.example.capachicaa.modules.place.model.Place
import com.example.capachicaa.modules.place.request.PlaceRequest
import com.example.capachicaa.modules.place.service.PlaceService
import retrofit2.Response

class PlaceController {

    private val api = ApiClient.retrofit.create(PlaceService::class.java)

    /* ---------- CRUD ---------- */
    suspend fun getAll(): Response<List<Place>> =
        api.getAll()

    suspend fun getById(id: Int): Response<Place> =
        api.getById(id)

    suspend fun create(request: PlaceRequest): Response<Place> =
        api.create(request)

    suspend fun update(id: Int, request: PlaceRequest): Response<Place> =
        api.update(id, request)

    suspend fun delete(id: Int): Response<Void> =
        api.delete(id)
}
