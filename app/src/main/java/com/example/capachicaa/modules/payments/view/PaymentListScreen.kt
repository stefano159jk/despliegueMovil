package com.example.capachicaa.modules.payments.view

import android.content.Context
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.capachicaa.modules.dashboard.controller.SessionController
import com.example.capachicaa.modules.payments.controller.PaymentController
import com.example.capachicaa.modules.payments.model.Payment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import androidx.compose.foundation.Image
import coil.compose.rememberAsyncImagePainter


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PaymentListScreen(navController: NavController) {
    // Controladores y estado
    val context = LocalContext.current
    val controller = remember { PaymentController(context) }
    val scope = rememberCoroutineScope()
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Estados
    var payments by remember { mutableStateOf<List<Payment>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    // Filtros
    var fromDateTxt by remember { mutableStateOf("") }
    var toDateTxt by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val statuses = listOf("Todos", "pendiente", "enviado", "confirmado", "rechazado")
    var selectedStatus by remember { mutableStateOf(statuses.first()) }

    // Diálogos
    var toConfirm by remember { mutableStateOf<Payment?>(null) }
    var toReject by remember { mutableStateOf<Payment?>(null) }

    // Cargar datos iniciales
    LaunchedEffect(Unit) {
        fetchPayments(
            token = SessionController(context).getToken() ?: "",
            controller = controller,
            scope = scope
        ) { result ->
            payments = result
            loading = false
        }
    }


    // Paleta de colores premium
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFFF8F9FA), Color(0xFFE9ECEF))
    )
    val primaryColor = Color(0xFF4361EE)
    val successColor = Color(0xFF4CC9F0)
    val errorColor = Color(0xFFF72585)
    val surfaceColor = Color(0xFFFFFFFF)

    // Diseño principal
    Box(modifier = Modifier.fillMaxSize().background(gradientBackground)) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "GESTIÓN DE PAGOS",
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.sp
                                )
                            )
                            Text(
                                "Transacciones y confirmaciones",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f))
                        ) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Volver",
                                tint = primaryColor
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = surfaceColor,
                        titleContentColor = primaryColor
                    ),
                    modifier = Modifier.shadow(elevation = 8.dp)
                )
            },
            containerColor = Color.Transparent
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Sección de filtros (mejorada)
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { -40 },
                        animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy)
                    )
                ) {
                    FilterSection(
                        fromDateTxt = fromDateTxt,
                        toDateTxt = toDateTxt,
                        selectedStatus = selectedStatus,
                        expanded = expanded,
                        statuses = statuses,
                        onFromDateChange = { fromDateTxt = it },
                        onToDateChange = { toDateTxt = it },
                        onStatusSelected = { selectedStatus = it },
                        onExpandedChange = { expanded = it },
                        primaryColor = primaryColor
                    )
                }

                // Contenido principal (con animaciones)
                when {
                    loading -> LoadingState(primaryColor)
                    errorMsg != null -> ErrorState(errorMsg!!, errorColor)
                    else -> PaymentListContent(
                        payments = applyFilters(payments, fromDateTxt, toDateTxt, selectedStatus, formatter),
                        onConfirm = { toConfirm = it },
                        onReject = { toReject = it },
                        primaryColor = primaryColor,
                        successColor = successColor,
                        errorColor = errorColor
                    )
                }
            }
        }

        // Diálogos flotantes
        toConfirm?.let { payment ->
            ConfirmationDialog(
                title = "Confirmar Pago #${payment.id}",
                message = "¿Estás seguro de confirmar este pago? Se notificará al cliente.",
                confirmText = "Confirmar Transacción",
                confirmColor = successColor,
                icon = Icons.Default.Verified,
                onConfirm = {
                    toReject = null
                    scope.launch {
                        val token = SessionController(context).getToken() ?: ""
                        controller.rejectPayment(payment.id) // ← corregido
                        fetchPayments(token, controller, scope) { payments = it }
                    }
                }
                ,
                onDismiss = { toConfirm = null }
            )
        }

        toReject?.let { payment ->
            ConfirmationDialog(
                title = "Rechazar Pago #${payment.id}",
                message = "¿Deseas rechazar esta transacción? El cliente será notificado.",
                confirmText = "Rechazar Definitivamente",
                confirmColor = errorColor,
                icon = Icons.Default.Warning,
                onConfirm = {
                    toConfirm = null
                    scope.launch {
                        val token = SessionController(context).getToken() ?: ""
                        controller.confirmPayment(payment.id, token) // ← aquí estaba el error
                        fetchPayments(token, controller, scope) { payments = it }
                    }
                },
                onDismiss = { toReject = null }
            )
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSection(
    fromDateTxt: String,
    toDateTxt: String,
    selectedStatus: String,
    expanded: Boolean,
    statuses: List<String>,
    onFromDateChange: (String) -> Unit,
    onToDateChange: (String) -> Unit,
    onStatusSelected: (String) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    primaryColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
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
            Text(
                "🔍 Filtros Avanzados",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = fromDateTxt,
                    onValueChange = onFromDateChange,
                    label = { Text("Fecha Inicio") },
                    placeholder = { Text("dd/MM/aaaa") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            null,
                            tint = primaryColor.copy(alpha = 0.8f)
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = toDateTxt,
                    onValueChange = onToDateChange,
                    label = { Text("Fecha Fin") },
                    placeholder = { Text("dd/MM/aaaa") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            Icons.Default.DateRange,
                            null,
                            tint = primaryColor.copy(alpha = 0.8f)
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = Color.LightGray
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = onExpandedChange,
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedStatus,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Estado del Pago") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(
                                expanded = expanded
                            )
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = primaryColor // Aplica el color aquí
                            )
                        },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                Icons.Default.FilterAlt,
                                null,
                                tint = primaryColor.copy(alpha = 0.8f))
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { onExpandedChange(false) },
                        modifier = Modifier.background(Color.White)
                    ) {
                        statuses.forEach { status ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        status.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                },
                                onClick = {
                                    onStatusSelected(status)
                                    onExpandedChange(false)
                                },
                                leadingIcon = {
                                    Icon(
                                        when(status) {
                                            "confirmado" -> Icons.Default.CheckCircle
                                            "rechazado" -> Icons.Default.Cancel
                                            "enviado" -> Icons.Default.Send
                                            else -> Icons.Default.HourglassEmpty
                                        },
                                        contentDescription = null,
                                        tint = when(status) {
                                            "confirmado" -> Color(0xFF4CAF50)
                                            "rechazado" -> Color(0xFFF44336)
                                            "enviado" -> Color(0xFF2196F3)
                                            else -> Color(0xFFFF9800)
                                        }
                                    )
                                }
                            )
                        }
                    }
                }

                Button(
                    onClick = { /* Filtrado automático */ },
                    modifier = Modifier
                        .height(56.dp)
                        .weight(0.5f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryColor,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Icon(Icons.Default.FilterList, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Aplicar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PaymentListContent(
    payments: List<Payment>,
    onConfirm: (Payment) -> Unit,
    onReject: (Payment) -> Unit,
    primaryColor: Color,
    successColor: Color,
    errorColor: Color
) {
    if (payments.isEmpty()) {
        EmptyState(message = "No hay pagos que coincidan con los filtros seleccionados")
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(payments, key = { it.id ?: "" }) { payment ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically(
                        initialOffsetY = { 40 },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    )
                ) {
                    PaymentCard(
                        payment = payment,
                        onConfirm = { onConfirm(payment) },
                        onReject = { onReject(payment) },
                        primaryColor = primaryColor,
                        successColor = successColor,
                        errorColor = errorColor,
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentCard(
    payment: Payment,
    onConfirm: () -> Unit,
    onReject: () -> Unit,
    primaryColor: Color,
    successColor: Color,
    errorColor: Color,
    modifier: Modifier = Modifier
) {
    val status = payment.status ?: "pendiente"
    val isPending = status.lowercase() in listOf("pendiente", "enviado")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isPending) 8.dp else 4.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = if (isPending) primaryColor.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = if (isPending) CardDefaults.outlinedCardBorder() else null
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header con ID y estado
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "TRANSACCIÓN #${payment.id}",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = primaryColor.copy(alpha = 0.8f)
                    )
                )

                StatusBadge(
                    status = status,
                    primaryColor = primaryColor,
                    successColor = successColor,
                    errorColor = errorColor
                )
            }

            // Divisor decorativo
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color.LightGray.copy(alpha = 0.3f))
            )

            // Información del pago
            InfoRow(
                icon = Icons.Default.Person,
                text = payment.clientName ?: "Cliente no especificado",
                iconColor = primaryColor
            )

            InfoRow(
                icon = Icons.Default.ShoppingCart,
                text = payment.productName ?: "Producto no especificado",
                iconColor = primaryColor
            )
            InfoRow(
                icon = Icons.Default.AttachMoney,
                text = if (payment.amount != null) "Monto: S/ %.2f".format(payment.amount) else "Monto no especificado",
                iconColor = primaryColor
            )

            InfoRow(
                icon = Icons.Default.CalendarToday,
                text = "Fecha: ${payment.paidAt ?: "No especificada"}",
                iconColor = primaryColor
            )
            // Comprobante (si existe)
            payment.receiptUrl?.let { imageUrl ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Comprobante de Pago:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = primaryColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Image(
                    painter = rememberAsyncImagePainter(imageUrl),
                    contentDescription = "Comprobante de pago",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
            }

            // Acciones para pagos pendientes
            if (isPending) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = onConfirm,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = successColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        )
                    ) {
                        Icon(Icons.Default.Check, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Confirmar", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = errorColor
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            width = 1.5.dp
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Close, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Rechazar", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(
    status: String,
    primaryColor: Color,
    successColor: Color,
    errorColor: Color
) {
    val (backgroundColor, textColor) = when (status.lowercase()) {
        "confirmado" -> successColor to Color.White
        "rechazado" -> errorColor to Color.White
        "enviado" -> primaryColor to Color.White
        else -> Color(0xFFFFA000) to Color.Black // pendiente
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                when(status.lowercase()) {
                    "confirmado" -> Icons.Default.CheckCircle
                    "rechazado" -> Icons.Default.Cancel
                    "enviado" -> Icons.Default.Send
                    else -> Icons.Default.HourglassEmpty
                },
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
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
}

@Composable
private fun InfoRow(icon: ImageVector, text: String, iconColor: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconColor.copy(alpha = 0.8f),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.DarkGray.copy(alpha = 0.8f)
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
                "Cargando transacciones...",
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
                text = "¡Ups! Algo salió mal",
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
private fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                Icons.Default.Payments,
                contentDescription = "Sin pagos",
                tint = Color.LightGray.copy(alpha = 0.5f),
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No se encontraron pagos",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray.copy(alpha = 0.7f)
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String,
    confirmColor: Color,
    icon: ImageVector,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                icon,
                contentDescription = null,
                tint = confirmColor,
                modifier = Modifier.size(32.dp)
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = confirmColor,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Text(
                    text = confirmText,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    width = 1.5.dp
                )
            ) {
                Text("Cancelar")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = Color.White
    )
}

// Funciones de utilidad (sin cambios)
private fun fetchPayments(
    token: String,
    controller: PaymentController,
    scope: CoroutineScope,
    onResult: (List<Payment>) -> Unit
) {
    scope.launch {
        if (token.isNotBlank()) {
            val response = controller.getMyPayments(token)
            if (response.isSuccessful) {
                onResult(response.body() ?: emptyList())
            } else {
                onResult(emptyList())
            }
        } else {
            onResult(emptyList())
        }
    }
}


private fun applyFilters(
    payments: List<Payment>,
    fromDateTxt: String,
    toDateTxt: String,
    selectedStatus: String,
    formatter: DateTimeFormatter
): List<Payment> {
    var filtered = payments

    // Filtrar por fecha
    val from = parseDate(fromDateTxt, formatter)
    val to = parseDate(toDateTxt, formatter)

    if (from != null) {
        filtered = filtered.filter {
            it.confirmedAt?.let { d -> parseDate(d) }?.isAfter(from.minusDays(1)) ?: false
        }
    }

    if (to != null) {
        filtered = filtered.filter {
            it.confirmedAt?.let { d -> parseDate(d) }?.isBefore(to.plusDays(1)) ?: false
        }
    }

    // Filtrar por estado
    if (selectedStatus != "Todos") {
        filtered = filtered.filter {
            it.status.equals(selectedStatus, ignoreCase = true)
        }
    }

    return filtered
}

private fun parseDate(date: String, formatter: DateTimeFormatter =
    DateTimeFormatter.ofPattern("yyyy-MM-dd")
): LocalDate? = try {
    LocalDate.parse(date, formatter)
} catch (_: DateTimeParseException) {
    null
}