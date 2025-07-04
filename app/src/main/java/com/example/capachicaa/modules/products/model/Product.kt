package com.example.capachicaa.modules.products.model

import com.example.capachicaa.modules.categories.model.Category

data class Product(
    val id: Int,
    val entrepreneur_id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val duration: String,
    val category_ids: List<Int>,  // Puedes mantener esto
    val categories: List<Category>  // ← ESTA LÍNEA ES CLAVE PARA LA EDICIÓN
)
