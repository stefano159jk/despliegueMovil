package com.example.capachicaa.modules.tours.view

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.modules.tours.controller.TourController
import com.example.capachicaa.modules.tours.model.TourRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TourCreateScreen(navController: NavController) {

    /* ──────────── ESTADOS ──────────── */
    var nombre by remember { mutableStateOf("") }
    var precio by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var imagenUrl by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    /* ──────────── CONTROLADORES ──────────── */
    val controller = remember { TourController() }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    /* ──────────── GRADIENTE & COLORES ──────────── */
    val gradientBackground = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFF5F7FA),
            Color(0xFFE4E8F0)
        )
    )
    val primaryColor = Color(0xFF4A6FA5)
    val successColor = Color(0xFF2ECC71)
    val errorColor = Color(0xFFE74C3C)

    /* ──────────── UI PRINCIPAL ──────────── */
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(gradientBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState)
        ) {
            /* ──────────── HEADER ──────────── */
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "✨ Crear Nuevo Tour",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Diseña experiencias memorables para tus clientes",
                        style = TextStyle(
                            fontSize = 14.sp,
                            color = Color.DarkGray.copy(alpha = 0.7f)
                        )
                    )
                }

                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            /* ──────────── FORMULARIO ──────────── */
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        spotColor = primaryColor.copy(alpha = 0.1f)
                    ),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Campo: Nombre
                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre del Tour") },
                        placeholder = { Text("Ej: Tour Premium a las Islas Flotantes") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        textStyle = TextStyle(fontSize = 16.sp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo: Precio
                    OutlinedTextField(
                        value = precio,
                        onValueChange = { precio = it },
                        label = { Text("Precio (S/.)") },
                        placeholder = { Text("Ej: 299.99") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        prefix = { Text("S/ ", color = Color.Gray) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo: Descripción
                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción Detallada") },
                        placeholder = { Text("Incluye actividades, horarios, comidas y experiencias únicas...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp),
                        maxLines = 5
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo: URL de Imagen
                    OutlinedTextField(
                        value = imagenUrl,
                        onValueChange = { imagenUrl = it },
                        label = { Text("URL de la Imagen Principal") },
                        placeholder = { Text("https://ejemplo.com/imagen-tour.jpg") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Mensaje de Error (Animado)
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn(animationSpec = tween(300)),
                        exit = fadeOut(animationSpec = tween(200))
                    ) {
                        errorMessage?.let { message ->
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = message,
                                color = errorColor,
                                style = TextStyle(
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }

                    // Botón de Guardar (Mejorado)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            // Validaciones
                            when {
                                nombre.isBlank() || precio.isBlank() || descripcion.isBlank() -> {
                                    errorMessage = "⚠️ Completa todos los campos requeridos"
                                    return@Button
                                }
                                precio.toDoubleOrNull() == null -> {
                                    errorMessage = "💰 El precio debe ser un número válido"
                                    return@Button
                                }
                            }

                            errorMessage = null
                            isLoading = true

                            // Crear solicitud
                            val req = TourRequest(
                                name = nombre.trim(),
                                description = descripcion.trim(),
                                price = precio.toDouble(),
                                image = imagenUrl.takeIf { it.isNotBlank() }
                            )

                            // Llamada al backend
                            scope.launch {
                                val resp = controller.create(req)
                                isLoading = false
                                if (resp.isSuccessful) {
                                    Toast.makeText(
                                        context,
                                        "✅ Tour creado exitosamente!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    navController.popBackStack()
                                } else {
                                    errorMessage = "❌ Error ${resp.code()}: ${resp.message()}"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = successColor,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 3.dp,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Icon(
                                Icons.Default.Save,
                                contentDescription = "Guardar",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "GUARDAR TOUR",
                                style = TextStyle(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}