package com.example.capachicaa.modules.categories.controller

import com.example.capachicaa.modules.categories.model.Category
import com.example.capachicaa.modules.categories.model.CategoryRequest
import com.example.capachicaa.modules.categories.service.CategoryService
import com.example.capachicaa.modules.core.network.ApiClient
import retrofit2.Response

class CategoryController {
    private val api = ApiClient.retrofit.create(CategoryService::class.java)

    suspend fun getAll(): Response<List<Category>>            = api.getCategories()
    suspend fun getById(id: Int): Response<Category>          = api.getCategory(id)
    suspend fun create(req: CategoryRequest): Response<Void>  = api.createCategory(req)
    suspend fun update(id: Int, req: CategoryRequest): Response<Void> = api.updateCategory(id, req)
    suspend fun delete(id: Int): Response<Void>               = api.deleteCategory(id)

}
