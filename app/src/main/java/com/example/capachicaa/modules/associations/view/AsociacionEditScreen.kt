package com.example.capachicaa.modules.associations.view

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capachicaa.modules.admin.controller.AssociationController
import com.example.capachicaa.modules.associations.model.AssociationRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsociacionEditScreen(navController: NavController, assocId: Int) {
    val controller = remember { AssociationController() }
    var nombre by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }
    var saving by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    // Cargar datos
    LaunchedEffect(assocId) {
        val resp = controller.getAssociationById(assocId)
        isLoading = false
        if (resp.isSuccessful) {
            resp.body()?.let { assoc ->
                nombre = assoc.name
                region = assoc.region
                descripcion = assoc.description
            } ?: run {
                errorMsg = "No se encontró la asociación"
            }
        } else {
            errorMsg = "Error ${resp.code()}"
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Editar Asociación") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp)
        ) {
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
                Text("Actualiza los datos", color = MaterialTheme.colorScheme.onPrimary)
            }

            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    label = { Text("Región") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
                Spacer(Modifier.height(16.dp))

                errorMsg?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (nombre.isBlank() || region.isBlank() || descripcion.isBlank()) {
                            errorMsg = "Todos los campos son obligatorios."
                            return@Button
                        }

                        saving = true
                        val request = AssociationRequest(nombre, region, descripcion)
                        scope.launch {
                            val resp = controller.updateAssociation(assocId, request)
                            saving = false
                            if (resp.isSuccessful) {
                                navController.popBackStack()
                            } else {
                                errorMsg = "Error al guardar"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (saving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(Icons.Default.Save, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Guardar cambios")
                    }
                }
            }
        }
    }
}
