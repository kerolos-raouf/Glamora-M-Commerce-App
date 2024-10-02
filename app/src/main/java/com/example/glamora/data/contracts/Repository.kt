package com.example.glamora.data.contracts

import com.example.glamora.data.model.ProductDTO
import com.example.glamora.util.State
import kotlinx.coroutines.flow.Flow

interface Repository {

    fun getProducts() : Flow<State<List<ProductDTO>>>

}