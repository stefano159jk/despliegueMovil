package com.example.capachicaa.modules.place.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capachicaa.modules.place.controller.PlaceController
import com.example.capachicaa.modules.place.request.PlaceRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceEditScreen(navController: NavController, placeId: Int) {
    val controller = remember { PlaceController() }
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }
    var activity by remember { mutableStateOf("") }
    var categoryId by remember { mutableStateOf("") }

    var loading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    // Cargar datos actuales del lugar
    LaunchedEffect(placeId) {
        val response = controller.getAll()
        if (response.isSuccessful) {
            val lugar = response.body()?.find { it.id == placeId }
            if (lugar != null) {
                name = lugar.name ?: ""
                description = lugar.description ?: ""
                latitude = lugar.latitude?.toString() ?: ""
                longitude = lugar.longitude?.toString() ?: ""
                activity = lugar.activity ?: ""
                categoryId = lugar.categoryId?.toString() ?: ""
            } else {
                error = "Lugar no encontrado"
            }
        } else {
            error = "Error al cargar datos (${response.code()})"
        }
        loading = false
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Editar Lugar Turístico") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->

        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del lugar") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitud") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitud") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = activity,
                onValueChange = { activity = it },
                label = { Text("Actividad sugerida") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = categoryId,
                onValueChange = { categoryId = it },
                label = { Text("ID Categoría") },
                modifier = Modifier.fillMaxWidth()
            )

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Button(
                onClick = {
                    if (name.isBlank() || description.isBlank() || latitude.isBlank() || longitude.isBlank() || categoryId.isBlank()) {
                        error = "Todos los campos son obligatorios"
                        return@Button
                    }

                    val lat = latitude.toDoubleOrNull()
                    val lng = longitude.toDoubleOrNull()
                    val catId = categoryId.toIntOrNull()

                    if (lat == null || lng == null || catId == null) {
                        error = "Latitud, longitud y categoría deben ser numéricos"
                        return@Button
                    }

                    saving = true
                    scope.launch {
                        val result = controller.update(
                            placeId,
                            PlaceRequest(name, description, lat, lng, activity, catId)
                        )
                        saving = false
                        if (result.isSuccessful) {
                            navController.popBackStack()
                        } else {
                            error = "Error al guardar (${result.code()})"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (saving) {
                    CircularProgressIndicator(strokeWidth = 2.dp, modifier = Modifier.size(20.dp))
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(6.dp))
                    Text("Guardar cambios")
                }
            }
        }
    }
}
