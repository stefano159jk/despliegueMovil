package com.example.capachicaa.modules.payments.controller

import android.content.Context
import android.util.Log
import com.example.capachicaa.modules.core.network.ApiClient
import com.example.capachicaa.modules.payments.model.Payment
import com.example.capachicaa.modules.payments.model.PaymentResponse
import com.example.capachicaa.modules.payments.service.PaymentService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import java.io.File
import retrofit2.Response

class PaymentController(private val context: Context) {

    private val api = ApiClient.getRetrofit(context).create(PaymentService::class.java)

    suspend fun getMyPayments(token: String): Response<List<Payment>> {
        return api.getMyPayments("Bearer $token")
    }

    suspend fun rejectPayment(id: Int): Response<PaymentResponse> {
        return api.reject(id)
    }

    // 🔽 Nuevo método para enviar comprobante de pago
    suspend fun enviarComprobantePago(
        token: String,
        reservationId: Int,
        note: String?,
        operationCode: String?,
        paymentMethod: String,
        imageFile: File?
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val data = mutableMapOf<String, RequestBody>()
            data["reservation_id"] = RequestBody.create("text/plain".toMediaTypeOrNull(), reservationId.toString())
            data["payment_method"] = RequestBody.create("text/plain".toMediaTypeOrNull(), paymentMethod)

            note?.let {
                data["note"] = RequestBody.create("text/plain".toMediaTypeOrNull(), it)
            }
            operationCode?.let {
                data["operation_code"] = RequestBody.create("text/plain".toMediaTypeOrNull(), it)
            }

            val imagePart = imageFile?.let {
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), it)
                MultipartBody.Part.createFormData("image_file", it.name, requestFile)
            }

            val response = api.enviarPago("Bearer $token", data, imagePart)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("PaymentController", "Error al enviar comprobante: ${e.message}")
            false
        }
    }
    suspend fun confirmPayment(paymentId: Int, token: String): Response<ResponseBody> {
        return api.confirmPayment(paymentId, "Bearer $token")
    }

}
