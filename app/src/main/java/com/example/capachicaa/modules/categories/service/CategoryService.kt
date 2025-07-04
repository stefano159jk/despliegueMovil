package com.example.capachicaa.modules.categories.service

import com.example.capachicaa.modules.categories.model.Category
import com.example.capachicaa.modules.categories.model.CategoryRequest
import retrofit2.Response
import retrofit2.http.*

interface CategoryService {

    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("categories/{id}")
    suspend fun getCategory(@Path("id") id: Int): Response<Category>

    @POST("categories")
    suspend fun createCategory(@Body data: CategoryRequest): Response<Void>

    @PUT("categories/{id}")
    suspend fun updateCategory(
        @Path("id") id: Int,
        @Body data: CategoryRequest
    ): Response<Void>

    @DELETE("categories/{id}")
    suspend fun deleteCategory(@Path("id") id: Int): Response<Void>


}
