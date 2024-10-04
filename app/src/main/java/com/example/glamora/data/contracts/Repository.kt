package com.example.glamora.data.contracts

import com.apollographql.apollo.api.ApolloResponse
import com.example.CreateCustomerMutation
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.util.State
import com.example.type.CustomerInput
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getProducts() : Flow<State<List<ProductDTO>>>

    suspend fun createShopifyUser(email: String, firstName: String, lastName: String, phone: String)

}