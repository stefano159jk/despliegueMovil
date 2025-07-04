package com.example.capachicaa.modules.auth.controller

import android.content.Context
import android.util.Log
import com.example.capachicaa.modules.dashboard.controller.SessionController
import com.example.capachicaa.modules.auth.model.*
import com.example.capachicaa.modules.auth.service.AuthService
import com.example.capachicaa.modules.core.network.ApiClient
import retrofit2.Response

class AuthController(private val context: Context) {

    private val api = ApiClient.retrofit.create(AuthService::class.java)
    private val session = SessionController(context)

    /* ---------- LOGIN ---------- */
    suspend fun login(email: String, password: String): LoginResponse? {
        return try {
            val response = api.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                response.body()?.also {
                    val role = it.roles.firstOrNull() ?: "cliente"
                    session.saveSession(
                        token = it.token,
                        name  = it.user.name,
                        role  = role
                    )
                }
            } else {
                Log.e("LOGIN", "Error: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("LOGIN", "Excepción: ${e.message}")
            null
        }
    }

    /* ---------- REGISTER ---------- */
    suspend fun register(
        name: String,
        email: String,
        password: String,
        role: String
    ): LoginResponse? {
        val request = RegisterRequest(
            name  = name,
            email = email,
            password = password,
            password_confirmation = password,
            role  = role
        )
        return try {
            val response = api.register(request)
            if (response.isSuccessful) {
                response.body()?.also {
                    val roleName = it.roles.firstOrNull() ?: "desconocido"
                    session.saveSession(
                        token = it.token,
                        name  = it.user.name,
                        role  = roleName
                    )
                }
            } else {
                Log.e("REGISTER", "Error: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("REGISTER", "Excepción: ${e.message}")
            null
        }
    }

    /* ---------- PERFIL ---------- */
    suspend fun getProfile(): Response<User> = api.getProfile()




    /* ---------- LOGOUT (única implementación) ---------- */
    suspend fun logout(): Boolean {
        return try {
            val response = api.logout()   // POST /logout
            session.clearSession()        // limpia prefs locales
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("LOGOUT", "Excepción: ${e.message}")
            session.clearSession()
            false
        }
    }


    /* ---------- UTILIDAD ---------- */
    fun getToken(): String? = session.getToken()

    fun saveToken(context: Context, token: String) {
        val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        prefs.edit().putString("token", token).apply()
    }

}