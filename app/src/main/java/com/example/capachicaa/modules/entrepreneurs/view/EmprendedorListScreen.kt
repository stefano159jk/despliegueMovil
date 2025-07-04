package com.example.capachicaa.modules.entrepreneurs.view

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.capachicaa.R
import com.example.capachicaa.modules.entrepreneurs.controller.EntrepreneurController
import com.example.capachicaa.modules.entrepreneurs.model.Entrepreneur
import kotlinx.coroutines.launch

private val categories   = listOf<String>()
private val associations = listOf<String>()

private val primaryColor   = Color(0xFF1565C0)
private val successColor   = Color(0xFF4CAF50)
private val errorColor     = Color(0xFFF44336)
private val borderGray     = Color(0xFFBDBDBD)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmprendedorListScreen(navController: NavController) {

    val ctrl = remember { EntrepreneurController() }
    val scope = rememberCoroutineScope()

    var listado     by remember { mutableStateOf<List<Entrepreneur>>(emptyList()) }
    var loading     by remember { mutableStateOf(true) }
    var errorMsg    by remember { mutableStateOf<String?>(null) }
    var toDelete    by remember { mutableStateOf<Entrepreneur?>(null) }

    var query       by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val resp = ctrl.getAll()
        loading = false
        if (resp.isSuccessful) listado = resp.body()?.data ?: emptyList()
        else errorMsg = "Error ${resp.code()}"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(painter = painterResource(R.drawable.ic_people), contentDescription = "Emprendedores", tint = Color.White)
                        Spacer(Modifier.width(12.dp))
                        Text("Gestión de Emprendedores")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = primaryColor,
                    titleContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { navController.navigate("emprendedorCreate") }) {
                        Icon(Icons.Default.Add, null, tint = Color.White)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("emprendedorCreate") },
                containerColor = primaryColor,
                contentColor   = Color.White,
            ) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(6.dp))
                Text("Nuevo")
            }
        }
    ) { inner ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(inner)
        ) {
            Card(
                Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        placeholder = { Text("Buscar emprendedores…") },
                        leadingIcon = { Icon(Icons.Default.Search, null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = primaryColor,
                            unfocusedBorderColor = borderGray
                        )
                    )

                    Spacer(Modifier.height(12.dp))
                }
            }

            when {
                loading -> LoadingBox()
                errorMsg != null -> ErrorBox(errorMsg!!)
                listado.isEmpty() -> EmptyBox(navController)
                else -> EntrepreneurList(listado, { navController.navigate("emprendedorEdit/${it.id}") }, { toDelete = it })
            }
        }
    }

    toDelete?.let { emp ->
        ConfirmDeleteDialog(
            name = emp.user?.name.orEmpty().ifBlank { "emprendedor" },
            onCancel = { toDelete = null },
            onConfirm = {
                scope.launch {
                    ctrl.delete(emp.id)
                    listado = listado.filterNot { it.id != emp.id }
                    toDelete = null
                }
            }
        )
    }
}

@Composable
private fun EntrepreneurList(
    listado: List<Entrepreneur>,
    onEdit: (Entrepreneur) -> Unit,
    onDelete: (Entrepreneur) -> Unit
) {
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        items(listado) { emp ->
            EntrepreneurCard(emp, { onEdit(emp) }, { onDelete(emp) }, Modifier.padding(vertical = 6.dp))
            Divider(color = borderGray, thickness = 0.5.dp)
        }
    }
}

@Composable
private fun EntrepreneurCard(
    entrepreneur: Entrepreneur,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier, elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            val displayName = entrepreneur.user?.name ?: entrepreneur.businessName ?: "Sin nombre"
            val email = entrepreneur.user?.email ?: "—"

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )

                val stateColor = if (entrepreneur.status == "activo") successColor else Color(0xFF757575)

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .background(stateColor, shape = MaterialTheme.shapes.small)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        entrepreneur.status.orEmpty().uppercase(),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Column {
                InfoRow(Icons.Default.Email, email)
                InfoRow(Icons.Default.Phone, entrepreneur.phone.orEmpty())
                InfoRow(Icons.Default.LocationOn, entrepreneur.district.orEmpty())
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, null, tint = primaryColor) }
                IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, null, tint = errorColor) }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, null, tint = borderGray, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(8.dp))
        Text(text.ifBlank { "—" }, fontSize = 13.sp)
    }
}

@Composable
private fun LoadingBox() {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        CircularProgressIndicator(color = primaryColor)
    }
}

@Composable
private fun ErrorBox(msg: String) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Text(msg, style = TextStyle(color = errorColor, fontSize = 16.sp))
    }
}

@Composable
private fun EmptyBox(navController: NavController) {
    Box(Modifier.fillMaxSize(), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.People, null, tint = primaryColor, modifier = Modifier.size(60.dp))
            Spacer(Modifier.height(8.dp))
            Text("No se encontraron emprendedores")
            Spacer(Modifier.height(12.dp))
            Button(onClick = { navController.navigate("emprendedorCreate") }) {
                Icon(Icons.Default.Add, null)
                Spacer(Modifier.width(6.dp))
                Text("Agregar")
            }
        }
    }
}

@Composable
private fun DropdownMenuCustom(
    label: String,
    items: List<String>,
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { Icon(Icons.Default.ArrowDropDown, null) },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = borderGray
            )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onItemSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun ConfirmDeleteDialog(
    name: String,
    onCancel: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text("Eliminar $name", fontWeight = FontWeight.Bold) },
        text = { Text("¿Estás seguro? Esta acción no se puede deshacer.") },
        confirmButton = {
            TextButton(onConfirm, colors = ButtonDefaults.textButtonColors(contentColor = errorColor)) {
                Text("Eliminar", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onCancel) { Text("Cancelar") }
        }
    )
}
