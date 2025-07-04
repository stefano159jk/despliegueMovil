package com.example.capachicaa.modules.products.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capachicaa.modules.categories.controller.CategoryController
import com.example.capachicaa.modules.categories.model.Category
import com.example.capachicaa.modules.products.controller.ProductController
import com.example.capachicaa.modules.products.model.ProductRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarProductoScreen(
    navController: NavController,
    token: String,
    productId: Int
) {
    val scope = rememberCoroutineScope()
    val controller = remember { ProductController() }
    val categoryController = remember { CategoryController() }

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }

    var categorias by remember { mutableStateOf(listOf<Category>()) }
    var selectedCategoria by remember { mutableStateOf<Category?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(true) {
        try {
            val catResponse = categoryController.getAll()
            if (catResponse.isSuccessful) {
                categorias = catResponse.body() ?: emptyList()
            }

            val prodResponse = controller.getProductById(token, productId)
            if (prodResponse.isSuccessful) {
                prodResponse.body()?.let { product ->
                    nombre = product.name
                    descripcion = product.description
                    precio = product.price.toString()
                    stock = product.stock.toString()
                    duracion = product.duration

                    val catId = product.category_ids?.firstOrNull()
                    if (catId != null) {
                        selectedCategoria = categorias.find { it.id == catId }
                    }
                }
            } else {
                errorMsg = "Error al cargar producto"
            }
        } catch (e: Exception) {
            errorMsg = e.message
        } finally {
            isLoading = false
        }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Editar Producto") },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null)
                        }
                    }
                )
            }
        ) { paddingValues ->
            ElevatedCard(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.tertiary
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Formulario de edición",
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.titleMedium
                    )
                }

                Column(Modifier.padding(16.dp)) {
                    listOf(
                        "Nombre del Producto" to nombre,
                        "Descripción" to descripcion,
                        "Precio" to precio,
                        "Stock" to stock,
                        "Duración (ej: 2h)" to duracion
                    ).forEachIndexed { i, pair ->
                        OutlinedTextField(
                            value = pair.second,
                            onValueChange = {
                                when (i) {
                                    0 -> nombre = it
                                    1 -> descripcion = it
                                    2 -> precio = it
                                    3 -> stock = it
                                    4 -> duracion = it
                                }
                            },
                            label = { Text(pair.first) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        )
                    }

                    Text("Categoría", style = MaterialTheme.typography.labelLarge)
                    Box {
                        OutlinedTextField(
                            value = selectedCategoria?.name ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Seleccionar Categoría") },
                            trailingIcon = {
                                IconButton(onClick = { isDropdownExpanded = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            categorias.forEach { categoria ->
                                DropdownMenuItem(
                                    text = { Text(categoria.name) },
                                    onClick = {
                                        selectedCategoria = categoria
                                        isDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    errorMsg?.let {
                        Spacer(Modifier.height(8.dp))
                        Text("Error: $it", color = MaterialTheme.colorScheme.error)
                    }

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = {
                            scope.launch {
                                val req = ProductRequest(
                                    entrepreneur_id = 0,
                                    name = nombre,
                                    description = descripcion,
                                    price = precio.toDoubleOrNull() ?: 0.0,
                                    stock = stock.toIntOrNull() ?: 0,
                                    duration = duracion,
                                    category_ids = listOf(selectedCategoria?.id ?: 1)
                                )

                                val response = controller.updateProduct(token, productId, req)
                                if (response.isSuccessful) {
                                    navController.popBackStack()
                                } else {
                                    errorMsg = "Error al actualizar producto"
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Guardar Cambios")
                    }
                }
            }
        }
    }
}
