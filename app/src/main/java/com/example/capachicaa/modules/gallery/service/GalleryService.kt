package com.example.capachicaa.modules.gallery.service

import com.example.capachicaa.modules.gallery.model.GalleryImage
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface GalleryService {


    @GET("gallery")
    suspend fun getImages(): Response<List<GalleryImage>>

    @Multipart
    @POST("gallery")
    suspend fun uploadImage(
        @Part image: MultipartBody.Part
    ): Response<Void>
}
