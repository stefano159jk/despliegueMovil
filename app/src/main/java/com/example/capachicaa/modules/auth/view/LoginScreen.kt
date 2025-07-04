package com.example.capachicaa.modules.auth.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.R
import com.example.capachicaa.modules.auth.controller.AuthController
import com.example.capachicaa.modules.dashboard.controller.SessionController
import kotlinx.coroutines.*

@Composable
fun LoginScreen(
    controller: AuthController,
    navController: NavController,
    onNavigateToRegister: () -> Unit,
    onAuthSuccess: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    val session = remember { SessionController(context) }


    val mountainDarkBlue = Color(0xFF1A2E4C)
    val waterBlue = Color(0xFF4A75B5)
    val forestGreen = Color(0xFF2D5542)
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
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(id = R.drawable.capachica_logo),
                contentDescription = "Capachica Tourism Logo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(bottom = 16.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "Descubre Capachica",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = mountainDarkBlue,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Inicia sesión para explorar la magia del turismo en Capachica",
                fontSize = 14.sp,
                color = mountainDarkBlue.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .padding(horizontal = 16.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correo electrónico") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = waterBlue)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = waterBlue,
                            focusedLabelColor = waterBlue
                        ),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contraseña") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = waterBlue)
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null,
                                    tint = waterBlue
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = waterBlue,
                            focusedLabelColor = waterBlue
                        ),
                        singleLine = true
                    )

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

                    Button(
                        onClick = {
                            if (email.isEmpty() || password.isEmpty()) {
                                error = "Por favor completa todos los campos"
                                return@Button
                            }

                            isLoading = true
                            error = ""

                            CoroutineScope(Dispatchers.IO).launch {
                                val response = controller.login(email.trim(), password)
                                withContext(Dispatchers.Main) {
                                    isLoading = false
                                    if (response != null) {
                                        // ✅ Guardar token, rol y entrepreneur_id de forma persistente
                                        session.saveToken(response.token)
                                        session.saveName(response.user.name)
                                        session.saveRole(response.roles.firstOrNull() ?: "")
                                        response.entrepreneur_id?.let { session.saveEntrepreneurId(it) }
                                        onAuthSuccess()
                                    } else {
                                        Toast.makeText(context, "Error inesperado", Toast.LENGTH_SHORT).show()
                                        error = "Correo o contraseña incorrectos"
                                    }
                                }
                            }

                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = forestGreen),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("INICIAR SESIÓN")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    TextButton(
                        onClick = onNavigateToRegister,
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("¿No tienes cuenta? Regístrate aquí", color = mountainDarkBlue)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .padding(top = 24.dp)
                            .clip(RoundedCornerShape(topStart = 100.dp, topEnd = 100.dp))
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(waterBlue.copy(alpha = 0.3f), waterBlue)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("CAPACHICA TOURISM", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}