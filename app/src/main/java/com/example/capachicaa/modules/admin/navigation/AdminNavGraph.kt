package com.example.capachicaa.modules.admin.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.capachicaa.modules.admin.view.*
import com.example.capachicaa.modules.associations.view.*
import com.example.capachicaa.modules.auth.controller.AuthController
import com.example.capachicaa.modules.auth.view.LoginScreen
import com.example.capachicaa.modules.categories.view.*
import com.example.capachicaa.modules.dashboard.controller.SessionController
import com.example.capachicaa.modules.entrepreneurs.view.*
import com.example.capachicaa.modules.gallery.view.GaleriaScreen
import com.example.capachicaa.modules.home.view.EditarHomeScreen
import com.example.capachicaa.modules.payments.view.PaymentListScreen
import com.example.capachicaa.modules.place.view.*
import com.example.capachicaa.modules.products.view.CatalogoProductosScreen
import com.example.capachicaa.modules.products.view.EditarProductoScreen
import com.example.capachicaa.modules.products.view.MisProductosScreen
import com.example.capachicaa.modules.reservations.view.ReservationListScreen
import com.example.capachicaa.modules.tours.model.Tour
import com.example.capachicaa.modules.tours.view.*

/* ──────────────── CONSTANTES DE RUTAS ──────────────── */
object AdminRoutes {
    const val LOGIN                = "login"

    const val DASHBOARD            = "dashboard"
    const val CATALOGO_PRODUCTOS = "catalogo_productos"


    // Tours
    const val TOURS_LIST           = "toursList"
    const val CREAR_TOUR           = "crearTour"
    const val EDITAR_TOUR          = "editarTour/{id}"

    // Reservas – Pagos
    const val RESERVAS             = "adminReservasList"
    const val PAGOS                = "pagos"

    // Home & secciones
    const val EDITAR_HOME          = "editarHome"
    const val EDITAR_HERO          = "editarHero"
    const val EDITAR_HISTORIA      = "editarHistoria"
    const val TOURS_DESTACADOS     = "toursDestacados"
    const val GALERIA_IMAGENES     = "galeriaImagenes"
    const val EDITAR_CONTACTO      = "editarContacto"
    const val EDITAR_DESTINOS      = "editarDestinos"
    const val EDITAR_EXPERIENCIAS  = "editarExperiencias"

    // Modo emprendedor
    const val MODO_EMPRENDEDOR     = "modoEmprendedor"

    // Asociaciones
    const val ASOCIACIONES_LIST    = "adminAsociacionesList"
    const val ASOCIACION_CREATE    = "adminAsociacionCreate"
    const val ASOCIACION_EDITAR    = "adminAsociacionEdit/{id}"

    // Categorías
    const val CATEGORIAS_LIST      = "adminCategoriasList"
    const val CATEGORIA_CREATE     = "adminCategoriaCreate"
    const val CATEGORIA_EDITAR     = "adminCategoriaEdit/{id}"

    // Emprendedores
    const val EMPRENDEDORES_LIST   = "adminEmprendedoresList"
    const val EMPRENDEDOR_CREATE   = "emprendedorCreate"
    const val EMPRENDEDOR_EDITAR   = "emprendedorEdit/{id}"

    // Destinos (Places)
    const val DESTINOS_LIST        = "adminDestinosList"
    const val DESTINO_CREATE       = "adminDestinoCreate"
    const val DESTINO_EDITAR       = "adminDestinoEdit/{id}"

    // Otros placeholders
    const val LUGARES_LIST         = "adminLugaresList"
    const val LUGARES              = "adminLugares"
    const val USUARIOS             = "adminUsuarios"
}

/* ──────────────── NAVGRAPH PRINCIPAL DEL ADMIN ──────────────── */
@Composable
fun AdminNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = AdminRoutes.DASHBOARD
    ) {

        composable("editar_producto/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            if (productId != null) {
                val session = SessionController(context = LocalContext.current)
                EditarProductoScreen(
                    navController = navController,
                    token = session.getToken() ?: "",
                    productId = productId
                )
            }
        }
        composable(AdminRoutes.DASHBOARD) {
            AdminContent(userRole = "Super-Admin", navController = navController)
        }
        composable(AdminRoutes.CATALOGO_PRODUCTOS) {
            CatalogoProductosScreen(navController)
        }


        /* ---------- TOURS ---------- */
        composable(AdminRoutes.TOURS_LIST)  { TourListScreen(navController) }
        composable(AdminRoutes.CREAR_TOUR)  { TourCreateScreen(navController) }
        composable(
            AdminRoutes.EDITAR_TOUR,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { back ->
            val id   = back.arguments?.getInt("id") ?: 0
            val tour = navController.previousBackStackEntry?.savedStateHandle?.get<Tour>("tour_$id")
            if (tour != null) TourEditScreen(navController, tour)
            else               PlaceholderScreen("No se pudo cargar el tour #$id")
        }

        /* ---------- ASOCIACIONES ---------- */
        composable(AdminRoutes.ASOCIACIONES_LIST) { AsociacionesListScreen(navController) }
        composable(AdminRoutes.ASOCIACION_CREATE) { AsociacionCreateScreen(navController) }
        composable(
            AdminRoutes.ASOCIACION_EDITAR,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { back ->
            val id = back.arguments?.getInt("id") ?: 0
            AsociacionEditScreen(navController, assocId = id)
        }

        /* ---------- CATEGORÍAS ---------- */
        composable(AdminRoutes.CATEGORIAS_LIST)  { CategoriaListScreen(navController) }
        composable(AdminRoutes.CATEGORIA_CREATE) { CategoriaCreateScreen(navController) }
        composable(
            AdminRoutes.CATEGORIA_EDITAR,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { back ->
            val id = back.arguments?.getInt("id") ?: 0
            CategoriaEditScreen(navController, categoryId = id)
        }

        /* ---------- EMPRENDEDORES ---------- */
        composable(AdminRoutes.EMPRENDEDORES_LIST) { EmprendedorListScreen(navController) }
        composable(AdminRoutes.EMPRENDEDOR_CREATE) { EmprendedorCreateScreen(navController) }
        composable(
            AdminRoutes.EMPRENDEDOR_EDITAR,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { back ->
            val id = back.arguments?.getInt("id") ?: 0
            EmprendedorEditScreen(navController, id)
        }

        /* ---------- DESTINOS ---------- */
        composable(AdminRoutes.DESTINOS_LIST)  { PlaceListScreen(navController) }
        composable(AdminRoutes.DESTINO_CREATE) { PlaceCreateScreen(navController) }
        composable(
            AdminRoutes.DESTINO_EDITAR,
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { back ->
            val id = back.arguments?.getInt("id") ?: 0
            PlaceEditScreen(navController, placeId = id)
        }


        /* ---------- OTROS MÓDULOS ---------- */
        composable(AdminRoutes.RESERVAS)         { ReservationListScreen(navController) }
        composable(AdminRoutes.PAGOS)            { PaymentListScreen(navController) }
        composable(AdminRoutes.GALERIA_IMAGENES) { GaleriaScreen(navController) }
        composable(AdminRoutes.EDITAR_HOME)      { EditarHomeScreen(navController) }
        composable(AdminRoutes.MODO_EMPRENDEDOR) { ModoEmprendedorScreen(navController) }

        /* ---------- PLACEHOLDERS ---------- */
        composable(AdminRoutes.LUGARES_LIST)        { PlaceholderScreen("Lista de Lugares") }
        composable(AdminRoutes.LUGARES)             { PlaceholderScreen("Lugares Turísticos") }
        composable(AdminRoutes.USUARIOS)            { PlaceholderScreen("Usuarios") }
        composable(AdminRoutes.EDITAR_HERO)         { PlaceholderScreen("Editar Hero") }
        composable(AdminRoutes.EDITAR_HISTORIA)     { PlaceholderScreen("Editar Historia") }
        composable(AdminRoutes.TOURS_DESTACADOS)    { PlaceholderScreen("Tours Destacados") }
        composable(AdminRoutes.EDITAR_CONTACTO)     { PlaceholderScreen("Editar Contacto") }
        composable(AdminRoutes.EDITAR_DESTINOS)     { PlaceholderScreen("Editar Destinos") }
        composable(AdminRoutes.EDITAR_EXPERIENCIAS) { PlaceholderScreen("Editar Experiencias") }
    }
}

/* ---------- Placeholder ---------- */
@Composable
private fun PlaceholderScreen(title: String) {
    Text(
        "Pantalla: $title (en desarrollo)",
        Modifier.padding(32.dp),
        fontSize = 22.sp
    )
}
