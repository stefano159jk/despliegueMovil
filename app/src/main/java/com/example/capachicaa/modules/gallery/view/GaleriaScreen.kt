package com.example.capachicaa.modules.gallery.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.capachicaa.modules.gallery.controller.GalleryController
import com.example.capachicaa.modules.gallery.model.GalleryImage
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GaleriaScreen(navController: NavController) {

    /* ───── Controllers & Estado ───────────────────────────────────── */
    val context = LocalContext.current
    val galleryController = remember { GalleryController(context) }
    val scope = rememberCoroutineScope()

    var images by remember { mutableStateOf<List<GalleryImage>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var uploading by remember { mutableStateOf(false) }
    var selectedUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    /* ───── Selector de imagen ─────────────────────────────────────── */
    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        selectedUri = uri
    }

    /* ───── Cargar galería al inicio ───────────────────────────────── */
    LaunchedEffect(Unit) {
        val resp = galleryController.getImages()
        loading = false
        if (resp.isSuccessful) {
            images = resp.body() ?: emptyList()
        } else {
            errorMessage = "Error ${resp.code()}"
        }
    }

    /* ───── UI ─────────────────────────────────────────────────────── */
    Scaffold(
        topBar = {
            SmallTopAppBar(
                title = {
                    Column {
                        Text("🖼 Gestión de Galería", fontSize = 20.sp)
                        Text(
                            "Administra las imágenes de tu galería",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {

            /* ---------- Subir imágenes ---------- */
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    Text("📂 Subir Nuevas Imágenes", fontSize = 14.sp)

                    OutlinedTextField(
                        value = selectedUri?.lastPathSegment ?: "",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Seleccionar imágenes") },
                        placeholder = { Text("Selecciona imágenes") }
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(
                            onClick = { pickImage.launch("image/*") }
                        ) { Text("Seleccionar") }

                        Button(
                            onClick = {
                                selectedUri?.let { uri ->
                                    uploading = true
                                    scope.launch {
                                        val ok = galleryController.uploadImage(uri)
                                        uploading = false
                                        if (ok) {
                                            // refrescar galería
                                            loading = true
                                            val res = galleryController.getImages()
                                            loading = false
                                            if (res.isSuccessful) images = res.body() ?: emptyList()
                                            selectedUri = null
                                        } else {
                                            errorMessage = "No se pudo subir la imagen"
                                        }
                                    }
                                }
                            },
                            enabled = selectedUri != null && !uploading
                        ) {
                            if (uploading) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else {
                                Icon(Icons.Default.CloudUpload, null)
                                Spacer(Modifier.width(6.dp))
                                Text("Subir Imágenes")
                            }
                        }
                    }
                }
            }

            /* ---------- Mensaje / Error ---------- */
            errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            /* ---------- Galería ---------- */
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                when {
                    loading -> {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .height(200.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    images.isEmpty() -> {
                        Column(
                            Modifier
                                .fillMaxWidth()
                                .padding(32.dp)
                                .height(200.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                Icons.Default.Image,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text("No hay imágenes en la galería", fontSize = 14.sp)
                            Text(
                                "Comienza subiendo tus primeras imágenes",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    else -> {
                        LazyVerticalGrid(
                            columns = GridCells.Adaptive(110.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(images) { image ->
                                Image(
                                    painter = rememberAsyncImagePainter(image.url),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(110.dp)
                                        .aspectRatio(1f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
