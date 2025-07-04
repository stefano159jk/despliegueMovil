package com.example.capachicaa.modules.auth.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val created_at: String
)