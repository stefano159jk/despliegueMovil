package com.example.capachicaa.modules.admin.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.capachicaa.modules.admin.navigation.AdminNavGraph
import com.example.capachicaa.modules.admin.navigation.AdminRoutes
import com.example.capachicaa.modules.dashboard.controller.SessionController
import kotlinx.coroutines.launch

/* ---------- Colores ---------- */
private val Purple80     = Color(0xFF6F42C1)
private val InfoCardColor = Color(0xFFE3F2FD)

/* ---------- DATA ---------- */
private data class QuickAction(
    val title: String,
    val description: String,
    val route: String,
    val icon: ImageVector,
    val color: Color = Purple80
)

private data class DrawerItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

/* ---------- Acciones rápidas ---------- */
private val quickActions = listOf(
    QuickAction("Crear Tour",        "Agrega nuevo paquete", AdminRoutes.CREAR_TOUR,   Icons.Default.Add,       Color(0xFF4CAF50)),
    QuickAction("Reservas",          "Gestiona reservas",    AdminRoutes.RESERVAS,    Icons.Default.EventNote, Color(0xFF2196F3)),
    QuickAction("Pagos",             "Consulta pagos",       AdminRoutes.PAGOS,       Icons.Default.Receipt,   Color(0xFF673AB7)),
    QuickAction("Paquetes",          "Revisa paquetes",      AdminRoutes.TOURS_LIST,  Icons.Default.List,      Color(0xFF607D8B)),
    QuickAction("Portada",           "Actualiza sección",    AdminRoutes.EDITAR_HOME, Icons.Default.Edit,      Color(0xFFE91E63)),
    QuickAction("Configuración",     "Ajustes",              AdminRoutes.EDITAR_HERO, Icons.Default.Settings,  Color(0xFF9C27B0))
)

/* ---------- Items del Drawer ---------- */
private val drawerItems = listOf(
    DrawerItem("Dashboard",   AdminRoutes.DASHBOARD,       Icons.Default.Dashboard),
    DrawerItem("Tours",       AdminRoutes.TOURS_LIST,      Icons.Default.List),
    DrawerItem("Reservas",    AdminRoutes.RESERVAS,        Icons.Default.CalendarToday),
    DrawerItem("Pagos",       AdminRoutes.PAGOS,           Icons.Default.Payments),
    DrawerItem("Hero",        AdminRoutes.EDITAR_HERO,     Icons.Default.Image),
    DrawerItem("Home",        AdminRoutes.EDITAR_HOME,     Icons.Default.Home),
    DrawerItem("Emprendedor", AdminRoutes.MODO_EMPRENDEDOR,Icons.Default.Person),
    DrawerItem("Cerrar Sesión","logout",                   Icons.Default.ExitToApp)
)

/* ---------- PANTALLA PRINCIPAL ---------- */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    appNavController: NavHostController,   // ← controller global (MainActivity)
    userRole: String = "Super-Admin"
) {
    val adminNavController = rememberNavController()      // ← controller interno
    val drawerState        = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope              = rememberCoroutineScope()
    val context            = LocalContext.current
    val session            = remember { SessionController(context) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            ModalDrawerSheet(Modifier.width(280.dp)) {
                /* --- Encabezado Drawer --- */
                Spacer(Modifier.height(16.dp))
                Row(
                    Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AdminPanelSettings, null, tint = Purple80, modifier = Modifier.size(32.dp))
                    Spacer(Modifier.width(12.dp))
                    Text("CapachicaAdmin", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                }
                Spacer(Modifier.height(24.dp))

                /* --- Opciones --- */
                drawerItems.forEach { item ->
                    NavigationDrawerItem(
                        label = { Text(item.label) },
                        icon  = { Icon(item.icon, null) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            if (item.route == "logout") {
                                session.clearSession()
                                appNavController.navigate("auth") { popUpTo("dashboard") { inclusive = true } }
                            } else {
                                adminNavController.navigate(item.route) { launchSingleTop = true }
                            }
                        },
                        modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = Purple80.copy(alpha = 0.2f)
                        )
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Panel de Administración", fontWeight = FontWeight.SemiBold) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, null)
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { adminNavController.navigate(AdminRoutes.CREAR_TOUR) },
                    containerColor = Purple80
                ) { Icon(Icons.Default.Add, null) }
            }
        ) { padding ->
            Box(Modifier.padding(padding)) {
                /* --- NavHost interno del módulo Admin --- */
                AdminNavGraph(navController = adminNavController)
            }
        }
    }
}

/* ---------- DASHBOARD VISUAL ---------- */
@Composable
fun AdminContent(
    userRole: String,
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text("Bienvenido al Dashboard", fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text("Gestiona tus datos y operaciones desde aquí", color = Color.Gray, fontSize = 14.sp)

        Spacer(Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors   = CardDefaults.cardColors(containerColor = InfoCardColor),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(Icons.Default.VerifiedUser, null, modifier = Modifier.size(32.dp))
                Column {
                    Text("Tu Rol: $userRole", fontWeight = FontWeight.SemiBold)
                    Text("Controla y administra la plataforma completa", fontSize = 12.sp)
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        Text("Acciones Rápidas", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 180.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxHeight()
        ) {
            items(quickActions) { action ->
                QuickActionCard(action, navController)
            }
        }
    }
}

@Composable
private fun QuickActionCard(action: QuickAction, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { navController.navigate(action.route) },
        colors = CardDefaults.cardColors(
            containerColor = action.color.copy(alpha = 0.1f),
            contentColor   = action.color
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(action.icon, null, modifier = Modifier.size(32.dp))
            Column {
                Text(action.title, fontWeight = FontWeight.SemiBold)
                Text(action.description, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

/* ---------- PREVIEW ---------- */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AdminScreenPreview() {
    val rootNav = rememberNavController()
    AdminScreen(appNavController = rootNav)
}
