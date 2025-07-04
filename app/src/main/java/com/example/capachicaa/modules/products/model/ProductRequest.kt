package com.example.capachicaa.modules.products.model

data class ProductRequest(
    val entrepreneur_id: Int,
    val name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val duration: String,
    val category_ids: List<Int>,  // ← requerido
    val place_id: Int? = null,
    val main_image: String? = null,
    val images: List<String>? = null
)

