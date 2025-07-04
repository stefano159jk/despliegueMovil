/*  Ruta:  com/example/capachicaa/modules/place/view/PlaceListScreen.kt  */

package com.example.capachicaa.modules.place.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.modules.admin.navigation.AdminRoutes
import com.example.capachicaa.modules.place.controller.PlaceController
import com.example.capachicaa.modules.place.model.Place
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceListScreen(navController: NavController) {

    /* ---------- Estados & controller ---------- */
    val controller = remember { PlaceController() }
    val scope      = rememberCoroutineScope()

    var places     by remember { mutableStateOf<List<Place>>(emptyList()) }
    var loading    by remember { mutableStateOf(true) }
    var errorMsg   by remember { mutableStateOf<String?>(null) }
    var toDelete   by remember { mutableStateOf<Place?>(null) }

    /* ---------- Carga inicial ---------- */
    LaunchedEffect(Unit) {
        loading = true
        val resp = controller.getAll()
        loading = false
        if (resp.isSuccessful) places = resp.body() ?: emptyList()
        else errorMsg = "Error ${resp.code()}"
    }

    /* ---------- UI ---------- */
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("📍 Gestión de Lugares Turísticos") },
                actions = {
                    FilledTonalButton(onClick = { navController.navigate(AdminRoutes.DESTINO_CREATE) }) {
                        Icon(Icons.Default.Add, null)
                        Spacer(Modifier.width(6.dp))
                        Text("Nuevo")
                    }
                }
            )
        }
    ) { pad ->
        Column(
            Modifier
                .padding(pad)
                .padding(16.dp)
        ) {
            when {
                loading   -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                errorMsg != null -> Text(errorMsg!!, color = MaterialTheme.colorScheme.error)
                places.isEmpty() -> Text("No hay lugares registrados.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(places) { place ->
                        PlaceCard(
                            place    = place,
                            onEdit   = { navController.navigate("placeEdit/${place.id}") },
                            onDelete = { toDelete = place }
                        )
                    }
                }
            }
        }
    }

    /* ---------- Diálogo de confirmación ---------- */
    toDelete?.let { place ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text("Eliminar \"${place.name}\"") },
            text  = { Text("Esta acción no se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        controller.delete(place.id)
                        places = places.filterNot { it.id == place.id }
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

/* ---------- Tarjeta individual ---------- */
@Composable
private fun PlaceCard(
    place: Place,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier  = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            /* campos protegidos contra nulls */
            Text(place.name ?: "—", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(place.description ?: "—", fontSize = 13.sp)
            Text("Lat: ${place.latitude ?: "—"}, Lng: ${place.longitude ?: "—"}", fontSize = 12.sp)
            Text("Actividad: ${place.activity ?: "—"}", fontSize = 12.sp)
            Text("Categoría: ${place.categoryName ?: "—"}", fontSize = 12.sp)

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(onClick = onEdit, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Editar")
                }
                OutlinedButton(onClick = onDelete, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Eliminar")
                }
            }
        }
    }
}
