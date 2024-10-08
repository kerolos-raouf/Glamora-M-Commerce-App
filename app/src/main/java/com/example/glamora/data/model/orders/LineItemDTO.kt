package com.example.glamora.data.model.orders

data class LineItemDTO(
    val name: String,
    val quantity: Int,
    val unitPrice: String,
    val currencyCode: String
)