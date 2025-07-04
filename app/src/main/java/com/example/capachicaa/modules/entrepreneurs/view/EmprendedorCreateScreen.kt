package com.example.capachicaa.modules.entrepreneurs.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.modules.entrepreneurs.controller.EntrepreneurController
import com.example.capachicaa.modules.entrepreneurs.model.EntrepreneurRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendedorCreateScreen(navController: NavController) {
    val ctrl = remember { EntrepreneurController() }
    val scope = rememberCoroutineScope()

    // Campos del formulario
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var associationId by remember { mutableStateOf("") }
    var placeId by remember { mutableStateOf("") }
    var lat by remember { mutableStateOf("") }
    var lng by remember { mutableStateOf("") }
    var categoriesTxt by remember { mutableStateOf("") }

    var saving by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Emprendedor Turístico") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (username.isBlank() || password.isBlank() || businessName.isBlank()) {
                        errorMsg = "Completa los campos obligatorios."
                        return@ExtendedFloatingActionButton
                    }

                    saving = true
                    errorMsg = null

                    scope.launch {
                        val request = EntrepreneurRequest(
                            username = username,
                            email = email,
                            password = password,
                            businessName = businessName,
                            phone = phone,
                            district = district,
                            description = description.ifBlank { null },
                            associationId = associationId.toIntOrNull(),
                            placeId = placeId.toIntOrNull(),
                            lat = lat.toDoubleOrNull(),
                            lng = lng.toDoubleOrNull(),
                            categories = categoriesTxt.split(',').mapNotNull { it.trim().toIntOrNull() },
                            status = "inactivo"
                        )

                        val resp = ctrl.create(request)
                        saving = false

                        if (resp.isSuccessful) navController.popBackStack()
                        else errorMsg = "Error ${'$'}{resp.code()}"
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                if (saving) CircularProgressIndicator(strokeWidth = 2.dp)
                else {
                    Icon(Icons.Default.Save, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar")
                }
            }
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Section(title = "Datos de Acceso") {
                Field("Usuario", username) { username = it }
                Field("Email", email) { email = it }
                Field("Contraseña", password) { password = it }
            }
            Section(title = "Información del Negocio") {
                Field("Nombre del Negocio", businessName) { businessName = it }
                Field("Teléfono", phone) { phone = it }
                Field("Distrito", district) { district = it }
                Field("Descripción", description) { description = it }
            }
            Section(title = "Ubicación y Asociación") {
                Field("ID Asociación", associationId) { associationId = it }
                Field("ID Lugar Turístico", placeId) { placeId = it }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Field("Latitud", lat, Modifier.weight(1f)) { lat = it }
                    Field("Longitud", lng, Modifier.weight(1f)) { lng = it }
                }
            }
            Section(title = "Categorías") {
                Text("(IDs separados por coma)", fontSize = 12.sp)
                Field("Categorías", categoriesTxt) { categoriesTxt = it }
            }
            errorMsg?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium)
        content()
    }
}

@Composable
private fun Field(label: String, value: String, modifier: Modifier = Modifier.fillMaxWidth(), onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
    )
}
