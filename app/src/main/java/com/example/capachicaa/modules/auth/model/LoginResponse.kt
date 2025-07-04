package com.example.capachicaa.modules.auth.model

data class LoginResponse(
    val message: String,
    val token: String,
    val user: User,
    val roles: List<String>,
    val entrepreneur_id: Int?
)

