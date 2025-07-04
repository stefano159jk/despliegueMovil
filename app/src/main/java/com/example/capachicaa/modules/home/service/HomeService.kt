package com.example.capachicaa.modules.home.service

import com.example.capachicaa.modules.home.model.HomeRequest
import com.example.capachicaa.modules.home.model.HomeResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface HomeService {
    @GET("/api/home")
    suspend fun getHome(): Response<HomeResponse>

    @PUT("/api/home")
    suspend fun updateHome(@Body data: HomeRequest): Response<Void>
}
