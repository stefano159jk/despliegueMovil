package com.example.capachicaa.modules.auth.view


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.capachicaa.R
import com.example.capachicaa.modules.auth.controller.AuthController
import kotlinx.coroutines.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    controller: AuthController,
    onSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("cliente") }
    var expanded by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val roleOptions = listOf("cliente", "emprendedor", "super-admin")

    // Definición de colores del tema de turismo Capachica (basados en el logo)
    val mountainDarkBlue = Color(0xFF1A2E4C)
    val waterBlue = Color(0xFF4A75B5)
    val forestGreen = Color(0xFF2D5542)
    val mountainBrown = Color(0xFF9B7E6B)
    val skyBlue = Color(0xFF87AACD)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(skyBlue.copy(alpha = 0.3f), Color.White)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Logo más pequeño que en la pantalla de login
            Image(
                painter = painterResource(id = R.drawable.capachica_logo),
                contentDescription = "Capachica Tourism Logo",
                modifier = Modifier
                    .size(150.dp)
                    .padding(bottom = 8.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Únete a la comunidad Capachica",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = mountainDarkBlue,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Crea tu cuenta para disfrutar de todos nuestros servicios turísticos",
                fontSize = 14.sp,
                color = mountainDarkBlue.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .padding(horizontal = 16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    // Nombre field
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre completo") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Nombre Icon",
                                tint = waterBlue
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = waterBlue,
                            focusedLabelColor = waterBlue
                        ),
                        singleLine = true
                    )

                    // Email field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = "Email Icon",
                                tint = waterBlue
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = waterBlue,
                            focusedLabelColor = waterBlue
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )

                    // Password field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Password Icon",
                                tint = waterBlue
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility",
                                    tint = waterBlue
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = waterBlue,
                            focusedLabelColor = waterBlue
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )

                    // Confirm Password field
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirmar contraseña") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Confirm Password Icon",
                                tint = waterBlue
                            )
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = waterBlue,
                            focusedLabelColor = waterBlue
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )

                    // Role dropdown
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            readOnly = true,
                            value = role,
                            onValueChange = {},
                            label = { Text("Tipo de usuario") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Role Icon",
                                    tint = waterBlue
                                )
                            },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = waterBlue,
                                focusedLabelColor = waterBlue
                            )
                        )

                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            roleOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            when(option) {
                                                "cliente" -> "Cliente (Turista)"
                                                "emprendedor" -> "Emprendedor (Negocio)"
                                                "super-admin" -> "Administrador"
                                                else -> option
                                            }
                                        )
                                    },
                                    onClick = {
                                        role = option
                                        expanded = false
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = when(option) {
                                                "cliente" -> Icons.Default.Person
                                                "emprendedor" -> Icons.Default.Business
                                                "super-admin" -> Icons.Default.AdminPanelSettings
                                                else -> Icons.Default.Person
                                            },
                                            contentDescription = null,
                                            tint = waterBlue
                                        )
                                    }
                                )
                            }
                        }
                    }

                    // Error message if any
                    if (error.isNotEmpty()) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 14.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Register button
                    Button(
                        onClick = {
                            // Validaciones
                            when {
                                name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() -> {
                                    error = "Por favor completa todos los campos"
                                    return@Button
                                }
                                password != confirmPassword -> {
                                    error = "Las contraseñas no coinciden"
                                    return@Button
                                }
                                password.length < 6 -> {
                                    error = "La contraseña debe tener al menos 6 caracteres"
                                    return@Button
                                }
                                !email.contains("@") -> {
                                    error = "Por favor ingresa un correo válido"
                                    return@Button
                                }
                            }

                            isLoading = true
                            error = ""

                            CoroutineScope(Dispatchers.IO).launch {
                                val response = controller.register(name, email, password, role)
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    if (response != null) {
                                        onSuccess()
                                    } else {
                                        error = "Error al registrar usuario. Inténtalo de nuevo"
                                    }
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = forestGreen
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text("CREAR CUENTA")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = { onNavigateToLogin() },
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    "¿Ya tienes una cuenta? Inicia sesión",
                    color = mountainDarkBlue
                )
            }

            // Footer con wave decoration
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .weight(1f),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .clip(RoundedCornerShape(topStart = 100.dp, topEnd = 100.dp))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(waterBlue.copy(alpha = 0.3f), waterBlue)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "CAPACHICA TOURISM",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}