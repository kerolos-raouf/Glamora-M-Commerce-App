package com.example.glamora.data.model.ordersModel

data class LineItemDTO(
    val name: String,
    val quantity: Int,
    val unitPrice: String,
    val currencyCode: String
)