package com.example.glamora.data.contracts

import com.example.glamora.data.internetStateObserver.ConnectivityObserver
import com.example.glamora.data.model.AddressModel
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.data.model.customerModels.Customer
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.model.FavoriteItemDTO
import com.example.glamora.data.model.PriceRulesDTO
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.data.model.brandModel.Brands
import com.example.glamora.util.State
import com.example.glamora.data.model.citiesModel.CityForSearchItem
import com.example.glamora.data.model.customerModels.CustomerInfo
import com.example.glamora.data.model.ordersModel.OrderDTO
import kotlinx.coroutines.flow.Flow

interface Repository {

    //graphql
    fun getProducts() : Flow<State<List<ProductDTO>>>

    fun getPriceRules() : Flow<State<List<PriceRulesDTO>>>

    fun getDiscountCodes() : Flow<State<List<DiscountCodeDTO>>>

    fun getAllBrands(): Flow<State<List<Brands>>>

    fun getCartItemsForCustomer(customerId: String) : Flow<State<List<CartItemDTO>>>

    fun getFavoriteItemsForCustomer(customerId: String) : Flow<State<List<FavoriteItemDTO>>>

    fun updateCustomerAddress(customerId: String,address : List<AddressModel>) : Flow<State<AddressModel>>

    fun deleteDraftOrder(draftOrderId: String) : Flow<State<String>>

    fun updateCartDraftOrder(draftOrderId: String, newCartItemsList: List<CartItemDTO>) : Flow<State<String>>

    fun updateFavoritesDraftOrder(draftOrderId: String,newFavoriteItemsList: List<FavoriteItemDTO>,): Flow<State<String>>

    fun createFinalDraftOrder(
        customerId: String,
        customerEmail : String,
        cartItems : List<CartItemDTO>,
        discountAmount: Double,
        address: AddressModel
        ) : Flow<State<String>>

    fun createOrderFromDraftOrder(draftOrderId: String) : Flow<State<String>>

    fun getCustomerAddressesByEmail(email: String): Flow<State<List<AddressModel>>>

    fun createShopifyUser(
        email: String,
        firstName: String,
        lastName: String,
        phone: String?
    ): Flow<Result<CustomerInfo>>

    fun getShopifyUserByEmail(email: String): Flow<State<CustomerInfo>>

    fun getOrdersByCustomer (email: String):  Flow<State<List<OrderDTO>>>



    //retrofit
    fun getCustomerUsingEmail(email: String) : Flow<State<Customer>>
    fun getCitiesForSearch(name: String) : Flow<State<List<CityForSearchItem>>>
    fun convertCurrency() : Flow<State<Double>>


    //connectivity
    fun observeOnInternetState() : Flow<ConnectivityObserver.InternetState>


    //shared pref
    fun setSharedPrefString(key: String, value: String)

    fun getSharedPrefString(key: String, defaultValue: String) : String

    fun setSharedPrefBoolean(key: String, value: Boolean)

    fun getSharedPrefBoolean(key: String, defaultValue: Boolean) : Boolean



    // Firebase Functions
    fun loginWithEmail(email: String, password: String): Flow<Result<CustomerInfo>>
    fun loginWithGoogle(idToken: String): Flow<Result<CustomerInfo>>
    fun resetUserPassword(email: String): Flow<Result<CustomerInfo>>
    fun signUp(email: String, password: String): Flow<Result<CustomerInfo>>
    fun signOut()




}