package com.example.capachicaa.modules.entrepreneurs.service

import com.example.capachicaa.modules.entrepreneurs.model.*
import retrofit2.Response
import retrofit2.http.*

interface EntrepreneurService {

    @GET("entrepreneurs")
    suspend fun getEntrepreneurs(): Response<EntrepreneurResponse>

    @GET("entrepreneurs/{id}")
    suspend fun getEntrepreneur(@Path("id") id: Int): Response<Entrepreneur>

    @POST("entrepreneurs")
    suspend fun createEntrepreneur(@Body req: EntrepreneurRequest): Response<Void>

    @PUT("entrepreneurs/{id}")
    suspend fun updateEntrepreneur(@Path("id") id: Int, @Body req: EntrepreneurRequest): Response<Void>

    @DELETE("entrepreneurs/{id}")
    suspend fun deleteEntrepreneur(@Path("id") id: Int): Response<Void>
}
