package com.example.capachicaa.modules.products.service

import com.example.capachicaa.modules.categories.model.Category
import com.example.capachicaa.modules.products.model.Product
import com.example.capachicaa.modules.products.model.ProductRequest
import retrofit2.Response
import retrofit2.http.*

interface ProductService {

    @GET("products/my")
    suspend fun getMyProducts(@Header("Authorization") token: String): Response<List<Product>>
    @GET("products")
    suspend fun getAllProducts(
        @Header("Authorization") token: String
    ): Response<List<Product>>

    @POST("products")
    suspend fun createProduct(
        @Header("Authorization") token: String,
        @Body data: ProductRequest
    ): Response<Void>
    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>

    @DELETE("products/{id}")
    suspend fun deleteProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Void>

    @GET("products/{id}")
    suspend fun getProductById(@Header("Authorization") token: String, @Path("id") id: Int): Response<Product>

    @PUT("products/{id}")
    suspend fun updateProduct(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body productRequest: ProductRequest
    ): Response<Void>

}
