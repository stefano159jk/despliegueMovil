package com.example.capachicaa.modules.entrepreneurs.controller

import com.example.capachicaa.modules.core.network.ApiClient
import com.example.capachicaa.modules.entrepreneurs.model.*
import com.example.capachicaa.modules.entrepreneurs.service.EntrepreneurService
import retrofit2.Response

class EntrepreneurController {

    private val api = ApiClient.retrofit.create(EntrepreneurService::class.java)


    suspend fun getAll(): Response<EntrepreneurResponse> {
        return api.getEntrepreneurs()
    }

    suspend fun getById(id: Int): Response<Entrepreneur> {
        return api.getEntrepreneur(id)
    }

    suspend fun create(req: EntrepreneurRequest): Response<Void> {
        return api.createEntrepreneur(req)
    }

    suspend fun update(id: Int, req: EntrepreneurRequest): Response<Void> {
        return api.updateEntrepreneur(id, req)
    }

    suspend fun delete(id: Int): Response<Void> {
        return api.deleteEntrepreneur(id)
    }
}
