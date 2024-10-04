package com.example.glamora.data.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.BrandsQuery
import com.example.DiscountCodesQuery
import com.example.PriceRulesQuery
import com.example.ProductQuery
import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.CutomerModels.Customer
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.model.PriceRulesDTO
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.data.model.brandModel.Brands
import com.example.glamora.data.sharedPref.SharedPrefHandler
import com.example.glamora.util.Constants
import com.example.glamora.util.State
import com.example.glamora.util.toBrandDTO
import com.example.glamora.util.toDiscountCodesDTO
import com.example.glamora.util.toPriceRulesDTO
import com.example.glamora.util.toProductDTO
import com.example.type.CustomerInput
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.timeout
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class RepositoryImpl @Inject constructor(
    private val apolloClient: ApolloClient,
    private val remoteDataSource: RemoteDataSource,
    private val sharedPrefHandler: SharedPrefHandler
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

    @OptIn(FlowPreview::class)
    override fun getDiscountCodes(): Flow<State<List<DiscountCodeDTO>>> = flow {
        emit(State.Loading)
        try {

            val discountCodesRulesResponse = apolloClient.query(DiscountCodesQuery()).execute()
            if (discountCodesRulesResponse.data != null) {

                val discountCodesList = discountCodesRulesResponse.data?.codeDiscountNodes?.toDiscountCodesDTO()
                if (discountCodesList != null) {
                    emit(State.Success(discountCodesList))
                }else
                {
                    emit(State.Error("No products found"))
                }
            }else
            {
                emit(State.Error(discountCodesRulesResponse.errors.toString() ?: "Unknown Error"))
            }
        }catch (e : Exception)
        {
            emit(State.Error(e.message.toString()))
        }
    }.timeout(15.seconds).catch {
        emit(State.Error(it.message.toString()))
    }

    @OptIn(FlowPreview::class)
    override fun getAllBrands(): Flow<State<List<Brands>>> = flow {
        emit(State.Loading)
        try {

            val brandResponse = apolloClient.query(BrandsQuery()).execute()
            if (brandResponse.data != null) {
                val brandsList = brandResponse.data!!.collections.toBrandDTO()
                if (brandsList != null) {
                    emit(State.Success(brandsList))
                }else
                {
                    emit(State.Error("No Brands found"))
                }
            }else
            {
                emit(State.Error(brandResponse.errors.toString() ?: "Unknown Error"))
            }
        }catch (e : Exception)
        {
            emit(State.Error(e.message.toString()))
        }
    }
        .timeout(15.seconds).catch {
            emit(State.Error(it.message.toString()))
        }

    override fun getCustomerUsingEmail(email: String): Flow<State<Customer>> = flow {
        emit(State.Loading)
        try {
            val customerResponse = remoteDataSource.getCustomersUsingEmail(email)
            if (customerResponse.isSuccessful)
            {
                if (customerResponse.body()?.customers != null
                    && customerResponse.body()?.customers?.size!! > 0
                    && customerResponse.body()?.customers?.get(0) != null)
                {
                    emit(State.Success(customerResponse.body()?.customers?.get(0)!!))
                }else
                {
                    emit(State.Error(Constants.CUSTOMER_NOT_FOUND))
                }
            }else
            {
                emit(State.Error(customerResponse.message()))
            }
        }catch (e : Exception)
        {
            emit(State.Error(e.message.toString()))
        }
    }

    override fun setSharedPrefString(key: String, value: String) {
        sharedPrefHandler.setSharedPrefString(key, value)
    }

    override fun getSharedPrefString(key: String, defaultValue: String): String {
        return sharedPrefHandler.getSharedPrefString(key, defaultValue)
    }

    override fun setSharedPrefBoolean(key: String, value: Boolean) {
        sharedPrefHandler.setSharedPrefBoolean(key, value)
    }

    override fun getSharedPrefBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPrefHandler.getSharedPrefBoolean(key, defaultValue)
    }


    override suspend fun createShopifyUser(email: String, firstName: String, lastName: String, phone: String) {

        val client = apolloClient
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown"

        Log.d("Abanob", "createShopifyUser: $userId")

        // Create mutation to add the customer to Shopify
        val customerInput = CustomerInput(
            email = Optional.Present(email),
            firstName = Optional.Present(firstName),
            lastName = Optional.Present(lastName),
            tags = Optional.Present(listOf(userId))
        )

        // Create mutation with the CustomerInput
        val mutation = CreateCustomerMutation(customerInput)

        try {
            val response = client.mutation(mutation).execute()

            Log.d("Abanob", "createShopifyUser: ${response.data}")

            if (response.hasErrors()) {
                Log.e("Abanob", "Error creating user: ${response.errors}")
            } else {
                val shopifyUserId = response.data?.customerCreate?.customer?.id

                Log.d("Abanob", "User created successfully in Shopify: $shopifyUserId")
                Log.d("Abanob", "Firebase User ID: $userId, Shopify User ID: $shopifyUserId")

                // Ensure both User IDs are logged properly
                Log.d("Abanob", "Firebase ID: $userId")
                Log.d("Abanob", "Shopify ID: $shopifyUserId")

                // Optionally, you can return the Shopify user ID or handle it as needed
            }
        } catch (e: Exception) {
            Log.e("Abanob", "Mutation failed: ${e.message}")
        }
    }

}