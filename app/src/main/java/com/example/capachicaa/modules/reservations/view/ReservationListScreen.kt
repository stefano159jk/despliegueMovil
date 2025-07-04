package com.example.capachicaa.modules.reservations.view

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.modules.reservations.controller.ReservationController
import com.example.capachicaa.modules.reservations.model.Reservation
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ReservationListScreen(navController: NavController) {
    // Contexto y controladores
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controller = remember { ReservationController(context) }

    // Estados
    var reservations by remember { mutableStateOf<List<Reservation>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Paleta de colores premium
    val primaryColor = Color(0xFF4361EE)
    val secondaryColor = Color(0xFF3F37C9)
    val successColor = Color(0xFF4CC9F0)
    val errorColor = Color(0xFFF72585)
    val surfaceColor = Color(0xFFFFFFFF)
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFFF8F9FA), Color(0xFFE9ECEF))
    )

    // Cargar datos
    LaunchedEffect(Unit) {
        val resp = controller.getAll()
        loading = false
        if (resp.isSuccessful) {
            reservations = resp.body() ?: emptyList()
        } else {
            errorMessage = "Error ${resp.code()}: ${resp.message()}"
        }
    }

    // Diseño principal
    Box(modifier = Modifier.fillMaxSize().background(gradientBackground)) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "MIS RESERVAS",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp,
                                    color = Color.White
                                )
                            )
                            Text(
                                "Historial de experiencias",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = primaryColor,
                        titleContentColor = Color.White
                    ),
                    modifier = Modifier.shadow(elevation = 12.dp)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize()
            ) {
                // Título con animación
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { -20 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    Text(
                        "Tus Experiencias Reservadas",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            fontSize = 22.sp
                        ),
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Contenido principal
                when {
                    loading -> LoadingState(primaryColor)
                    errorMessage != null -> ErrorState(errorMessage!!, errorColor)
                    reservations.isEmpty() -> EmptyState()
                    else -> {
                        // Header con efecto glassmorphism
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .shadow(
                                    elevation = 8.dp,
                                    shape = RoundedCornerShape(16.dp),
                                    spotColor = primaryColor.copy(alpha = 0.1f)
                                ),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = surfaceColor.copy(alpha = 0.9f)
                            )
                        ) {
                            HeaderRow(primaryColor)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Lista de reservas con animaciones
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(reservations, key = { it.id ?: "" }) { reservation ->
                                AnimatedVisibility(
                                    visible = true,
                                    enter = fadeIn() + slideInVertically(
                                        initialOffsetY = { 40 },
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioLowBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    ),
                                    modifier = Modifier.animateItemPlacement()
                                ) {
                                    ReservationCard(
                                        reservation = reservation,
                                        primaryColor = primaryColor,
                                        successColor = successColor,
                                        onViewReceipt = { url ->
                                            context.startActivity(
                                                Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderRow(primaryColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "EXPERIENCIA",
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                letterSpacing = 0.5.sp
            )
        )
        Text(
            "CANT.",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                letterSpacing = 0.5.sp
            ),
            textAlign = TextAlign.Center
        )
        Text(
            "FECHA",
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                letterSpacing = 0.5.sp
            ),
            textAlign = TextAlign.Center
        )
        Text(
            "ESTADO",
            modifier = Modifier.weight(2f),
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.Bold,
                color = primaryColor,
                letterSpacing = 0.5.sp
            ),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun ReservationCard(
    reservation: Reservation,
    primaryColor: Color,
    successColor: Color,
    onViewReceipt: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = primaryColor.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Información principal
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(2f)) {
                    Text(
                        reservation.productName ?: "Experiencia no especificada",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.DarkGray
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = reservation.totalAmount?.let { "Inversión: S/ $it" } ?: "S/ —",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = Color.Gray
                        )
                    )
                }

                Text(
                    reservation.quantity?.toString() ?: "—",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    ),
                    textAlign = TextAlign.Center
                )

                Text(
                    reservation.reservationDate ?: "—",
                    modifier = Modifier.weight(2f),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.DarkGray.copy(alpha = 0.8f)
                    ),
                    textAlign = TextAlign.Center
                )

                StatusBadge(
                    status = reservation.status ?: "pendiente",
                    modifier = Modifier.weight(2f)
                )
            }

            // Acciones y detalles adicionales
            reservation.receiptUrl?.let { url ->
                Button(
                    onClick = { onViewReceipt(url) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = successColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 2.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    Icon(
                        Icons.Default.Receipt,
                        contentDescription = "Boleta",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Ver Comprobante", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: String, modifier: Modifier = Modifier) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "confirmado" -> Color(0xFF4CAF50) to Color.White
        "cancelado" -> Color(0xFFF44336) to Color.White
        "pendiente" -> Color(0xFFFFA000) to Color.Black
        else -> Color(0xFF9E9E9E) to Color.White
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )
        )
    }
}

@Composable
private fun LoadingState(primaryColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = primaryColor,
                strokeWidth = 4.dp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                "Cargando tus experiencias...",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = primaryColor.copy(alpha = 0.8f)
                )
            )
        }
    }
}

@Composable
private fun ErrorState(message: String, errorColor: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                Icons.Default.ErrorOutline,
                contentDescription = "Error",
                tint = errorColor,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "¡Algo salió mal!",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = errorColor
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.DarkGray.copy(alpha = 0.8f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                Icons.Default.ConfirmationNumber,
                contentDescription = "Sin reservas",
                tint = Color.LightGray.copy(alpha = 0.5f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No tienes reservas aún",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Explora nuestras experiencias y reserva tu próxima aventura",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}