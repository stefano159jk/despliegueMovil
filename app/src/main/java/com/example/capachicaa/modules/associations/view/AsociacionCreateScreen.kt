package com.example.capachicaa.modules.associations.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.modules.admin.controller.AssociationController
import com.example.capachicaa.modules.associations.model.AssociationRequest
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsociacionCreateScreen(navController: NavController) {

    var nombre by remember { mutableStateOf("") }
    var region by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()
    val controller = remember { AssociationController() }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Nueva Asociación") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->

        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .padding(16.dp)
        ) {

            /* Header */
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
                    "Gestión de grupos asociados",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
            }

            Column(Modifier.padding(16.dp)) {

                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre de la Asociación") },
                    placeholder = { Text("Ej: Asociación de Guías Turísticos…") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = region,
                    onValueChange = { region = it },
                    label = { Text("Región") },
                    placeholder = { Text("Cusco, Arequipa, Puno…") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = descripcion,
                    onValueChange = { descripcion = it },
                    label = { Text("Descripción") },
                    placeholder = { Text("Servicios y actividades principales…") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
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

                        errorMsg = null
                        isLoading = true

                        val request = AssociationRequest(
                            name = nombre,
                            region = region,
                            description = descripcion
                        )

                        scope.launch {
                            try {
                                val resp = controller.createAssociation(request)
                                isLoading = false
                                if (resp.isSuccessful) {
                                    navController.previousBackStackEntry
                                        ?.savedStateHandle
                                        ?.set("association_added", true)

                                    navController.popBackStack()
                                } else {
                                    val errorBody = resp.errorBody()?.string()
                                    errorMsg = errorBody ?: "Error ${resp.code()}: No se pudo registrar"
                                }
                            } catch (e: IOException) {
                                isLoading = false
                                errorMsg = "Error de red: ${e.message}"
                            } catch (e: HttpException) {
                                isLoading = false
                                errorMsg = "Error del servidor: ${e.message}"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Icon(Icons.Default.Save, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Registrar Asociación")
                    }
                }
            }
        }
    }
}
