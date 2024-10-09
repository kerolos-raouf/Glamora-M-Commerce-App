package com.example.glamora.data.repository.payPalRepo

import android.util.Base64
import android.util.Log
import com.example.glamora.data.model.payPalModels.Amount
import com.example.glamora.data.model.payPalModels.CaptureRequest
import com.example.glamora.data.model.payPalModels.OrderRequest
import com.example.glamora.data.model.payPalModels.OrderResponse
import com.example.glamora.data.network.payPalService.PayPalService
import com.example.glamora.util.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PayPalRepository @Inject constructor(
    private val payPalService: PayPalService,
) : IPayPalRepository {

    private var accessToken = ""

    // Function to fetch PayPal Access Token using Retrofit
    override suspend fun fetchAccessToken(): String {
        val authString = "${Constants.CLIENT_ID}:${Constants.SECRET_ID}"
        val encodedAuthString = "Basic " + Base64.encodeToString(authString.toByteArray(), Base64.NO_WRAP)

        return withContext(Dispatchers.IO) {
            try {
                val response = payPalService.fetchAccessToken(encodedAuthString)
                accessToken = response.access_token
                Log.i("Kerolos", "Access Token: $accessToken")

                accessToken
            } catch (e: Exception) {
                Log.e("Kerolos", "Error fetching token: ${e.localizedMessage}")
                throw e // Rethrow exception for the caller to handle
            }
        }
    }

    // Function to create an order
    override suspend fun createOrder(orderRequest: OrderRequest): OrderResponse {
        return withContext(Dispatchers.IO) {
            try {
                val response = payPalService.createOrder("Bearer $accessToken", orderRequest.purchase_units[0].reference_id, orderRequest)
                val orderId = response.id
                Log.d("Kerolos", "Order Created with ID: $orderId")
                response
            } catch (e: Exception) {
                Log.e("Kerolos", "Order creation error: ${e.localizedMessage}")
                throw e // Rethrow exception for the caller to handle
            }
        }
    }

    // Function to capture an order
    override suspend fun captureOrder(orderId: String, amount: String, token: String, payerId: String): String {
        val captureRequest = CaptureRequest(
            amount = Amount(
                currency_code = "USD",
                value = amount
            )
        )

        return withContext(Dispatchers.IO) {
            try {
                val response = payPalService.captureOrder("Bearer $token", orderId, captureRequest)
                val capturedOrderId = response.id
                Log.i("Kerolos", "Order Captured! ID: $capturedOrderId")
                capturedOrderId
            } catch (e: Exception) {
                Log.e("Kerolos", "Capture order error: ${e.localizedMessage}")
                throw e // Rethrow exception for the caller to handle
            }
        }
    }
}
