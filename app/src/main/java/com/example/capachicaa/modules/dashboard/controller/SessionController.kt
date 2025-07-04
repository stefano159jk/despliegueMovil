package com.example.capachicaa.modules.dashboard.controller

import android.content.Context

class SessionController(private val context: Context) {
    private val prefs = context.getSharedPreferences("auth", Context.MODE_PRIVATE)

    // Guardar todos los datos de sesión
    fun saveSession(token: String, name: String, role: String) {
        prefs.edit()
            .putString("token", token)
            .putString("name", name)
            .putString("role", role)
            .apply()
    }

    // Métodos individuales para guardar y obtener campos por separado
    fun saveToken(token: String) {
        prefs.edit().putString("token", token).apply()
    }

    fun getToken(): String? = prefs.getString("token", null)

    fun saveName(name: String) {
        prefs.edit().putString("name", name).apply()
    }

    fun getName(): String? = prefs.getString("name", "Usuario")

    fun saveRole(role: String) {
        prefs.edit().putString("role", role).apply()
    }

    fun getRole(): String? = prefs.getString("role", "desconocido")

    // Limpiar sesión
    fun clearSession() {
        prefs.edit().clear().apply()
    }
    fun getEntrepreneurId(): Int? {
        return prefs.getInt("entrepreneur_id", -1).takeIf { it != -1 }
    }
    fun saveEntrepreneurId(id: Int) {
        prefs.edit().putInt("entrepreneur_id", id).apply()
    }


}
