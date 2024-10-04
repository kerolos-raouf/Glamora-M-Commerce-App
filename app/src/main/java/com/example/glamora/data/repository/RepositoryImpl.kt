package com.example.glamora.data.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.apollographql.apollo.api.Optional
import com.example.CreateCustomerMutation
import com.example.ProductQuery
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.util.State
import com.example.glamora.util.toProductDTO
import com.example.type.CustomerInput
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.withContext
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
                } else {
                    emit(State.Error("No products found"))
                }
            } else {
                emit(State.Error(productsResponse.errors.toString() ?: "Unknown Error"))
            }
        } catch (e: Exception) {
            emit(State.Error(e.message.toString()))
        }
    }.timeout(15.seconds).catch {
        emit(State.Error(it.message.toString()))
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