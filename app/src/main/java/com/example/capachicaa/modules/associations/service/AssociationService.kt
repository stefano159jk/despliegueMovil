package com.example.capachicaa.modules.admin.service

import com.example.capachicaa.modules.admin.model.Association
import com.example.capachicaa.modules.associations.model.AssociationRequest
import retrofit2.Response
import retrofit2.http.*

interface AssociationService {

    /** Obtener todas las asociaciones */
    @GET("associations")
    suspend fun getAssociations(): Response<List<Association>>

    /** Obtener una asociación por ID */
    @GET("associations/{id}")
    suspend fun getAssociation(@Path("id") id: Int): Response<Association>

    /** Crear una nueva asociación */
    @POST("associations")
    suspend fun createAssociation(@Body data: AssociationRequest): Response<Void>

    /** Actualizar una asociación existente */
    @PUT("associations/{id}")
    suspend fun updateAssociation(
        @Path("id") id: Int,
        @Body data: AssociationRequest
    ): Response<Void>

    /** Eliminar una asociación */
    @DELETE("associations/{id}")
    suspend fun deleteAssociation(@Path("id") id: Int): Response<Void>
}
