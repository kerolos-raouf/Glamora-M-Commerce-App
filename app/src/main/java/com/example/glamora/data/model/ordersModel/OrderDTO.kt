package com.example.glamora.data.model.ordersModel

data class OrderDTO(
    val id: String,
    val name: String,
    val createdAt: String,
    val totalPrice: String,
    val currencyCode: String,
    val lineItems: List<LineItemDTO>
)