package com.example.capachicaa.modules.payments.view

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import com.example.capachicaa.modules.dashboard.controller.SessionController
import com.example.capachicaa.modules.payments.controller.PaymentController
import com.example.capachicaa.modules.payments.model.Payment
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendedorPagosScreen(navController: NavController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val controller = remember { PaymentController(context) }
    val session = remember { SessionController(context) }

    var payments by remember { mutableStateOf<List<Payment>>(emptyList()) }
    var fullImageUrl by remember { mutableStateOf<String?>(null) }

    fun cargarPagos() {
        scope.launch {
            val token = session.getToken()
            if (!token.isNullOrBlank()) {
                val response = controller.getMyPayments(token)
                if (response.isSuccessful) {
                    payments = response.body() ?: emptyList()
                } else {
                    Toast.makeText(context, "Error al cargar pagos", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Token no disponible", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(true) {
        cargarPagos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("💸 Pagos Recibidos", fontWeight = FontWeight.Bold, fontSize = 20.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(payments) { payment ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F4F8))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("📄 Detalles del Pago", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF1B5E20))
                        Spacer(modifier = Modifier.height(8.dp))

                        PaymentRow("Cliente", payment.reservation?.user?.name)
                        PaymentRow("Correo", payment.reservation?.user?.email)
                        PaymentRow("Producto", payment.reservation?.product?.name)
                        PaymentRow("Monto", "S/ ${"%.2f".format(payment.amount ?: 0.0)}")
                        PaymentRow("Fecha", payment.paidAt)
                        PaymentRow("Estado", payment.status)

                        payment.receiptUrl?.let { url ->
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("🧾 Comprobante", fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(6.dp))
                            AsyncImage(
                                model = url,
                                contentDescription = "Comprobante de pago",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { fullImageUrl = url },
                                onState = {
                                    if (it is AsyncImagePainter.State.Error) {
                                        Toast.makeText(context, "Error cargando imagen", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }

                        if (payment.status == "pendiente" || payment.status == "enviado") {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = {
                                        scope.launch {
                                            val token = session.getToken()
                                            if (!token.isNullOrBlank()) {
                                                val result = controller.confirmPayment(payment.id, token)
                                                if (result.isSuccessful) {
                                                    Toast.makeText(context, "Pago confirmado correctamente", Toast.LENGTH_SHORT).show()
                                                    // Recargar pagos actualizados
                                                    val response = controller.getMyPayments(token)
                                                    if (response.isSuccessful) {
                                                        payments = response.body() ?: emptyList()
                                                    }
                                                } else {
                                                    Toast.makeText(context, "Error al confirmar pago: ${result.code()}", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                                ) {
                                    Text("Confirmar Pago", color = Color.White)
                                }

                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            val result = controller.rejectPayment(payment.id)
                                            if (result.isSuccessful) {
                                                Toast.makeText(context, "Pago rechazado correctamente", Toast.LENGTH_SHORT).show()
                                                cargarPagos()
                                            } else {
                                                Toast.makeText(context, "Error al rechazar pago", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                                ) {
                                    Text("Rechazar")
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    // Mostrar imagen en pantalla completa
    if (fullImageUrl != null) {
        AlertDialog(
            onDismissRequest = { fullImageUrl = null },
            confirmButton = {},
            text = {
                AsyncImage(
                    model = fullImageUrl,
                    contentDescription = "Vista completa del comprobante",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            },
            shape = RoundedCornerShape(12.dp),
            containerColor = Color.White
        )
    }
}

@Composable
fun PaymentRow(label: String, value: String?) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("$label:", fontWeight = FontWeight.Medium, fontSize = 14.sp)
        Text(value ?: "—", fontSize = 14.sp, color = Color.DarkGray)
    }
}
