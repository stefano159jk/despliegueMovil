package com.example.capachicaa.modules.place.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.modules.core.network.ApiClient
import com.example.capachicaa.modules.place.controller.PlaceController
import com.example.capachicaa.modules.place.request.PlaceRequest
import com.example.capachicaa.modules.categories.model.Category
import com.example.capachicaa.modules.categories.service.CategoryService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceCreateScreen(navController: NavController) {
    val placeCtrl   = remember { PlaceController() }
    val scope       = rememberCoroutineScope()

    var name        by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var latitude    by remember { mutableStateOf("") }
    var longitude   by remember { mutableStateOf("") }
    var activity    by remember { mutableStateOf("") }

    var categorias          by remember { mutableStateOf<List<Category>>(emptyList()) }
    var categoriaExpandida  by remember { mutableStateOf(false) }
    var selectedCategory    by remember { mutableStateOf<Category?>(null) }

    var saving      by remember { mutableStateOf(false) }
    var error       by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val service = ApiClient.retrofit.create(CategoryService::class.java)
            val resp = service.getCategories()
            if (resp.isSuccessful) categorias = resp.body() ?: emptyList()
            else error = "Error al cargar categorías (${resp.code()})"
        } catch (e: Exception) {
            error = "Error de red: ${e.localizedMessage}"
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Agregar Lugar Turístico") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            Text("Formulario de creación", fontSize = 18.sp)

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

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitud") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitud") },
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = activity,
                onValueChange = { activity = it },
                label = { Text("Actividad sugerida") },
                modifier = Modifier.fillMaxWidth()
            )

            ExposedDropdownMenuBox(
                expanded = categoriaExpandida,
                onExpandedChange = { categoriaExpandida = !categoriaExpandida },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = selectedCategory?.name ?: "",
                    onValueChange = {},
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpandida) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )

                ExposedDropdownMenu(
                    expanded = categoriaExpandida,
                    onDismissRequest = { categoriaExpandida = false }
                ) {
                    categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.name) },
                            onClick = {
                                selectedCategory   = cat
                                categoriaExpandida = false
                            }
                        )
                    }
                }
            }

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = {
                    error = null

                    if (name.isBlank() || description.isBlank()
                        || latitude.isBlank() || longitude.isBlank()
                        || selectedCategory == null
                    ) {
                        error = "Todos los campos son obligatorios"
                        return@Button
                    }

                    val lat   = latitude.toDoubleOrNull()
                    val lng   = longitude.toDoubleOrNull()
                    val catId = selectedCategory?.id

                    if (lat == null || lng == null) {
                        error = "Latitud y longitud deben ser numéricas"
                        return@Button
                    }

                    saving = true
                    scope.launch {
                        val resp = placeCtrl.create(
                            PlaceRequest(
                                name        = name,
                                description = description,
                                latitude    = lat,
                                longitude   = lng,
                                activity    = activity,
                                categoryId  = catId!!
                            )
                        )
                        saving = false
                        if (resp.isSuccessful) navController.popBackStack()
                        else error = "Error al guardar: ${resp.code()}"
                    }
                },
                enabled = !saving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (saving) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(Icons.Default.Save, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Guardar")
                }
            }
        }
    }
}
