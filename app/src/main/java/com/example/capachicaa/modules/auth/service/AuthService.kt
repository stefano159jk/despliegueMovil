package com.example.capachicaa.modules.auth.service

import com.example.capachicaa.modules.auth.model.LoginRequest
import com.example.capachicaa.modules.auth.model.LoginResponse
import com.example.capachicaa.modules.auth.model.RegisterRequest
import com.example.capachicaa.modules.auth.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<LoginResponse>

    @GET("me")
    suspend fun getProfile(): Response<User>

    @POST("logout")
    suspend fun logout(): Response<Unit>
}