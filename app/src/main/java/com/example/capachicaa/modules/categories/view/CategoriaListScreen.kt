package com.example.capachicaa.modules.categories.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.modules.categories.controller.CategoryController
import com.example.capachicaa.modules.categories.model.Category
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaListScreen(navController: NavController) {

    val ctrl   = remember { CategoryController() }
    val scope  = rememberCoroutineScope()

    var categorias by remember { mutableStateOf<List<Category>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    /* ---- Cargar desde backend ---- */
    LaunchedEffect(Unit) {
        val resp = ctrl.getAll()
        isLoading = false
        if (resp.isSuccessful) categorias = resp.body() ?: emptyList()
        else errorMsg = "Error ${resp.code()}"
    }

    Column(modifier = Modifier.fillMaxSize()) {

        /* Cabecera azul */
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color(0xFF0D6EFD))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { navController.popBackStack() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.ArrowBack, null, tint = Color.Black)
                Spacer(Modifier.width(4.dp))
                Text("Panel Principal", color = Color.Black, fontSize = 12.sp)
            }

            Text(
                "📂 Gestionar Categorías de Turismo",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )

            Button(
                onClick = { navController.navigate("categoriaCreate") },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Icon(Icons.Default.Add, null, tint = Color.Black)
                Spacer(Modifier.width(4.dp))
                Text("+ Nueva", color = Color.Black, fontSize = 12.sp)
            }
        }

        Text(
            "Administra las secciones de tu oferta turística.",
            Modifier.padding(start = 16.dp, top = 8.dp),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(8.dp))

        /* Cabecera de tabla */
        Row(
            Modifier
                .fillMaxWidth()
                .background(Color(0xFFE3F2FD))
                .padding(vertical = 8.dp, horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("#", fontWeight = FontWeight.Bold)
            Text("Nombre de la Categoría", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
            Text("Acciones", fontWeight = FontWeight.Bold)
        }

        /* Contenido */
        when {
            isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }

            errorMsg != null -> Text(errorMsg ?: "", color = MaterialTheme.colorScheme.error)

            categorias.isEmpty() -> Text(
                "No hay categorías registradas.",
                Modifier.padding(16.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            else -> LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(categorias) { cat ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(cat.id.toString())
                        Text(cat.name, Modifier.weight(1f))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            OutlinedButton(
                                onClick = { navController.navigate("categoriaEdit/${cat.id}") },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Edit, null)
                                Spacer(Modifier.width(4.dp))
                                Text("Editar", fontSize = 12.sp)
                            }
                            OutlinedButton(
                                onClick = {
                                    scope.launch {
                                        ctrl.delete(cat.id)
                                        categorias = categorias.filterNot { it.id == cat.id }
                                    }
                                },
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Delete, null, tint = Color.Red)
                                Spacer(Modifier.width(4.dp))
                                Text("Eliminar", fontSize = 12.sp, color = Color.Red)
                            }
                        }
                    }
                    Divider()
                }
            }
        }

        /* Total */
        Text(
            "Total de categoría: ${categorias.size}",
            Modifier.padding(16.dp),
            fontSize = 13.sp
        )
    }
}
