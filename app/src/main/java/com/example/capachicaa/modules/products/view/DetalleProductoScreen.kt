package com.example.capachicaa.modules.products.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.capachicaa.modules.dashboard.controller.SessionController
import com.example.capachicaa.modules.products.controller.ProductController
import com.example.capachicaa.modules.products.model.Product
import kotlinx.coroutines.launch

@Composable
fun DetalleProductoScreen(
    navController: NavController,
    productId: Int
) {
    var product by remember { mutableStateOf<Product?>(null) }
    var cantidad by remember { mutableStateOf(1) }
    val scope = rememberCoroutineScope()
    val totalAmount = remember(product?.price, cantidad) {
        (product?.price ?: 0.0) * cantidad
    }
    val context = LocalContext.current
    val sessionController = remember { SessionController(context) }

    LaunchedEffect(productId) {
        scope.launch {
            val token = sessionController.getToken() ?: ""
            val response = ProductController().getProductById(token, productId)
            if (response.isSuccessful) {
                product = response.body()
            }
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        color = MaterialTheme.colorScheme.background
    ) {
        product?.let { producto ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth(0.95f)
                        .wrapContentHeight(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = producto.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Text(
                            text = "S/ ${producto.price}",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF0D6EFD)
                        )

                        Divider()

                        Text(
                            text = "📄 ${producto.description}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "⏱️ Duración: ${producto.duration}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text = "📦 Stock disponible: ${producto.stock}",
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Cantidad:", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(8.dp))
                            IconButton(
                                onClick = { if (cantidad > 1) cantidad-- },
                                colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFE0E0E0))

                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Restar")
                            }

                            Text(
                                text = "Total: S/ %.2f".format(totalAmount),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )

                            IconButton(
                                onClick = { if (cantidad < producto.stock) cantidad++ },
                                colors = IconButtonDefaults.iconButtonColors(containerColor = Color(0xFFE0E0E0))
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Sumar")
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val totalAmount = producto.price * cantidad
                                navController.navigate("reserva_pago/${producto.id}/${producto.name}/${cantidad}/${totalAmount}")

                            },
                            modifier = Modifier
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Reservar ahora")
                        }
                    }
                }
            }
        } ?: run {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}


