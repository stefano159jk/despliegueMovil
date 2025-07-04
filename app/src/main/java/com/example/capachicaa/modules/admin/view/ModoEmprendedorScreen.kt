package com.example.capachicaa.modules.admin.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.modules.admin.navigation.AdminRoutes

/* ---------- Modelo ---------- */
data class AdminFeatureDualAction(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val count: Int,
    val headerColor: Color,
    val routeList: String,
    val routeCreate: String
)

/* ---------- Lista ---------- */
private val features = listOf(
    AdminFeatureDualAction(
        title = "Asociaciones",
        subtitle = "Grupos y comunidades locales",
        icon = Icons.Default.Groups,
        count = 0,
        headerColor = Color(0xFF1E88E5),
        routeList = "adminAsociacionesList",
        routeCreate = "adminAsociacionCreate"
    ),
    AdminFeatureDualAction(
        title = "Categorías",
        subtitle = "Clasificación de experiencias",
        icon = Icons.Default.Category,
        count = 0,
        headerColor = Color(0xFF43A047),
        routeList = "adminCategoriasList",
        routeCreate = "adminCategoriaCreate"
    ),
    AdminFeatureDualAction(
        title = "Emprendedores",
        subtitle = "Proveedores de servicios",
        icon = Icons.Default.Person,
        count = 0,
        headerColor = Color(0xFFFB8C00),
        routeList = "adminEmprendedoresList",
        routeCreate = "adminEmprendedorCreate"
    ),
    AdminFeatureDualAction(
        title = "Reservas",
        subtitle = "Gestión de reservas",
        icon = Icons.Default.CalendarToday,
        count = 0,
        headerColor = Color(0xFF00ACC1),
        routeList = "adminReservasList",
        routeCreate = "adminReservaCreate"
    ),
    AdminFeatureDualAction(
        title = "Lugares",
        subtitle = "Puntos de interés",
        icon = Icons.Default.Place,
        count = 0,
        headerColor = Color(0xFF039BE5),
        routeList = AdminRoutes.DESTINOS_LIST,
        routeCreate = "adminLugarCreate"
    ),
    AdminFeatureDualAction(
        title = "Usuarios",
        subtitle = "Gestión de cuentas",
        icon = Icons.Default.People,
        count = 0,
        headerColor = Color(0xFF8E24AA),
        routeList = "adminUsuariosList",
        routeCreate = "adminUsuarioCreate"
    )
)

/* ---------- Pantalla ---------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModoEmprendedorScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Modo Emprendedor",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Header informativo
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Business,
                        contentDescription = "Emprendedor",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Panel de Administración",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Gestiona los recursos para emprendedores",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Grid de características
            LazyVerticalGrid(
                columns = GridCells.Adaptive(180.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(features) { item ->
                    FeatureCard(item, navController)
                }
            }
        }
    }
}

@Composable
private fun FeatureCard(
    feature: AdminFeatureDualAction,
    navController: NavController
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // Cabecera con degradado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                feature.headerColor,
                                feature.headerColor.copy(alpha = 0.8f)
                            )
                        )
                    )
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        feature.icon,
                        contentDescription = feature.title,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            feature.title,
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            feature.subtitle,
                            color = Color.White.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            // Contenido
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "${feature.count} registros",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { navController.navigate(feature.routeList) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = feature.headerColor
                        )
                    ) {
                        Text("Lista", fontSize = 12.sp)
                    }

                    FilledTonalButton(
                        onClick = { navController.navigate(feature.routeCreate) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = feature.headerColor.copy(alpha = 0.2f),
                            contentColor = feature.headerColor
                        )
                    ) {
                        Text("Nuevo", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}