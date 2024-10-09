package com.example.glamora.data.model

data class CartItemDTO(
    val id : String,
    val productId : String,
    val draftOrderId : String,
    val title : String,
    var quantity : Int,
    val inventoryQuantity : Int,
    val price : String,
    val image : String,
    val isFavorite : Boolean = false
)
