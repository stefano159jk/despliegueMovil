package com.example.capachicaa.modules.categories.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.modules.categories.controller.CategoryController
import com.example.capachicaa.modules.categories.model.CategoryRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaCreateScreen(navController: NavController) {

    val ctrl = remember { CategoryController() }
    val scope = rememberCoroutineScope()

    var nombre by remember { mutableStateOf("") }
    var icono by remember { mutableStateOf("bi-geo-alt") }
    var saving by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("➕ Crear Categoría Turística") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        ElevatedCard(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {

            // Header visual superior
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Text(
                    "Define una nueva categoría turística",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la Categoría") },
                    placeholder = { Text("Ej: Aventura, Ecoturismo") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = icono,
                    onValueChange = { icono = it },
                    label = { Text("Nombre del ícono (Bootstrap Icons)") },
                    supportingText = { Text("Ej: bi-geo-alt, bi-camera, bi-bicycle") },
                    modifier = Modifier.fillMaxWidth()
                )

                error?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = {
                        if (nombre.isBlank()) {
                            error = "El nombre es obligatorio"
                            return@Button
                        }
                        saving = true
                        error = null
                        scope.launch {
                            val resp = ctrl.create(CategoryRequest(nombre, icono))
                            saving = false
                            if (resp.isSuccessful) navController.popBackStack()
                            else error = "Error ${resp.code()}"
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (saving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(6.dp))
                        Text("Guardar")
                    }
                }
            }
        }
    }
}
