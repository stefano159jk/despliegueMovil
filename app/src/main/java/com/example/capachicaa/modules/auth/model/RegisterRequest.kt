package com.example.capachicaa.modules.auth.model

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String,
    val password_confirmation: String,
    val role: String
)
