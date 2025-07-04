package com.example.capachicaa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.capachicaa.dashboard.view.ClienteScreen
import com.example.capachicaa.modules.admin.view.AdminScreen
import com.example.capachicaa.modules.auth.view.AuthNavGraph
import com.example.capachicaa.modules.dashboard.controller.SessionController
import com.example.capachicaa.ui.theme.CapachicaaTheme
import com.example.capachicaa.modules.entrepreneurs.view.EmprendedorDashboardScreen
import com.example.capachicaa.modules.payments.view.EmprendedorPagosScreen
import com.example.capachicaa.modules.products.view.CatalogoProductosScreen
import com.example.capachicaa.modules.products.view.CrearProductoScreen
import com.example.capachicaa.modules.products.view.DetalleProductoScreen
import com.example.capachicaa.modules.products.view.EditarProductoScreen
import com.example.capachicaa.modules.products.view.MisProductosScreen
import com.example.capachicaa.modules.reservations.view.MisReservasScreen
import com.example.capachicaa.modules.reservations.view.ReservaPagoScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            CapachicaaTheme {
                val session = remember { SessionController(this) }
                val navController = rememberNavController()

                var userRole by remember { mutableStateOf("") }
                var isLoggedIn by remember { mutableStateOf(false) }

                NavHost(
                    navController = navController,
                    startDestination = "auth"
                ) {
                    // Auth
                    composable("auth") {
                        AuthNavGraph(
                            onAuthSuccess = {
                                userRole = session.getRole() ?: ""
                                isLoggedIn = true
                                navController.navigate("dashboard") {
                                    popUpTo("auth") { inclusive = true }
                                }
                            }
                        )
                    }

                    // Dashboard por rol
                    composable("dashboard") {
                        when (userRole) {
                            "super-admin" -> AdminScreen(
                                appNavController = navController,
                                userRole = userRole
                            )
                            "cliente" -> ClienteScreen(
                                navController = navController,
                                onLogout = {
                                    session.clearSession()
                                    userRole = ""
                                    isLoggedIn = false
                                    navController.navigate("auth") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                }
                            )
                            "emprendedor" -> EmprendedorDashboardScreen(
                                navController = navController,
                                onLogout = {
                                    session.clearSession()
                                    userRole = ""
                                    isLoggedIn = false
                                    navController.navigate("auth") {
                                        popUpTo("dashboard") { inclusive = true }
                                    }
                                }
                            )
                            else -> AdminScreen(
                                appNavController = navController,
                                userRole = "admin"
                            )
                        }
                    }

                    // Crear producto
                    composable("crear_producto") {
                        CrearProductoScreen(
                            navController = navController,
                            token = session.getToken() ?: "",
                            entrepreneurId = session.getEntrepreneurId() ?: 0
                        )
                    }

                    // Mis productos
                    composable("mis_productos") {
                        MisProductosScreen(
                            token = session.getToken() ?: "",
                            navController = navController
                        )
                    }

                    // Editar producto
                    composable("editar_producto/{productId}") { backStackEntry ->
                        val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                        if (productId != null) {
                            EditarProductoScreen(
                                navController = navController,
                                token = session.getToken() ?: "",
                                productId = productId
                            )
                        }
                    }

                    composable("catalogo_productos") {
                        CatalogoProductosScreen(navController = navController)
                    }

                    composable("detalle_producto/{productId}") { backStackEntry ->
                        val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
                        if (productId != null) {
                            DetalleProductoScreen(
                                navController = navController,
                                productId = productId
                            )
                        }
                    }

                    composable(
                        "reserva_pago/{productId}/{productName}/{quantity}/{totalAmount}",
                        arguments = listOf(
                            navArgument("productId") { type = NavType.IntType },
                            navArgument("productName") { type = NavType.StringType },
                            navArgument("quantity") { type = NavType.IntType },
                            navArgument("totalAmount") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val productId = backStackEntry.arguments?.getInt("productId") ?: 0
                        val productName = backStackEntry.arguments?.getString("productName") ?: ""
                        val totalAmountStr = backStackEntry.arguments?.getString("totalAmount") ?: "0.0"
                        val totalAmount = totalAmountStr.toDoubleOrNull() ?: 0.0

                        ReservaPagoScreen(
                            navController = navController,
                            productId = productId,
                            productName = productName,
                            totalAmount = totalAmount
                        )
                    }

                    // Mis reservas
                    composable("mis_reservas") {
                        MisReservasScreen(
                            navController = navController,
                            token = session.getToken() ?: ""
                        )
                    }

                    // Pagos del emprendedor
                    composable("pagos") {
                        EmprendedorPagosScreen(navController)
                    }
                }
            }
        }
    }
}
