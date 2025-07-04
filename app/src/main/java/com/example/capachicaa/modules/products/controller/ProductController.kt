package com.example.capachicaa.modules.products.controller

import com.example.capachicaa.modules.core.network.ApiClient
import com.example.capachicaa.modules.products.model.Product
import com.example.capachicaa.modules.products.model.ProductRequest
import com.example.capachicaa.modules.products.service.ProductService
import retrofit2.Response

class ProductController {

    private val api = ApiClient.retrofit.create(ProductService::class.java)

    suspend fun getMyProducts(token: String): Response<List<Product>> =
        api.getMyProducts("Bearer $token")
    suspend fun getAllProducts(token: String): Response<List<Product>> {
        return api.getAllProducts("Bearer $token")
    }


    suspend fun createProduct(token: String, data: ProductRequest): Response<Void> =
        api.createProduct("Bearer $token", data)
    suspend fun deleteProduct(token: String, id: Int): Response<Void> =
        api.deleteProduct("Bearer $token", id)
    suspend fun getProductById(token: String, id: Int): Response<Product> {
        return api.getProductById("Bearer $token", id)
    }
    suspend fun updateProduct(token: String, id: Int, request: ProductRequest): Response<Void> {
        return api.updateProduct("Bearer $token", id, request)
    }

}
