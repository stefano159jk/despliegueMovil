package com.example.capachicaa.modules.admin.controller

import com.example.capachicaa.modules.admin.model.Association
import com.example.capachicaa.modules.admin.service.AssociationService
import com.example.capachicaa.modules.associations.model.AssociationRequest
import com.example.capachicaa.modules.core.network.ApiClient
import retrofit2.Response

class AssociationController {

    // Instancia del servicio que maneja las llamadas a la API
    private val api = ApiClient.retrofit.create(AssociationService::class.java)

    /** Obtener lista de asociaciones */
    suspend fun getAllAssociations(): Response<List<Association>> =
        api.getAssociations()

    /** Obtener una asociación por su ID */
    suspend fun getAssociationById(id: Int): Response<Association> =
        api.getAssociation(id)

    /** Crear una nueva asociación */
    suspend fun createAssociation(data: AssociationRequest): Response<Void> =
        api.createAssociation(data)

    /** Actualizar una asociación existente */
    suspend fun updateAssociation(id: Int, data: AssociationRequest): Response<Void> =
        api.updateAssociation(id, data)

    /** Eliminar una asociación por su ID */
    suspend fun deleteAssociation(id: Int): Response<Void> =
        api.deleteAssociation(id)
}
