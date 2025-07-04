package com.example.capachicaa.modules.entrepreneurs.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable

fun EmprendedorDashboardScreen(
    navController: NavController,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hola, Emprendedor", fontSize = 20.sp) },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Salir")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            item { PerfilCard(navController) }
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item { EstadisticasResumen() }
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item { BotonesGestionNegocio(navController) }
            item { Spacer(modifier = Modifier.height(12.dp)) }
            item { ProductosRecientesSection() }
            item { PagosRecientesSection() }
            item { ActividadRecienteSection() }
        }
    }
}

@Composable
fun PerfilCard(navController: NavController) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Mi Perfil", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(Color.Gray, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto de perfil",
                        tint = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Correo: marco123@gmail.com")
                    Text("Teléfono: 987654321")
                    Text("Ubicación: San Román")
                    Text("RUC: 12345678919")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { navController.navigate("editar_perfil") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2D5542))
            ) {
                Text("Editar Perfil")
            }
        }
    }
}

@Composable
fun EstadisticasResumen() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        EstadisticaItem("Productos", "0")
        EstadisticaItem("Reservas", "0")
        EstadisticaItem("Ingresos", "S/ 0.00")
        EstadisticaItem("Valoración", "—/5")
    }
}

@Composable
fun EstadisticaItem(label: String, value: String) {
    Card(
        modifier = Modifier
            .width(80.dp)
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(label, fontSize = 12.sp)
        }
    }
}

@Composable
fun BotonesGestionNegocio(navController: NavController) {
    val botones = listOf(
        "Mis Productos" to "mis_productos",
        "Crear Producto" to "crear_producto",
        "Mis Reservas" to "mis_reservas",
        "Mis Lugares" to "mis_lugares",
        "Tours Destacados" to "tours_destacados",
        "Pagos" to "pagos",
        "Editar Perfil" to "editar_perfil"
    )
    Column {
        Text("Gestión del Negocio", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))

        for (i in botones.chunked(2)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for ((label, route) in i) {
                    Button(
                        onClick = { navController.navigate(route) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A75B5))
                    ) {
                        Text(label, fontSize = 12.sp, color = Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ProductosRecientesSection() {
    Column {
        Text("Productos Recientes", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("No hay productos registrados", color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun PagosRecientesSection() {
    Column {
        Text("Pagos Recientes", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("No hay pagos recientes", color = Color.Gray, fontSize = 12.sp)
    }
}

@Composable
fun ActividadRecienteSection() {
    Column {
        Text("Actividad Reciente", fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("✅ Creación de perfil – 18/6/2025, 13:35:29", fontSize = 12.sp)
        Text("🔄 Última actualización – 18/6/2025, 13:35:29", fontSize = 12.sp)
    }
}
