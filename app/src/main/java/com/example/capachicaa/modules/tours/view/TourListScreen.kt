package com.example.capachicaa.modules.tours.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.capachicaa.modules.tours.controller.TourController
import com.example.capachicaa.modules.tours.model.Tour
import kotlinx.coroutines.launch

@Composable
fun TourListScreen(navController: NavController) {

    /* ─────── State ─────── */
    var tours           by remember { mutableStateOf<List<Tour>>(emptyList()) }
    var isLoading       by remember { mutableStateOf(false) }
    var errorMessage    by remember { mutableStateOf<String?>(null) }
    var selectedTour    by remember { mutableStateOf<Tour?>(null) }
    var query           by remember { mutableStateOf("") }

    /* ─────── Controller & scope ─────── */
    val controller = remember { TourController() }
    val scope      = rememberCoroutineScope()

    /* ─────── Load tours ─────── */
    fun fetchTours() {
        scope.launch {
            isLoading = true
            val resp = controller.all()
            isLoading = false
            if (resp.isSuccessful) {
                tours = resp.body() ?: emptyList()
                errorMessage = null
            } else {
                errorMessage = "Error ${resp.code()}: ${resp.message()}"
            }
        }
    }
    /* Cargar al entrar */
    LaunchedEffect(Unit) { fetchTours() }

    /* Filtro búsqueda */
    val filteredTours = tours.filter {
        it.name.contains(query, true) || it.description.contains(query, true)
    }

    /* ─────── UI ─────── */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        /* Encabezado */
        Text("📋 Lista de Tours", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text("Administra los paquetes turísticos de tu organización",
            fontSize = 14.sp, color = Color.Gray)

        Spacer(Modifier.height(12.dp))

        /* Buscar */
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = { Text("Buscar por nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        /* Acciones */
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.navigate("crearTour") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE7E1D8))
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black)
                Spacer(Modifier.width(8.dp))
                Text("Nuevo Tour", color = Color.Black)
            }
            IconButton(onClick = { fetchTours() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refrescar")
            }
        }

        Spacer(Modifier.height(12.dp))

        /* Cabecera de tabla */
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFD2C1A0))
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Nombre",  Modifier.weight(1f),   fontWeight = FontWeight.Bold)
            Text("Precio",  Modifier.weight(0.5f), fontWeight = FontWeight.Bold)
            Text("Acciones",Modifier.weight(1f),   fontWeight = FontWeight.Bold)
        }

        /* Contenido */
        when {
            isLoading -> {
                Spacer(Modifier.height(24.dp))
                CircularProgressIndicator()
            }
            errorMessage != null -> {
                Spacer(Modifier.height(24.dp))
                Text(errorMessage ?: "", color = Color.Red)
            }
            filteredTours.isEmpty() -> {
                Spacer(Modifier.height(24.dp))
                Text("No se encontraron tours", color = Color.Gray)
            }
            else -> {
                LazyColumn {
                    items(filteredTours) { tour ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Nombre e imagen
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = tour.image,
                                    contentDescription = null,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(tour.name, fontWeight = FontWeight.SemiBold)
                            }

                            // Precio alineado
                            Text(
                                text = "S/ ${tour.price}",
                                modifier = Modifier
                                    .weight(0.5f)
                                    .padding(start = 8.dp),
                                fontSize = 14.sp
                            )

                            // Acciones
                            Row(
                                modifier = Modifier
                                    .weight(0.5f)
                                    .wrapContentWidth(Alignment.End),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                IconButton(onClick = {
                                    navController.currentBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("tour${tour.id}", tour)
                                    navController.navigate("editarTour/${tour.id}")
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                                }

                                IconButton(onClick = { selectedTour = tour }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                                }
                            }
                        }

                        Divider(thickness = 0.5.dp)
                    }
                }
            }
        }

        /* Diálogo de confirmación */
        selectedTour?.let { t ->
            AlertDialog(
                onDismissRequest = { selectedTour = null },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            controller.delete(t.id)
                            selectedTour = null
                            fetchTours()
                        }
                    }) { Text("Eliminar") }
                },
                dismissButton = {
                    TextButton(onClick = { selectedTour = null }) {
                        Text("Cancelar")
                    }
                },
                title = { Text("¿Eliminar \"${t.name}\"?") },
                text  = { Text("Esta acción no se puede deshacer.") }
            )
        }
    }
}
