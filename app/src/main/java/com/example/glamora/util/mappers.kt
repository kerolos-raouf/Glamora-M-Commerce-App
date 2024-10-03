package com.example.glamora.util

import com.example.ProductQuery
import com.example.glamora.data.model.AvailableProductsModel
import com.example.glamora.data.model.ProductDTO

fun ProductQuery.Products.toProductDTO() : List<ProductDTO>
{
    val products = mutableListOf<ProductDTO>()

    this.edges.forEach { product ->
        products.add(ProductDTO(
            id = product.node.id,
            title = product.node.title,
            brand = product.node.vendor,
            category = product.node.productType,
            description = product.node.description,
            mainImage = product.node.featuredImage?.url.toString(),
            images = product.node.media.edges.map { it.node.preview?.image?.url.toString() },
            availableColors = product.node.options[1].values.toList(),
            availableSizes = product.node.options[0].values.toList(),
            availableProducts = product.node.variants.edges.map {
                AvailableProductsModel(
                    id = it.node.id,
                    price = it.node.price.toString(),
                    color = it.node.selectedOptions[1].value,
                    size = it.node.selectedOptions[0].value,
                    quantity = it.node.inventoryQuantity ?: 0)
            }
        ))
    }
    return products
}