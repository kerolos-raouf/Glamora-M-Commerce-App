package com.example.glamora.data.repository

import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.example.BrandsQuery
import com.example.CreateCustomerMutation
import com.example.CreateDrafterOrderMutation
import com.example.CreateOrderFromDraftOrderMutation
import com.example.DeleteDraftOrderMutation
import com.example.DiscountCodesQuery
import com.example.GetDraftOrdersByCustomerQuery
import com.example.PriceRulesQuery
import com.example.ProductQuery
import com.example.UpdateCustomerAddressMutation
import com.example.UpdateDraftOrderMutation
import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.firebase.IFirebaseHandler
import com.example.glamora.data.internetStateObserver.ConnectivityObserver
import com.example.glamora.data.model.AddressModel
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.data.model.customerModels.Customer
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.model.PriceRulesDTO
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.data.model.brandModel.Brands
import com.example.glamora.data.sharedPref.SharedPrefHandler
import com.example.glamora.util.Constants
import com.example.glamora.util.State
import com.example.glamora.util.toAddressModel
import com.example.glamora.util.toBrandDTO
import com.example.glamora.util.toDiscountCodesDTO
import com.example.glamora.util.toPriceRulesDTO
import com.example.glamora.util.toProductDTO
import com.example.glamora.data.model.citiesModel.CityForSearchItem
import com.example.glamora.util.toCartItemsDTO
import com.example.type.CustomerInput
import com.example.type.DraftOrderDeleteInput
import com.example.type.DraftOrderInput
import com.example.type.DraftOrderLineItemInput
import com.example.type.MailingAddressInput
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
    private val sharedPrefHandler: SharedPrefHandler,
    private val connectivityObserver: ConnectivityObserver,
    private val firebaseHandler: IFirebaseHandler
) : Repository {


    //graph queries
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
    }.timeout(15.seconds).catch {
            emit(State.Error(it.message.toString()))
    }

    override fun getCartItemsForCustomer(customerId: String) : Flow<State<List<CartItemDTO>>> = flow {
        emit(State.Loading)
        try {

            val cartItemsResponse = apolloClient.query(GetDraftOrdersByCustomerQuery(
                query = "customer_id:$customerId"
            )).execute()
            if (cartItemsResponse.data != null) {

                val draftOrdersResponse = cartItemsResponse.data?.draftOrders
                if (draftOrdersResponse != null) {
                    emit(State.Success(draftOrdersResponse.toCartItemsDTO()))
                }else
                {
                    emit(State.Error("No products found"))
                }
            }else
            {
                emit(State.Error(cartItemsResponse.errors.toString() ?: "Unknown Error"))
            }
        }catch (e : Exception)
        {
            emit(State.Error(e.message.toString()))
        }
    }.timeout(15.seconds).catch {
        emit(State.Error(it.message.toString()))
    }

    @OptIn(FlowPreview::class)
    override fun updateCustomerAddress(
        customerId: String,
        address: AddressModel
    ): Flow<State<AddressModel>> = flow {
        emit(State.Loading)
        try {
            val address = MailingAddressInput(
                firstName = Optional.Present(address.firstName),
                lastName = Optional.Present(address.lastName),
                phone = Optional.Present(address.phone),
                address1 = Optional.Present(address.street),
                city = Optional.Present(address.city),
                country = Optional.Present(address.country),
            )
            val customerInput = CustomerInput(
                id = Optional.Present(customerId),
                addresses = Optional.Present(listOf(address))
            )
            val customerResponse = apolloClient.mutation(UpdateCustomerAddressMutation(customerInput)).execute()
            if (!customerResponse.hasErrors() && customerResponse.data?.customerUpdate?.customer != null)
            {
                val addressModel = customerResponse.data?.customerUpdate?.customer?.addresses?.get(0)?.toAddressModel()
                if (addressModel != null) {
                    emit(State.Success(addressModel))
                }else {
                    emit(State.Error(Constants.CUSTOMER_NOT_FOUND))
                }
            }else
            {
                emit(State.Error(Constants.CUSTOMER_NOT_FOUND))
            }
        }catch (e : Exception)
        {
            emit(State.Error(e.message.toString()))
        }
    }.timeout(15.seconds).catch {
        emit(State.Error(it.message.toString()))
    }

    override fun deleteDraftOrder(draftOrderId: String): Flow<State<String>>  = flow {
        emit(State.Loading)
        try {
            val draftOrderDeletingResponse = apolloClient.mutation(DeleteDraftOrderMutation(
                DraftOrderDeleteInput(
                    id = draftOrderId
                )
            )).execute()

            if (!draftOrderDeletingResponse.hasErrors())
            {
                emit(State.Success(draftOrderDeletingResponse.data?.draftOrderDelete?.deletedId.toString()))
            }else
            {
                emit(State.Error(draftOrderDeletingResponse.errors?.get(0)?.message.toString() ?: "Unknown Error"))
            }
        }catch (e : Exception)
        {
            emit(State.Error(e.message.toString()))
        }
    }

    override fun updateDraftOrder(
        draftOrderId: String,
        variantId: String,
        quantity: Int
    ): Flow<State<String>> = flow {
        emit(State.Loading)
        try {
            val updateDraftOrderResponse = apolloClient.mutation(UpdateDraftOrderMutation(
                DraftOrderInput(
                    lineItems = Optional.Present(listOf(
                        DraftOrderLineItemInput(
                            variantId = Optional.Present(variantId),
                            quantity = quantity
                        )
                    ))
                ),
                ownerId = draftOrderId
            )).execute()

            if (!updateDraftOrderResponse.hasErrors())
            {
                emit(State.Success(updateDraftOrderResponse.data?.draftOrderUpdate?.draftOrder?.id.toString()))
            }else
            {
                emit(State.Error(updateDraftOrderResponse.errors?.get(0)?.message.toString() ?: "Unknown Error"))
            }
        }catch (e : Exception)
        {
            emit(State.Error(e.message.toString()))
        }
    }

    override fun createFinalDraftOrder(
        customerId: String,
        customerEmail: String,
        cartItems: List<CartItemDTO>
    ): Flow<State<String>> = flow {
        emit(State.Loading)
        try {

            val draftOrderItemList = mutableListOf<DraftOrderLineItemInput>()

            cartItems.forEach {
                draftOrderItemList.add(
                    DraftOrderLineItemInput(
                        variantId = Optional.Present(it.id),
                        quantity = it.quantity
                    )
                )
            }

            val createDraftOrder = apolloClient.mutation(CreateDrafterOrderMutation(
                DraftOrderInput(
                    customerId = Optional.Present(customerId),
                    email = Optional.Present(customerEmail),
                    lineItems = Optional.Present(draftOrderItemList)
                )
            )).execute()

            if (!createDraftOrder.hasErrors())
            {
                emit(State.Success(createDraftOrder.data?.draftOrderCreate?.draftOrder?.id.toString()))
            }else
            {
                emit(State.Error(createDraftOrder.errors?.get(0)?.message.toString() ?: "Unknown Error"))
            }
        }catch (e : Exception)
        {
            emit(State.Error(e.message.toString()))
        }
    }

    override fun createOrderFromDraftOrder(draftOrderId: String): Flow<State<String>> = flow {
        emit(State.Loading)
        try {
            val orderResponse = apolloClient.mutation(CreateOrderFromDraftOrderMutation(
                id = draftOrderId
            )).execute()

            if (!orderResponse.hasErrors())
            {
                emit(State.Success(orderResponse.data?.draftOrderComplete?.draftOrder?.id.toString()))
            }else
            {
                emit(State.Error(orderResponse.errors?.get(0)?.message.toString() ?: "Unknown Error"))
            }
        }catch (e : Exception)
        {
            emit(State.Error(e.message.toString()))
        }
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

    override fun getCitiesForSearch(name: String): Flow<State<List<CityForSearchItem>>> = flow{
        emit(State.Loading)
        try {
            val citiesResponse = remoteDataSource.getCitiesForSearch(name)
            if (citiesResponse.isSuccessful && citiesResponse.body() != null)
            {
                emit(State.Success(citiesResponse.body() ?: emptyList()))
            }else
            {
                emit(State.Error(citiesResponse.message()))
            }
        }catch (e : Exception)
        {
            emit(State.Error(e.message.toString()))
        }
    }

    override fun convertCurrency(): Flow<State<Double>> = flow{
        emit(State.Loading)
        try {
            val newPrice= remoteDataSource.convertCurrency(1.toString(), getSharedPrefString(Constants.CURRENCY_KEY,Constants.EGP))
            emit(State.Success(newPrice))
        }catch (e : Exception)
        {
            emit(State.Error(e.message.toString()))
        }
    }

    override fun observeOnInternetState(): Flow<ConnectivityObserver.InternetState> {
        return connectivityObserver.observer()
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