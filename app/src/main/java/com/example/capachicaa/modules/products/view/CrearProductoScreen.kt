package com.example.capachicaa.modules.products.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capachicaa.modules.categories.controller.CategoryController
import com.example.capachicaa.modules.categories.model.Category
import com.example.capachicaa.modules.products.controller.ProductController
import com.example.capachicaa.modules.products.model.ProductRequest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearProductoScreen(
    navController: NavController,
    token: String,
    entrepreneurId: Int
) {
    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var stock by remember { mutableStateOf("") }
    var duracion by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    var categorias by remember { mutableStateOf(listOf<Category>()) }
    var selectedCategoria by remember { mutableStateOf<Category?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    val controller = remember { ProductController() }
    val categoryController = remember { CategoryController() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(true) {
        try {
            val response = categoryController.getAll()
            if (response.isSuccessful) {
                categorias = response.body() ?: emptyList()
            }
        } catch (_: Exception) {}
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Producto") },
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
                            listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.tertiary
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                Text("Registrar nuevo producto", color = MaterialTheme.colorScheme.onPrimary)
            }

            Column(Modifier.padding(16.dp)) {
                OutlinedTextField(nombre, { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(descripcion, { descripcion = it }, label = { Text("Descripción") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(precio, { precio = it }, label = { Text("Precio") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(stock, { stock = it }, label = { Text("Stock") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(duracion, { duracion = it }, label = { Text("Duración (ej: 2h)") }, modifier = Modifier.fillMaxWidth())

                Spacer(Modifier.height(8.dp))
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

                Spacer(Modifier.height(16.dp))
                errorMsg?.let {
                    Text("Error: $it", color = MaterialTheme.colorScheme.error)
                }

                Button(
                    onClick = {
                        if (nombre.isBlank() || descripcion.isBlank() || precio.isBlank() || stock.isBlank() || duracion.isBlank() || selectedCategoria == null) {
                            errorMsg = "Todos los campos son obligatorios."
                            return@Button
                        }

                        val request = ProductRequest(
                            entrepreneur_id = entrepreneurId,
                            name = nombre,
                            description = descripcion,
                            price = precio.toDouble(),
                            stock = stock.toInt(),
                            duration = duracion,
                            category_ids = listOf(selectedCategoria!!.id)
                        )

                        isLoading = true
                        scope.launch {
                            try {
                                val response = controller.createProduct(token, request)
                                isLoading = false
                                if (response.isSuccessful) {
                                    navController.popBackStack()
                                } else {
                                    errorMsg = response.errorBody()?.string() ?: "Error al crear"
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
                    enabled = !isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (isLoading)
                        CircularProgressIndicator(modifier = Modifier.size(18.dp))
                    else
                        Text("Registrar Producto")
                }
            }
        }
    }
}
