package com.example.capachicaa.modules.associations.view

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.modules.admin.controller.AssociationController
import com.example.capachicaa.modules.admin.model.Association
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AsociacionesListScreen(navController: NavController) {

    val controller = remember { AssociationController() }
    var asociaciones by remember { mutableStateOf<List<Association>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var toDelete by remember { mutableStateOf<Association?>(null) }

    val scope = rememberCoroutineScope()

    // Cargar datos desde el backend
    LaunchedEffect(Unit) {
        scope.launch {
            val resp = controller.getAllAssociations()
            isLoading = false
            if (resp.isSuccessful) {
                asociaciones = resp.body() ?: emptyList()
            } else {
                errorMsg = "Error ${resp.code()}: ${resp.message()}"
            }
        }
    }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("👥 Gestión de Asociaciones") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    FilledTonalButton(onClick = {
                        navController.navigate("adminAsociacionCreate")
                    }) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(4.dp))
                        Text("Nueva")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(12.dp)
        ) {
            Text(
                "Lista de grupos y comunidades locales",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(12.dp))

            // Cabecera simple
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.tertiaryContainer
                            )
                        )
                    )
                    .padding(vertical = 8.dp, horizontal = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Nombre", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Región", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text("Acciones", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }

            Spacer(Modifier.height(8.dp))

            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }

                errorMsg != null -> Text(errorMsg ?: "", color = MaterialTheme.colorScheme.error)

                asociaciones.isEmpty() -> Text(
                    "No se encontraron asociaciones",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(asociaciones) { item ->
                        AssociationRow(
                            item = item,
                            onEdit = {
                                navController.navigate("adminAsociacionEdit/${item.id}")
                            },
                            onDelete = {
                                toDelete = item
                            }
                        )
                    }
                }
            }
        }
    }

    // Diálogo de confirmación
    toDelete?.let { assoc ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Eliminar \"${assoc.name}\"") },
            text = { Text("¿Deseas eliminar esta asociación? Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        controller.deleteAssociation(assoc.id)
                        asociaciones = asociaciones.filterNot { it.id == assoc.id }
                        toDelete = null
                    }
                }) { Text("Eliminar") }
            },
            dismissButton = {
                TextButton(onClick = { toDelete = null }) { Text("Cancelar") }
            }
        )
    }
}

@Composable
private fun AssociationRow(
    item: Association,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("📌 ${item.name}", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text("🌎 Región: ${item.region}", fontSize = 13.sp)
            Spacer(Modifier.height(4.dp))
            Text("📝 ${item.description}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
