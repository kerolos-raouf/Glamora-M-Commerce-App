package com.example.glamora.data.model

data class ProductDTO(
    val id: String,
    val title: String,
    val brand : String,
    val category : String,
    val description: String,
    val mainImage: String,
    val availableProducts : List<AvailableProductsModel>,
    val images : List<String>,
    val availableColors : List<String>,
    val availableSizes : List<String>
)