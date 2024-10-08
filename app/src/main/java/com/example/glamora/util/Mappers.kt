package com.example.glamora.util

import com.example.BrandsQuery
import com.example.DiscountCodesQuery
import com.example.GetDraftOrdersByCustomerQuery
import com.example.PriceRulesQuery
import com.example.ProductQuery
import com.example.UpdateCustomerAddressMutation
import com.example.glamora.data.model.AddressModel
import com.example.glamora.data.model.AvailableProductsModel
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.model.FavoriteItemDTO
import com.example.glamora.data.model.PriceRulesDTO
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.data.model.brandModel.Brands
import com.example.glamora.data.model.brandModel.Image
import com.example.glamora.data.model.ordersModel.OrderDTO


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

fun UpdateCustomerAddressMutation.Address.toAddressModel(): AddressModel {
    val addressModel = AddressModel()

    addressModel.city = city ?: Constants.UNKNOWN
    addressModel.country = country ?: Constants.UNKNOWN
    addressModel.firstName = firstName ?: Constants.UNKNOWN
    addressModel.lastName = lastName ?: Constants.UNKNOWN
    addressModel.phone = phone ?: Constants.UNKNOWN
    addressModel.street = address1 ?: Constants.UNKNOWN


    return addressModel
}

fun GetDraftOrdersByCustomerQuery.DraftOrders.toCartItemsDTO() : List<CartItemDTO> {
    val cartItems = mutableListOf<CartItemDTO>()

    nodes.forEach {draftOrder->
        if(draftOrder.tags[0] == Constants.CART_DRAFT_ORDER_KEY)
        {
            draftOrder.lineItems.nodes.forEach { lineItem ->
                cartItems.add(CartItemDTO(
                    id = lineItem.variant?.id ?: Constants.UNKNOWN,
                    draftOrderId = draftOrder.id ?: Constants.UNKNOWN,
                    title = lineItem.title ?: Constants.UNKNOWN,
                    quantity = lineItem.quantity ?: 0,
                    inventoryQuantity = lineItem.variant?.inventoryQuantity ?: 0,
                    price = lineItem.variant?.price.toString() ?: "0",
                    image = lineItem.variant?.product?.media?.nodes?.get(0)?.onMediaImage?.image?.url?.toString() ?: Constants.UNKNOWN,
                ))
            }
        }
    }

    return cartItems
}

fun GetDraftOrdersByCustomerQuery.DraftOrders.toFavoriteItemsDTO() : List<FavoriteItemDTO> {
    val favoritesItems = mutableListOf<FavoriteItemDTO>()

    nodes.forEach {draftOrder->
        if(draftOrder.tags[0] == Constants.FAVORITES_DRAFT_ORDER_KEY)
        {
            draftOrder.lineItems.nodes.forEach { lineItem ->
                favoritesItems.add(FavoriteItemDTO(
                    id = lineItem.variant?.id ?: Constants.UNKNOWN,
                    draftOrderId = draftOrder.id ?: Constants.UNKNOWN,
                    title = lineItem.title ?: Constants.UNKNOWN,
                    price = lineItem.variant?.price.toString() ?: "0",
                    image = lineItem.variant?.product?.media?.nodes?.get(0)?.onMediaImage?.image?.url?.toString() ?: Constants.UNKNOWN,
                ))
            }
        }
    }

    return favoritesItems
}



//fun getOrdersByCustomer.Orders.toOrderDTO(): List<OrderDTO> {
//    val orders = mutableListOf<OrderDTO>()
//
//    this.edges.forEach { orderEdge ->
//        val orderNode = orderEdge.node
//        val lineItems = orderNode.lineItems.edges.map { lineItemEdge ->
//            val lineItemNode = lineItemEdge.node
//            LineItemDTO(
//                name = lineItemNode.name,
//                quantity = lineItemNode.quantity,
//                unitPrice = lineItemNode.originalUnitPriceSet.shopMoney.amount.toString(),
//                currencyCode = lineItemNode.originalUnitPriceSet.shopMoney.currencyCode
//            )
//        }
//
//        val orderDTO = OrderDTO(
//            id = orderNode.id,
//            name = orderNode.name,
//            createdAt = orderNode.createdAt,
//            totalPrice = orderNode.totalPriceSet.shopMoney.amount.toString(),
//            currencyCode = orderNode.totalPriceSet.shopMoney.currencyCode,
//            lineItems = lineItems
//        )
//
//        orders.add(orderDTO)
//    }
//
//    return orders
//}







