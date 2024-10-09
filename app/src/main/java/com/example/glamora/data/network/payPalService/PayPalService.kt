package com.example.glamora.data.network.payPalService

import com.example.glamora.data.model.payPalModels.AccessTokenResponse
import com.example.glamora.data.model.payPalModels.CaptureRequest
import com.example.glamora.data.model.payPalModels.OrderRequest
import com.example.glamora.data.model.payPalModels.OrderResponse
import retrofit2.http.*


interface PayPalService {

    // Fetch Access Token
    @FormUrlEncoded
    @POST("v1/oauth2/token")
    suspend fun fetchAccessToken(
        @Header("Authorization") authorization: String,
        @Field("grant_type") grantType: String = "client_credentials"
    ): AccessTokenResponse

    // Create an Order
    @POST("v2/checkout/orders")
    suspend fun createOrder(
        @Header("Authorization") authorization: String,
        @Header("PayPal-Request-Id") requestId: String,
        @Body orderRequest: OrderRequest
    ): OrderResponse

    // Capture an Order
    @POST("v2/checkout/orders/{order_id}/capture")
    suspend fun captureOrder(
        @Header("Authorization") authHeader: String,
        @Path("order_id") orderId: String,
        @Body captureRequest: CaptureRequest
    ): OrderResponse
}