package com.example.glamora.data.contracts

import com.example.glamora.data.model.CutomerModels.Customer
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.model.PriceRulesDTO
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.data.model.brandModel.Brands
import com.example.glamora.util.State
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getProducts() : Flow<State<List<ProductDTO>>>

    fun getPriceRules() : Flow<State<List<PriceRulesDTO>>>

    fun getDiscountCodes() : Flow<State<List<DiscountCodeDTO>>>

    fun getAllBrands(): Flow<State<List<Brands>>>

    //retrofit
    fun getCustomerUsingEmail(email: String) : Flow<State<Customer>>


    //shared pref
    fun setSharedPrefString(key: String, value: String)

    fun getSharedPrefString(key: String, defaultValue: String) : String

    fun setSharedPrefBoolean(key: String, value: Boolean)

    fun getSharedPrefBoolean(key: String, defaultValue: Boolean) : Boolean

    suspend fun createShopifyUser(email: String, firstName: String, lastName: String, phone: String)


}