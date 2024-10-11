package com.example.glamora.data.apolloClientHandler

import com.example.glamora.data.contracts.IApolloClient
import com.example.glamora.data.model.AddressModel
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.model.FavoriteItemDTO
import com.example.glamora.data.model.PriceRulesDTO
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.data.model.brandModel.Brands
import com.example.glamora.data.model.customerModels.CustomerInfo
import com.example.glamora.data.model.ordersModel.OrderDTO
import com.example.glamora.util.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeApolloClientHandler : IApolloClient {
    override fun getProducts(): Flow<State<List<ProductDTO>>> {
        TODO("Not yet implemented")
    }

    override fun getPriceRules(): Flow<State<List<PriceRulesDTO>>> {
        TODO("Not yet implemented")
    }

    override fun getDiscountCodes(): Flow<State<List<DiscountCodeDTO>>> = flow {
        emit(State.Loading)
        val discountCodes = listOf(
            DiscountCodeDTO("1","Summer Sale",5.0),
            DiscountCodeDTO("2","Summer Sale",10.0),
            DiscountCodeDTO("3","Summer Sale",15.0),
            DiscountCodeDTO("4","Summer Sale",20.0),
            DiscountCodeDTO("5","Summer Sale",25.0),
        )

        emit(State.Success(discountCodes))
    }

    override fun getAllBrands(): Flow<State<List<Brands>>> {
        TODO("Not yet implemented")
    }

    override fun getCartItemsForCustomer(customerId: String): Flow<State<List<CartItemDTO>>> {
        TODO("Not yet implemented")
    }

    override fun getFavoriteItemsForCustomer(customerId: String): Flow<State<List<FavoriteItemDTO>>> {
        TODO("Not yet implemented")
    }

    override fun updateCustomerAddress(
        customerId: String,
        address: List<AddressModel>
    ): Flow<State<AddressModel>> {
        TODO("Not yet implemented")
    }

    override fun deleteDraftOrder(draftOrderId: String): Flow<State<String>> {
        TODO("Not yet implemented")
    }

    override fun updateCartDraftOrder(
        draftOrderId: String,
        newCartItemsList: List<CartItemDTO>
    ): Flow<State<String>> {
        TODO("Not yet implemented")
    }

    override fun updateFavoritesDraftOrder(
        draftOrderId: String,
        newFavoriteItemsList: List<FavoriteItemDTO>
    ): Flow<State<String>> {
        TODO("Not yet implemented")
    }

    override fun createFinalDraftOrder(
        customerId: String,
        customerEmail: String,
        cartItems: List<CartItemDTO>,
        discountAmount: Double,
        address: AddressModel,
        tag: String
    ): Flow<State<String>> {
        TODO("Not yet implemented")
    }

    override fun createOrderFromDraftOrder(draftOrderId: String): Flow<State<String>> {
        TODO("Not yet implemented")
    }

    override fun getCustomerAddressesByEmail(email: String): Flow<State<List<AddressModel>>> {
        TODO("Not yet implemented")
    }

    override fun updateCustomerDefaultAddress(
        customerId: String,
        addressId: String
    ): Flow<State<String>> {
        TODO("Not yet implemented")
    }

    override fun createShopifyUser(
        email: String,
        firstName: String,
        lastName: String,
        phone: String?
    ): Flow<Result<CustomerInfo>> {
        TODO("Not yet implemented")
    }

    override fun getShopifyUserByEmail(email: String): Flow<State<CustomerInfo>> {
        TODO("Not yet implemented")
    }

    override fun getOrdersByCustomer(email: String): Flow<State<List<OrderDTO>>> {
        TODO("Not yet implemented")
    }
}