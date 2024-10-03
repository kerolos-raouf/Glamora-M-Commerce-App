package com.example.glamora.data.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.example.PriceRulesQuery
import com.example.ProductQuery
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.PriceRulesDTO
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.util.State
import com.example.glamora.util.toPriceRulesDTO
import com.example.glamora.util.toProductDTO
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.timeout
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class RepositoryImpl @Inject constructor(
    private val apolloClient: ApolloClient
) : Repository {


    @OptIn(FlowPreview::class)
    override fun getProducts(): Flow<State<List<ProductDTO>>> = flow {
        emit(State.Loading)
        try {

            val productsResponse = apolloClient.query(ProductQuery()).execute()
            if (productsResponse.data != null) {
                Log.d("Kerolos", "getProducts: ${productsResponse.data?.products}")
                val productList = productsResponse.data?.products?.toProductDTO()
                if (productList != null) {
                    emit(State.Success(productList))
                }else
                {
                    emit(State.Error("No products found"))
                }
            }else
            {
                emit(State.Error(productsResponse.errors.toString() ?: "Unknown Error"))
            }
        }catch (e : Exception)
        {
            emit(State.Error(e.message.toString()))
        }
    }.timeout(15.seconds).catch {
        emit(State.Error(it.message.toString()))
    }

    @OptIn(FlowPreview::class)
    override fun getPriceRules(): Flow<State<List<PriceRulesDTO>>> = flow {
        emit(State.Loading)
        try {

            val priceRulesResponse = apolloClient.query(PriceRulesQuery()).execute()
            if (priceRulesResponse.data != null) {

                Log.d("Kerolos", "getPriceRules: ${priceRulesResponse.data?.priceRules}")
                val priceRulesList = priceRulesResponse.data?.priceRules?.toPriceRulesDTO()
                if (priceRulesList != null) {
                    emit(State.Success(priceRulesList))
                }else
                {
                    emit(State.Error("No products found"))
                }
            }else
            {
                emit(State.Error(priceRulesResponse.errors.toString() ?: "Unknown Error"))
            }
        }catch (e : Exception)
        {
            emit(State.Error(e.message.toString()))
        }
    }.timeout(15.seconds).catch {
        emit(State.Error(it.message.toString()))
    }
}