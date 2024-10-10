package com.example.glamora.data.model.ordersModel

data class LineItemDTO(
    val productId: String,
//    val id: String,
    val name: String,
    val quantity: Int,
    val unitPrice: String,
    val image:String,
    val currencyCode: String
)
