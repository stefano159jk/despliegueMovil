package com.example.capachicaa.modules.tours.view

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.modules.tours.controller.TourController
import com.example.capachicaa.modules.tours.model.Tour
import com.example.capachicaa.modules.tours.model.TourRequest
import kotlinx.coroutines.launch

@Composable
fun TourEditScreen(navController: NavController, tour: Tour) {

    var nombre by remember { mutableStateOf(tour.name) }
    var precio by remember { mutableStateOf(tour.price.toString()) }
    var descripcion by remember { mutableStateOf(tour.description) }
    var imagenUrl by remember { mutableStateOf(tour.image ?: "") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val controller = remember { TourController() }
    val scope = rememberCoroutineScope()
    val ctx = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("✏️ Editar Tour", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Button(onClick = { navController.popBackStack() }) {
                Text("Cancelar")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = nombre,
            onValueChange = { nombre = it },
            label = { Text("Nombre del Tour") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = precio,
            onValueChange = { precio = it },
            label = { Text("Precio") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = descripcion,
            onValueChange = { descripcion = it },
            label = { Text("Descripción") },
            modifier = Modifier.fillMaxWidth().height(100.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = imagenUrl,
            onValueChange = { imagenUrl = it },
            label = { Text("URL de la Imagen") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val req = TourRequest(
                    name = nombre.trim(),
                    description = descripcion.trim(),
                    price = precio.toDoubleOrNull() ?: 0.0,
                    image = imagenUrl.takeIf { it.isNotBlank() }
                )
                scope.launch {
                    isLoading = true
                    val res = controller.update(tour.id, req)
                    isLoading = false
                    if (res.isSuccessful) {
                        Toast.makeText(ctx, "Actualizado", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    } else {
                        errorMsg = "Error ${res.code()}"
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6FCF97))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text("Guardar cambios")
            }
        }

        errorMsg?.let {
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = it, color = Color.Red, fontSize = 14.sp)
        }
    }
}
