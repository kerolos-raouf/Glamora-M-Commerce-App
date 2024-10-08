package com.example.glamora.data.repository.payPalRepo

import com.example.glamora.data.model.payPalModels.OrderRequest
import com.example.glamora.data.model.payPalModels.OrderResponse

interface IPayPalRepository {
    // Function to fetch PayPal Access Token using Retrofit
    suspend fun fetchAccessToken(): String

    // Function to create an order
    suspend fun createOrder(orderRequest: OrderRequest): OrderResponse

    // Function to capture an order
    suspend fun captureOrder(
        orderId: String,
        amount: String,
        token: String,
        payerId: String
    ): String
}