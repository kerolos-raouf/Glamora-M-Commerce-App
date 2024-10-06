package com.example.glamora.data.contracts

import com.example.glamora.data.internetStateObserver.ConnectivityObserver
import com.example.glamora.data.model.AddressModel
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.data.model.customerModels.Customer
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.model.PriceRulesDTO
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.data.model.brandModel.Brands
import com.example.glamora.util.State
import com.example.glamora.data.model.citiesModel.CityForSearchItem
import kotlinx.coroutines.flow.Flow

interface Repository {

    //graphql
    fun getProducts() : Flow<State<List<ProductDTO>>>

    fun getPriceRules() : Flow<State<List<PriceRulesDTO>>>

    fun getDiscountCodes() : Flow<State<List<DiscountCodeDTO>>>

    fun getAllBrands(): Flow<State<List<Brands>>>

    fun getCartItemsForCustomer(customerId: String) : Flow<State<List<CartItemDTO>>>

    fun updateCustomerAddress(customerId: String,address : AddressModel) : Flow<State<AddressModel>>

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

    suspend fun createShopifyUser(email: String, firstName: String, lastName: String, phone: String)


}