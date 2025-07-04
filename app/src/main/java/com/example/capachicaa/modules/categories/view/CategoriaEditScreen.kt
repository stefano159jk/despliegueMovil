package com.example.capachicaa.modules.categories.view

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
import com.example.capachicaa.modules.categories.controller.CategoryController
import com.example.capachicaa.modules.categories.model.CategoryRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaEditScreen(navController: NavController, categoryId: Int) {

    val ctrl = remember { CategoryController() }
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var icono  by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var saving  by remember { mutableStateOf(false) }
    var error   by remember { mutableStateOf<String?>(null) }

    /* --- Cargar info --- */
    LaunchedEffect(categoryId) {
        val resp = ctrl.getById(categoryId)
        loading = false
        if (resp.isSuccessful) {
            resp.body()?.let { cat ->
                nombre = cat.name
                icono  = cat.icon
            } ?: run { error = "No encontrada" }
        } else error = "Error ${resp.code()}"
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Editar Categoría") },
                navigationIcon = {

                }
            )
        }
    ) { pad ->
        if (loading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            Modifier
                .fillMaxSize()
                .padding(pad)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = icono,
                onValueChange = { icono = it },
                label = { Text("Ícono") },
                modifier = Modifier.fillMaxWidth()
            )

            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Button(
                onClick = {
                    if (nombre.isBlank()) {
                        error = "El nombre es obligatorio"; return@Button
                    }
                    saving = true
                    error = null
                    scope.launch {
                        val resp = ctrl.update(categoryId, CategoryRequest(nombre, icono))
                        saving = false
                        if (resp.isSuccessful) navController.popBackStack()
                        else error = "Error ${resp.code()}"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (saving) CircularProgressIndicator(strokeWidth = 2.dp)
                else {
                    Icon(Icons.Default.Save, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Guardar cambios")
                }
            }
        }
    }
}
