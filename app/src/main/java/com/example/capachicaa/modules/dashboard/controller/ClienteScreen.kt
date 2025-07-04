package com.example.capachicaa.dashboard.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.*
import androidx.compose.ui.text.style.TextAlign
import kotlinx.coroutines.launch
import com.example.capachicaa.modules.auth.controller.AuthController
import com.example.capachicaa.modules.auth.model.User
import com.example.capachicaa.modules.dashboard.controller.SessionController
import com.example.capachicaa.modules.products.model.Product
import com.example.capachicaa.modules.products.controller.ProductController
import com.example.capachicaa.modules.reservations.controller.ReservationController
import com.example.capachicaa.modules.reservations.model.Reservation


/* ----------  ROOT  ---------- */
@Composable
fun ClienteScreen(navController: NavController, onLogout: () -> Unit) {   // ← solo UNA llave
    val context = navController.context
    val session = SessionController(context)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F6FA)),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item { TopWelcomeBar(navController, session, onLogout) }
        item { Spacer(Modifier.height(12.dp)) }
        item { ProfileCard() }
        item { Spacer(Modifier.height(12.dp)) }
        item { LoyaltyCard() }
        item { Spacer(Modifier.height(12.dp)) }
        item { StatsRow() }
        item { Spacer(Modifier.height(12.dp)) }
        item {
            ReservasRecientesSection(navController = navController)
        } // ← te explico abajo
        item { Spacer(Modifier.height(12.dp)) }
        item { RecommendationsSection() }
    }
}

/* ----------  COMPONENTES DE NIVEL SUPERIOR  ---------- */

@Composable
private fun TopWelcomeBar(
    navController: NavController,
    session: SessionController,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    // 👉 state donde guardaremos los datos del usuario
    var user by remember { mutableStateOf<User?>(null) }

    // 👉 cargamos el perfil al entrar al composable
    LaunchedEffect(Unit) {
        scope.launch {
            val controller = AuthController(context)
            val response   = controller.getProfile()
            if (response.isSuccessful) {
                user = response.body()          // guarda el usuario
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0D6EFD))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (user != null) {
                    Text(
                        text       = "👋 ¡Bienvenido, ${user!!.name}!",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                } else {
                    Text(
                        "👋 ¡Bienvenido!",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                }

                Text(
                    "Aquí puedes gestionar tus actividades turísticas",
                    color    = Color.White,
                    fontSize = 14.sp
                )
            }

            Button(
                onClick = {
                    scope.launch {
                        AuthController(context).logout() // invalida token en API
                        onLogout()                       // notifica a MainActivity
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) {
                Icon(Icons.Default.Logout, null, tint = Color(0xFF0D6EFD))
                Spacer(Modifier.width(4.dp))
                Text("Cerrar sesión", color = Color(0xFF0D6EFD))
            }
        }
    }
}


/* --- Mi Perfil --- */
@Composable
private fun ProfileCard() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var user by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        scope.launch {
            val controller = AuthController(context)
            val response = controller.getProfile()
            if (response.isSuccessful) {
                user = response.body()
            }
        }
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "👤 Mi Perfil",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.height(12.dp))

            user?.let {
                InfoRow("Nombre:", it.name)
                InfoRow("Correo:", it.email)
                InfoRow("Teléfono:", it.phone ?: "No registrado")
                InfoRow("Dirección:", it.address ?: "No registrada")
                InfoRow("Registrado desde:", it.created_at.substring(0, 10))
            } ?: run {
                Text("Cargando perfil...", color = Color.Gray)
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontWeight = FontWeight.Medium)
        Text(value, fontWeight = FontWeight.Light, textAlign = TextAlign.End)
    }
}




/* --- Programa de Fidelidad --- */
@Composable
private fun LoyaltyCard() {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0D6EFD))
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("⭐ Programa de Fidelidad", color = Color.White, fontWeight = FontWeight.Bold)
            LinearProgressIndicator(
                progress = 0.0f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .padding(vertical = 8.dp),
                color = Color.White,
                trackColor = Color(0xFF3B82F6)
            )
            Text("¡Solo te faltan 5 reservas para alcanzar el siguiente nivel!", color = Color.White)
        }
    }
}

/* --- Métricas --- */
@Composable
private fun StatsRow() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var total by remember { mutableStateOf(0) }
    var pendientes by remember { mutableStateOf(0) }
    var completadas by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        scope.launch {
            val auth = AuthController(context)
            val token = auth.getToken()
            if (!token.isNullOrEmpty()) {
                val controller = ReservationController(context)
                val response = controller.getAll()
                if (response.isSuccessful) {
                    val reservas = response.body() ?: emptyList()
                    total = reservas.size
                    pendientes = reservas.count { it.status == "pendiente" }
                    completadas = reservas.count { it.status == "completada" || it.status == "aprobada" }
                }
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        DashboardStatCard(
            title = "Reservas Totales",
            value = total.toString(),
            subtitle = "+15% vs último mes",
            bgColor = Color(0xFFEEF4FF),
            modifier = Modifier.weight(1f)
        )
        DashboardStatCard(
            title = "Pendientes",
            value = pendientes.toString(),
            subtitle = "Últimos 30 días",
            bgColor = Color(0xFFFFF9E6),
            modifier = Modifier.weight(1f)
        )
        DashboardStatCard(
            title = "Completadas",
            value = completadas.toString(),
            subtitle = "Últimos 30 días",
            bgColor = Color(0xFFE6FFF2),
            modifier = Modifier.weight(1f)
        )
    }
}


@Composable
fun DashboardStatCard(
    title: String,
    value: String,
    subtitle: String,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(Modifier.padding(12.dp)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(value, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

/* --- Tus Reservas Recientes --- */
@Composable
private fun RecentReservationsCard(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var reservations by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Fetch desde backend
    LaunchedEffect(Unit) {
        scope.launch {
            val token = SessionController(context).getToken()
            val controller = ReservationController(context)
            val response = controller.getAll()

            if (response.isSuccessful) {
                reservations = response.body() ?: emptyList()
            }
            isLoading = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            // 🔹 Título
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, contentDescription = null, tint = Color(0xFF0D6EFD))
                Spacer(Modifier.width(6.dp))
                Text("Tus Reservas Recientes", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(8.dp))

            // 🔹 Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = { navController.navigate("mis_reservas") }
                ) {
                    Text("Ver todas")
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { navController.navigate("catalogo_productos") }) {
                    Icon(Icons.Default.Add, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Nueva reserva")
                }
            }

            Spacer(Modifier.height(12.dp))

            // 🔹 Estado de carga
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                return@Column
            }

            // 🔹 Mostrar lista o mensaje vacío
            if (reservations.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.EventBusy, null, tint = Color.Gray, modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("No tienes reservas aún", fontWeight = FontWeight.SemiBold)
                    Text("Haz tu primera reserva para que aparezca aquí", color = Color.Gray)
                }
            } else {
                reservations.take(3).forEach { res ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text("Producto: ${res.productName}", fontWeight = FontWeight.Bold)
                            Text("Cantidad: ${res.quantity}")
                            Text("Total: S/ ${res.totalAmount}")
                            Text("Estado: ${res.status}")
                            Text("Fecha: ${res.reservationDate}")
                        }
                    }
                }
            }
        }
    }
}




/* --- Recomendaciones para ti --- */
@Composable
private fun RecommendationsSection() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val auth = AuthController(context)
                val token = auth.getToken()
                if (token.isNullOrEmpty()) {
                    println("❌ No hay token disponible")
                    return@launch
                }

                val controller = ProductController()
                val response = controller.getAllProducts(token)

                println("📦 Código HTTP = ${response.code()}")
                println("📦 Cuerpo = ${response.body()}")

                if (response.isSuccessful) {
                    products = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                println("❌ Error en recomendaciones:")
                e.printStackTrace()
            }
        }
    }

    if (products.isNotEmpty()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = Color(0xFFFFC107))
                    Spacer(Modifier.width(4.dp))
                    Text("Recomendaciones para ti", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(12.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(products) { product ->
                        ProductCard(
                            title = product.name,
                            subtitle = product.description,
                            price = product.price.toString()
                        )
                    }
                }
            }
        }
    }
}

/* ----------  ELEMENTOS DE PRODUCTO  ---------- */

@Composable
private fun ProductCard(
    title: String,
    subtitle: String,
    price: String
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(200.dp), // Aumentamos la altura
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Imagen (aún placeholder)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp) // ← Cambia de 10.dp a 120.dp para que sea visible
                    .background(Color(0xFFE0E0E0))
            )

            Column(Modifier.padding(12.dp)) {
                Text(title, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, fontSize = 12.sp, color = Color.Gray, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) {
                        Icon(Icons.Default.Star, null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                    }
                    Spacer(Modifier.width(4.dp))
                    Text("(24)", fontSize = 12.sp, color = Color.Gray)
                }

                Spacer(Modifier.height(4.dp))
                Text("S/ $price", fontWeight = FontWeight.Bold, color = Color(0xFF0D6EFD))

                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { /* explorar */ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Explorar")
                }
            }
        }
    }
}
@Composable
fun ReservasRecientesSection(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var reservas by remember { mutableStateOf<List<Reservation>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val token = AuthController(context).getToken()
                if (!token.isNullOrEmpty()) {
                    val controller = ReservationController(context)
                    val response = controller.getAll()
                    if (response.isSuccessful) {
                        reservas = response.body() ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Tus Reservas", fontWeight = FontWeight.Bold)
                OutlinedButton(onClick = { navController.navigate("mis_reservas") }) {
                    Text("Ver todas", fontSize = 13.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            if (reservas.isEmpty()) {
                Text("No tienes reservas aún", color = Color.Gray)
            } else {
                Spacer(Modifier.height(8.dp))
                reservas.take(3).forEach { reserva -> ReservaItem(reserva = reserva) }
            }

            Spacer(Modifier.height(12.dp))
            Button(
                onClick = { navController.navigate("catalogo_productos") },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D6EFD))
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(6.dp))
                Text("Nueva reserva", color = Color.White)
            }
        }
    }
}

@Composable
fun ReservaItem(reserva: Reservation) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F9FC))
    ) {
        Column(Modifier.padding(12.dp)) {
            Text("Producto: ${reserva.productName}", fontWeight = FontWeight.SemiBold)
            Text("Fecha: ${reserva.reservationDate}")
            Text("Cantidad: ${reserva.quantity}")
            Text("Total: S/ ${reserva.totalAmount}")
            Text(
                "Estado: ${reserva.status}",
                color = when (reserva.status) {
                    "pendiente" -> Color(0xFFFFC107)
                    "aprobado" -> Color(0xFF4CAF50)
                    "rechazado" -> Color(0xFFF44336)
                    else -> Color.Gray
                },
                fontWeight = FontWeight.Bold
            )
        }
    }
}



