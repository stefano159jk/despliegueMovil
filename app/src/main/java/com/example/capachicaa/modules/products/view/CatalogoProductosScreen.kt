package com.example.capachicaa.modules.products.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.modules.auth.controller.AuthController
import com.example.capachicaa.modules.products.controller.ProductController
import com.example.capachicaa.modules.products.model.Product
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogoProductosScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var products by remember { mutableStateOf<List<Product>>(emptyList()) }

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val authController = AuthController(context)
                val token = authController.getToken()
                if (!token.isNullOrEmpty()) {
                    val controller = ProductController()
                    val response = controller.getAllProducts(token)
                    if (response.isSuccessful) {
                        products = response.body() ?: emptyList()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Explora Productos Turísticos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            items(products) { product ->
                ProductCardUI(product = product, navController)
            }
        }
    }
}

@Composable
fun ProductCardUI(product: Product, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            // Categoría (si existe al menos una)
            if (product.categories.isNotEmpty()) {
                Text(
                    text = product.categories[0].name, // muestra la primera categoría
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(Color(0xFF0D6EFD), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Nombre del producto
            Text(
                text = product.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            // Descripción
            Text(
                text = product.description,
                fontSize = 14.sp,
                color = Color.Gray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Duración y precio
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "${product.duration} HRS", fontSize = 13.sp)
                Text(
                    text = "S/. ${product.price}",
                    color = Color(0xFF0D6EFD),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón de reservar
            Button(
                onClick = {
                    navController.navigate("detalle_producto/${product.id}")
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D6EFD))
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = null, Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text("Reservar ahora")
            }
        }
    }
}

