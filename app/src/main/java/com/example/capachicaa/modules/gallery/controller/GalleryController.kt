package com.example.capachicaa.modules.gallery.controller

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import com.example.capachicaa.modules.core.network.ApiClient
import com.example.capachicaa.modules.gallery.model.GalleryImage
import com.example.capachicaa.modules.gallery.service.GalleryService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

class GalleryController(private val context: Context) {

    private val service: GalleryService by lazy {
        ApiClient.getRetrofit(context).create(GalleryService::class.java)
    }

    /* ---- obtener imágenes ---- */
    suspend fun getImages(): Response<List<GalleryImage>> = withContext(Dispatchers.IO) {
        service.getImages()
    }

    /* ---- subir imagen ---- */
    suspend fun uploadImage(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        try {
            val input = context.contentResolver.openInputStream(uri)
                ?: return@withContext false

            val tempFile = File.createTempFile("upload_", ".jpg", context.cacheDir)
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }

            val requestFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())

            val body = MultipartBody.Part.createFormData("images[]", tempFile.name, requestFile)

            val response = service.uploadImage(body)

            if (!response.isSuccessful) {
                Log.e(
                    "UPLOAD_ERROR",
                    "Código: ${response.code()} – ${response.message()}\n${response.errorBody()?.string()}"
                )
            }

            response.isSuccessful
        } catch (e: Exception) {
            Log.e("UPLOAD_EXCEPTION", e.localizedMessage ?: "error", e)
            false
        }
    }



    /* -------- utilitario -------- */
    private fun copyUriToTempFile(uri: Uri): File {
        val resolver: ContentResolver = context.contentResolver
        val fileName = resolver.query(uri, null, null, null, null)?.use { c ->
            val nameIndex = c.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            c.moveToFirst()
            c.getString(nameIndex)
        } ?: "temp_${System.currentTimeMillis()}.jpg"

        val tempFile = File.createTempFile("upload_", fileName, context.cacheDir)
        resolver.openInputStream(uri).use { input ->
            FileOutputStream(tempFile).use { output ->
                input?.copyTo(output)
            }
        }
        return tempFile
    }
}
