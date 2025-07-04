package com.example.capachicaa.modules.entrepreneurs.view

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.modules.entrepreneurs.controller.EntrepreneurController
import com.example.capachicaa.modules.entrepreneurs.model.EntrepreneurRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendedorEditScreen(
    navController: NavController,
    entrepreneurId: Int          // coherente con AdminNavGraph
) {
    val ctrl  = remember { EntrepreneurController() }
    val scope = rememberCoroutineScope()

    /* ---- Estados del formulario ---- */
    var username      by remember { mutableStateOf("") }
    var email         by remember { mutableStateOf("") }
    var businessName  by remember { mutableStateOf("") }
    var phone         by remember { mutableStateOf("") }
    var district      by remember { mutableStateOf("") }
    var description   by remember { mutableStateOf("") }
    var categoriesTxt by remember { mutableStateOf("") }
    var status        by remember { mutableStateOf("inactivo") }

    var loading  by remember { mutableStateOf(true) }
    var saving   by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    /* ---- Cargar datos ---- */
    LaunchedEffect(entrepreneurId) {
        val resp = ctrl.getById(entrepreneurId)
        loading = false
        if (resp.isSuccessful) {
            resp.body()?.let { e ->
                username      = e.username.orEmpty()
                email         = e.email.orEmpty()
                businessName  = e.businessName.orEmpty()
                phone         = e.phone.orEmpty()
                district      = e.district.orEmpty()
                description   = e.description.orEmpty()
                categoriesTxt = e.categories.joinToString(",")
                status        = e.status.orEmpty()
            } ?: run { errorMsg = "Emprendedor no encontrado" }
        } else errorMsg = "Error ${resp.code()}"
    }

    /* ---- UI ---- */
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = { Text("Editar Emprendedor") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { pad ->
        if (loading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            Modifier
                .padding(pad)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            /* Campos principales */
            OutlinedTextField(username, { username = it }, label = { Text("Usuario") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(email, { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(businessName, { businessName = it }, label = { Text("Negocio") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(phone, { phone = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(district, { district = it }, label = { Text("Distrito") }, modifier = Modifier.fillMaxWidth())

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
            )

            OutlinedTextField(
                value = categoriesTxt,
                onValueChange = { categoriesTxt = it },
                label = { Text("Categorías (IDs separados por coma)") },
                supportingText = { Text("Ej: 1,2,3") },
                modifier = Modifier.fillMaxWidth()
            )

            EstadoDropdown(status) { status = it }

            errorMsg?.let {
                Text(it, color = MaterialTheme.colorScheme.error, fontSize = 13.sp)
            }

            Button(
                onClick = {
                    if (username.isBlank() || email.isBlank() || businessName.isBlank()) {
                        errorMsg = "Usuario, email y negocio son obligatorios"
                        return@Button
                    }
                    saving = true
                    errorMsg = null

                    scope.launch {
                        val req = EntrepreneurRequest(
                            username      = username,
                            email         = email,
                            password      = null, // no se cambia contraseña aquí
                            businessName  = businessName,
                            phone         = phone,
                            district      = district,
                            description   = description.takeIf { it.isNotBlank() },
                            associationId = null,
                            placeId       = null,
                            lat           = null,
                            lng           = null,
                            categories    = categoriesTxt.split(',')
                                .mapNotNull { it.trim().toIntOrNull() },
                            status        = status
                        )
                        val resp = ctrl.update(entrepreneurId, req)
                        saving = false
                        if (resp.isSuccessful) navController.popBackStack()
                        else errorMsg = "Error ${resp.code()}"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                if (saving) CircularProgressIndicator(strokeWidth = 2.dp)
                else {
                    Icon(Icons.Default.Save, null)
                    Spacer(Modifier.width(6.dp))
                    Text("Guardar cambios")
                }
            }
        }
    }
}

/* ---- Dropdown simplificado para estado ---- */
@Composable
private fun EstadoDropdown(
    selected: String,
    onSelect: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Text("Estado", fontSize = 13.sp, fontWeight = FontWeight.Medium)
        OutlinedButton(
            onClick = { expanded = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(selected.ifBlank { "Seleccionar..." })
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            listOf("activo", "inactivo").forEach { state ->
                DropdownMenuItem(
                    text = { Text(state) },
                    onClick = { onSelect(state); expanded = false }
                )
            }
        }
    }
}
