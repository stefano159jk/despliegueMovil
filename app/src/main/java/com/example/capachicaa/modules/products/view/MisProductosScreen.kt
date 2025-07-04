package com.example.capachicaa.modules.products.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capachicaa.modules.products.controller.ProductController
import com.example.capachicaa.modules.products.model.Product
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisProductosScreen(
    token: String,
    navController: NavController // Asegúrate de pasarlo desde MainActivity
) {
    val controller = remember { ProductController() }
    val scope = rememberCoroutineScope()

    var productos by remember { mutableStateOf(listOf<Product>()) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    var productoAEliminar by remember { mutableStateOf<Product?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    // Cargar productos al iniciar
    LaunchedEffect(true) {
        isLoading = true
        try {
            val response = controller.getMyProducts(token)
            if (response.isSuccessful) {
                productos = response.body() ?: emptyList()
            } else {
                errorMsg = "Error ${response.code()}"
            }
        } catch (e: Exception) {
            errorMsg = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Productos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(Modifier.padding(paddingValues).padding(16.dp)) {
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (errorMsg != null) {
                Text("Error: $errorMsg", color = MaterialTheme.colorScheme.error)
            } else {
                LazyColumn {
                    items(productos) { producto ->
                        ElevatedCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(
                                                    MaterialTheme.colorScheme.primary,
                                                    MaterialTheme.colorScheme.tertiary
                                                )
                                            )
                                        )
                                        .padding(12.dp)
                                ) {
                                    Text(
                                        text = producto.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }

                                Column(Modifier.padding(16.dp)) {
                                    Text("S/. ${producto.price}", style = MaterialTheme.typography.bodyLarge)
                                    Text(producto.description, style = MaterialTheme.typography.bodyMedium)

                                    Spacer(Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End
                                    ) {
                                        IconButton(onClick = {
                                            navController.navigate("editar_producto/${producto.id}")
                                        }) {
                                            Icon(Icons.Default.Edit, contentDescription = "Editar")
                                        }
                                        IconButton(onClick = {
                                            productoAEliminar = producto
                                            showConfirmDialog = true
                                        }) {
                                            Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showConfirmDialog && productoAEliminar != null) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirmar eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar este producto?") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        productoAEliminar?.let {
                            val deleteResp = controller.deleteProduct(token, it.id)
                            if (deleteResp.isSuccessful) {
                                productos = productos.filterNot { p -> p.id == it.id }
                            }
                        }
                        showConfirmDialog = false
                    }
                }) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

