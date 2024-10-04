package com.example.glamora.data.contracts

import com.example.glamora.data.model.CutomerModels.Customer
import com.example.glamora.data.model.CutomerModels.CustomerDTO
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.model.PriceRulesDTO
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.util.State
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getProducts() : Flow<State<List<ProductDTO>>>

    fun getPriceRules() : Flow<State<List<PriceRulesDTO>>>

    fun getDiscountCodes() : Flow<State<List<DiscountCodeDTO>>>

    fun getCustomerUsingEmail(email: String) : Flow<State<Customer>>
}