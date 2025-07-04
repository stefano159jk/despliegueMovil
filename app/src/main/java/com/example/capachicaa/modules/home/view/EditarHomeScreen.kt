package com.example.capachicaa.modules.home.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.modules.admin.navigation.AdminRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarHomeScreen(navController: NavController) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Dashboard de Contenido",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(4.dp)
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
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
                        Icons.Default.Settings,
                        contentDescription = "Configuración",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            "Personalización del Sitio",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Administra todas las secciones de tu sitio turístico",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            // Grid de contenido
            ContentGrid(navController)
        }
    }
}

@Composable
private fun ContentGrid(navController: NavController) {
    val contentItems = listOf(
        ContentItem(
            title = "Hero (Bienvenida)",
            description = "Personaliza el encabezado principal con imágenes y texto",
            icon = Icons.Default.Star,
            route = AdminRoutes.EDITAR_HERO,
            color = Color(0xFF1E88E5)
        ),
        ContentItem(
            title = "Historia",
            description = "Edita la sección 'Sobre nosotros' y su contenido",
            icon = Icons.Default.Description,
            route = AdminRoutes.EDITAR_HISTORIA,
            color = Color(0xFF43A047)
        ),
        ContentItem(
            title = "Tours Destacados",
            description = "Gestiona los tours que aparecen en la página principal",
            icon = Icons.Default.Map,
            route = AdminRoutes.TOURS_LIST,
            color = Color(0xFFFB8C00)
        ),
        ContentItem(
            title = "Galería",
            description = "Administra las imágenes de la galería del sitio",
            icon = Icons.Default.Photo,
            route = AdminRoutes.GALERIA_IMAGENES,
            color = Color(0xFFE53935)
        ),
        ContentItem(
            title = "Contacto",
            description = "Actualiza información de contacto y ubicación",
            icon = Icons.Default.LocationOn,
            route = AdminRoutes.EDITAR_CONTACTO,
            color = Color(0xFF8E24AA)
        ),
        ContentItem(
            title = "Destinos Turísticos",
            description = "Administra los destinos registrados",
            icon = Icons.Default.Place,
            route = AdminRoutes.DESTINOS_LIST,
            color = Color(0xFF00ACC1)
        ),
        ContentItem(
            title = "Experiencias",
            description = "Gestiona las experiencias turísticas destacadas",
            icon = Icons.Default.Explore,
            route = AdminRoutes.EDITAR_EXPERIENCIAS,
            color = Color(0xFF5E35B1)
        )
    )

    LazyVerticalGrid(
        columns = GridCells.Adaptive(160.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        itemsIndexed(contentItems) { _, item ->
            ContentCard(item, navController)
        }
    }
}

private data class ContentItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String,
    val color: Color
)

@Composable
private fun ContentCard(item: ContentItem, navController: NavController) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .clickable { navController.navigate(item.route) },
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = item.title,
                tint = item.color,
                modifier = Modifier.size(32.dp)
            )

            Column {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}