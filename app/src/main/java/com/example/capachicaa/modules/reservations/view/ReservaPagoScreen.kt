package com.example.capachicaa.modules.reservations.view

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.capachicaa.modules.auth.controller.AuthController
import com.example.capachicaa.modules.reservations.controller.ReservationController
import com.example.capachicaa.modules.reservations.model.ReservationRequest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.io.File
import com.example.capachicaa.modules.payments.controller.PaymentController
import com.example.capachicaa.modules.reservations.model.Reservation



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservaPagoScreen(
    navController: NavController,
    productId: Int,
    productName: String,
    totalAmount: Double
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var code by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    val date = remember { SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()) }
    var selectedImageUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri = uri
    }
    var selectedMethod by remember { mutableStateOf("yape") }
    val paymentMethods = listOf("yape", "plin", "efectivo")
    var expanded by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x99000000)), // fondo semi-transparente
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Spacer(Modifier.height(8.dp))
                Text("Subiendo comprobante...", color = Color.White)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirmar Reserva") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Top
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Resumen de Pago", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text("Producto: $productName")
                    Text("Total a pagar: S/ %.2f".format(totalAmount), color = MaterialTheme.colorScheme.primary)
                }
            }
            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Seleccionar Comprobante (imagen)")
            }

            selectedImageUri?.let { uri ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Imagen seleccionada:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(model = uri),
                    contentDescription = "Comprobante seleccionado",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp))
                )
            }


            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = code,
                onValueChange = { code = it },
                label = { Text("Código de operación") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Mensaje adicional (opcional)") },
                modifier = Modifier.fillMaxWidth()
            )



            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = selectedMethod,
                    onValueChange = {},
                    label = { Text("Método de pago") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    paymentMethods.forEach { method ->
                        DropdownMenuItem(
                            text = { Text(method.replaceFirstChar { it.uppercase() }) },
                            onClick = {
                                selectedMethod = method
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00796B)
                )
            ) {
                Text("Enviar Comprobante")
            }
        }
    }

    // Diálogo de confirmación
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("¿Confirmar reserva?") },
            text = { Text("Se enviará el comprobante y se creará una reserva pendiente.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (code.isBlank()) {
                            Toast.makeText(context, "Ingresa el código de operación", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }

                        if (selectedImageUri == null) {
                            Toast.makeText(context, "Selecciona una imagen del comprobante", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }

                        showDialog = false
                        isLoading = true // <-- activa loading

                        scope.launch {
                            try {
                                val auth = AuthController(context)
                                val token = auth.getToken()
                                val controller = ReservationController(context)
                                val paymentController = PaymentController(context)
                                val req = ReservationRequest(
                                    product_id = productId,
                                    quantity = 1,
                                    reservation_date = date,
                                    operation_code = code,
                                    message = message,
                                    receipt = null
                                )

                                val reservationResponse = controller.createReservation(token!!, req)
                                if (reservationResponse.isSuccessful) {
                                    val reservation = reservationResponse.body()?.reservation
                                    val file = selectedImageUri?.let {
                                        val inputStream = context.contentResolver.openInputStream(it)
                                        val tempFile = File.createTempFile("comprobante", ".jpg", context.cacheDir)
                                        tempFile.outputStream().use { output -> inputStream?.copyTo(output) }
                                        tempFile
                                    }

                                    val paymentSuccess = paymentController.enviarComprobantePago(
                                        token = token,
                                        reservationId = reservation!!.id,
                                        note = message,
                                        operationCode = code,
                                        paymentMethod = selectedMethod,
                                        imageFile = file
                                    )

                                    if (paymentSuccess) {
                                        Toast.makeText(context, "Comprobante enviado correctamente", Toast.LENGTH_SHORT).show()
                                        navController.popBackStack()
                                    } else {
                                        Toast.makeText(context, "Error al enviar comprobante", Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    Toast.makeText(context, "Error al crear reserva", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                            } finally {
                                isLoading = false // <-- desactiva loading siempre
                            }
                        }
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancelar")
                }
            }

        )
    }
}


