package com.example.capachicaa.modules.reservations.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.capachicaa.dashboard.view.ReservaItem
import com.example.capachicaa.modules.reservations.controller.ReservationController
import com.example.capachicaa.modules.reservations.model.Reservation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisReservasScreen(navController: NavController, token: String) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var reservas by remember { mutableStateOf<List<Reservation>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            val controller = ReservationController(context)
            val response = controller.getAll()
            if (response.isSuccessful) {
                reservas = response.body() ?: emptyList()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis Reservas") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reservas) { reserva ->
                ReservaItem(reserva)
            }
        }
    }
}
