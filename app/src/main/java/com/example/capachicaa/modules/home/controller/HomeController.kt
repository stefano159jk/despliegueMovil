package com.example.capachicaa.modules.home.controller

import com.example.capachicaa.modules.core.network.ApiClient
import com.example.capachicaa.modules.home.model.HomeRequest
import com.example.capachicaa.modules.home.model.HomeResponse
import com.example.capachicaa.modules.home.service.HomeService
import retrofit2.Response

class HomeController {

    private val api = ApiClient.retrofit.create(HomeService::class.java)

    suspend fun getContent(): Response<HomeResponse> {
        return api.getHome()
    }

    suspend fun updateContent(data: HomeRequest): Response<Void> {
        return api.updateHome(data)
    }
}
