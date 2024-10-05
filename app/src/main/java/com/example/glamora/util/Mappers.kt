package com.example.glamora.util

import com.example.BrandsQuery
import com.example.DiscountCodesQuery
import com.example.PriceRulesQuery
import com.example.ProductQuery
import com.example.glamora.data.model.AvailableProductsModel
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.model.PriceRulesDTO
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.data.model.brandModel.Brands
import com.example.glamora.data.model.brandModel.Image


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

fun PriceRulesQuery.PriceRules.toPriceRulesDTO() : List<PriceRulesDTO>
{
    val priceRules = mutableListOf<PriceRulesDTO>()

    this.edges.forEach {
        val idList = it.node.id.split("/")
        priceRules.add(PriceRulesDTO(
            id = idList[idList.size - 1],
            percentage = it.node.value.onPriceRulePercentValue?.percentage ?: 0.0,
        ))
    }

    return priceRules
}

fun DiscountCodesQuery.CodeDiscountNodes.toDiscountCodesDTO() : List<DiscountCodeDTO> {
    val discountCodes = mutableListOf<DiscountCodeDTO>()

    this.nodes.forEach {
        val percentage = it.codeDiscount.onDiscountCodeBasic?.summary?.split("%")?.get(0)?.toDoubleOrNull()
        discountCodes.add(DiscountCodeDTO(
            id = it.id,
            code = it.codeDiscount.onDiscountCodeBasic?.title ?: "",
            percentage = percentage ?: 0.0,
        ))
    }

    return discountCodes
}

fun BrandsQuery.Collections.toBrandDTO(): List<Brands> {
    val brandsItem = mutableListOf<Brands>()

    this.nodes.forEachIndexed { index, node ->
        if (index > 0 && node.image != null) {
            val brandId = node.id

            val brand = Brands(
                id = brandId,
                image = Image(node.image.url.toString()),
                title = node.title
            )
            brandsItem.add(brand)
        }
    }

    return brandsItem
}






