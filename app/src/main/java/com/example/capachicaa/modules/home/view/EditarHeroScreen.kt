package com.example.capachicaa.modules.home.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarHeroScreen(navController: NavController) {
    var titulo by remember { mutableStateOf("") }
    var colorTitulo by remember { mutableStateOf("#112037") }
    var tamañoTitulo by remember { mutableStateOf("3rem") }
    var descripcion by remember { mutableStateOf("") }
    var colorFondo by remember { mutableStateOf("#ffffff") }

    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("✂️ Editar Página Principal") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            Button(onClick = { /* acción futura */ }) {
                Text("🟣 Nueva Configuración")
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
            ) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    Text("📝 Crear Nueva Configuración", fontSize = 18.sp)

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            label = { Text("Título Principal") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = colorTitulo,
                            onValueChange = { colorTitulo = it },
                            label = { Text("Color del Título") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = tamañoTitulo,
                        onValueChange = { tamañoTitulo = it },
                        label = { Text("Tamaño del Título") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = descripcion,
                        onValueChange = { descripcion = it },
                        label = { Text("Descripción") },
                        modifier = Modifier.fillMaxWidth(),
                        minLines = 4
                    )

                    OutlinedTextField(
                        value = colorFondo,
                        onValueChange = { colorFondo = it },
                        label = { Text("Color de Fondo") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text("📷 Imágenes Destacadas", fontSize = 16.sp)
                    OutlinedButton(
                        onClick = { /* subir imágenes */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("📁 Seleccionar imágenes")
                    }

                    Button(
                        onClick = { /* guardar */ },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00C853))
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Guardar")
                    }
                }
            }
        }
    }
}
