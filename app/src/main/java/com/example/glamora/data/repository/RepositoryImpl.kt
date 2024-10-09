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
import com.example.GetCustomerByEmailQuery
import com.example.GetDraftOrdersByCustomerQuery
import com.example.GetOrdersByCustomerQuery
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
import com.example.glamora.data.model.FavoriteItemDTO
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
import com.example.glamora.data.model.customerModels.CustomerInfo
import com.example.glamora.data.model.ordersModel.LineItemDTO
import com.example.glamora.data.model.ordersModel.OrderDTO
import com.example.glamora.util.toCartItemsDTO
import com.example.glamora.util.toFavoriteItemsDTO
import com.example.type.CustomerInput
import com.example.type.DraftOrderAppliedDiscountInput
import com.example.type.DraftOrderAppliedDiscountType
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

    @OptIn(FlowPreview::class)
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
    override fun getFavoriteItemsForCustomer(customerId: String) : Flow<State<List<FavoriteItemDTO>>> = flow {
        emit(State.Loading)
        try {

            val favoriteItemsResponse = apolloClient.query(GetDraftOrdersByCustomerQuery(
                query = "customer_id:$customerId"
            )).execute()
            if (favoriteItemsResponse.data != null) {

                val draftOrdersResponse = favoriteItemsResponse.data?.draftOrders
                if (draftOrdersResponse != null) {
                    emit(State.Success(draftOrdersResponse.toFavoriteItemsDTO()))
                }else
                {
                    emit(State.Error("No products found"))
                }
            }else
            {
                emit(State.Error(favoriteItemsResponse.errors.toString() ?: "Unknown Error"))
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
        address: List<AddressModel>
    ): Flow<State<AddressModel>> = flow {
        emit(State.Loading)
        try {
            val mailingAddressInputList = mutableListOf<MailingAddressInput>()

            address.forEach {
                mailingAddressInputList.add(
                    MailingAddressInput(
                        firstName = Optional.Present(it.firstName),
                        lastName = Optional.Present(it.lastName),
                        phone = Optional.Present(it.phone),
                        address1 = Optional.Present(it.street),
                        city = Optional.Present(it.city),
                        country = Optional.Present(it.country),
                    )
                )
            }

            val customerInput = CustomerInput(
                id = Optional.Present(customerId),
                addresses = Optional.Present(mailingAddressInputList)
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

    override fun updateCartDraftOrder(
        draftOrderId: String,
        newCartItemsList: List<CartItemDTO>,
    ): Flow<State<String>> = flow {
        emit(State.Loading)
        try {
            val draftOrderLineItemInputList = mutableListOf<DraftOrderLineItemInput>()//create list of line items from cart items
            newCartItemsList.forEach {
                draftOrderLineItemInputList.add(
                    DraftOrderLineItemInput(
                        variantId = Optional.Present(it.id),
                        quantity = it.quantity
                    )
                )
            }
            val updateDraftOrderResponse = apolloClient.mutation(UpdateDraftOrderMutation(
                DraftOrderInput(
                    lineItems = Optional.Present(draftOrderLineItemInputList)
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

    override fun updateFavoritesDraftOrder(
        draftOrderId: String,
        newFavoriteItemsList: List<FavoriteItemDTO>,
    ): Flow<State<String>> = flow {
        emit(State.Loading)
        try {
            val draftOrderLineItemInputList = mutableListOf<DraftOrderLineItemInput>()
            newFavoriteItemsList.forEach {
                draftOrderLineItemInputList.add(
                    DraftOrderLineItemInput(
                        variantId = Optional.Present(it.id),
                        quantity = 1
                    )
                )
            }
            val updateDraftOrderResponse = apolloClient.mutation(UpdateDraftOrderMutation(
                DraftOrderInput(
                    lineItems = Optional.Present(draftOrderLineItemInputList)
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
        cartItems: List<CartItemDTO>,
        discountAmount: Double,
        address: AddressModel
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
                    tags = Optional.present(listOf(Constants.CART_DRAFT_ORDER_KEY)),
                    customerId = Optional.Present(customerId),
                    email = Optional.Present(customerEmail),
                    lineItems = Optional.Present(draftOrderItemList),
                    appliedDiscount = Optional.Present(DraftOrderAppliedDiscountInput(
                        value = discountAmount,
                        valueType = DraftOrderAppliedDiscountType.PERCENTAGE
                    )
                    ),
                    billingAddress = Optional.Present(
                        MailingAddressInput(
                            address1 = Optional.Present(address.street),
                            city = Optional.Present(address.city),
                            country = Optional.Present(address.country),
                            firstName = Optional.Present(address.firstName),
                            lastName = Optional.Present(address.lastName),
                            phone = Optional.Present(address.phone)
                        )
                    )
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

    override fun getCustomerAddressesByEmail(email: String): Flow<State<List<AddressModel>>> = flow {
        val query = GetCustomerByEmailQuery(email)

        try {
            val response = apolloClient.query(query).execute()

            if (response.hasErrors()) {
                emit(State.Error("Error fetching user: ${response.errors}"))
            } else {
                val customerEdges = response.data?.customers?.edges

                if (!customerEdges.isNullOrEmpty()) {
                    val addresses = customerEdges[0].node.addresses
                    val addressModelList = mutableListOf<AddressModel>()
                    addresses.forEach {
                        addressModelList.add(it.toAddressModel())
                    }
                    emit(State.Success(addressModelList))
                } else {
                    emit(State.Error("User not found"))
                }
            }
        } catch (e: Exception) {
            emit(State.Error(e.message.toString()))
        }
    }

    override fun getCustomerUsingEmail(email: String): Flow<State<Customer>> = flow {
        emit(State.Loading)
        try {
            val customerResponse = remoteDataSource.getCustomersUsingEmail(email)
            if (customerResponse.isSuccessful) {
                if (customerResponse.body()?.customers != null
                    && customerResponse.body()?.customers?.size!! > 0
                    && customerResponse.body()?.customers?.get(0) != null
                ) {
                    emit(State.Success(customerResponse.body()?.customers?.get(0)!!))
                } else {
                    emit(State.Error(Constants.CUSTOMER_NOT_FOUND))
                }
            } else {
                emit(State.Error(customerResponse.message()))
            }
        } catch (e: Exception) {
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


    override fun createShopifyUser(
        email: String,
        firstName: String,
        lastName: String,
        phone: String?
    ): Flow<Result<CustomerInfo>> = flow {
        val client = apolloClient

        val customerInput = CustomerInput(
            email = Optional.Present(email),
            firstName = Optional.Present(firstName),
            lastName = Optional.Present(lastName),
            phone = Optional.Present(phone)
        )

        val mutation = CreateCustomerMutation(customerInput)

        try {
            val response = client.mutation(mutation).execute()

            if (response.hasErrors()) {
                emit(Result.failure(Throwable("Error creating user: ${response.errors}")))
            } else {
                val shopifyUserId = response.data?.customerCreate?.customer?.id
                if (shopifyUserId != null) {
                    emit(Result.success(CustomerInfo()))
                } else {
                    if (response.data.toString().contains("Phone has already been taken")) {
                        emit(Result.failure(Throwable("Phone has already been taken")))
                    } else if (response.data.toString().contains("Email has already been taken")) {
                        emit(Result.failure(Throwable("Email has already been taken")))
                    }
                    else if (response.data.toString().contains("Enter a valid phone number")) {
                        emit(Result.failure(Throwable("InValid phone number")))
                    }
                    else {
                        emit(Result.failure(Throwable("User creation failed without errors.")))
                    }

                }
            }
        } catch (e: Exception) {
            emit(Result.failure(Throwable("Mutation failed: ${e.message}")))
        }
    }

    override fun getShopifyUserByEmail(email: String): Flow<State<CustomerInfo>> = flow {
        val query = GetCustomerByEmailQuery(email)

        try {
            val response = apolloClient.query(query).execute()

            if (response.hasErrors()) {
                emit(State.Error("Error fetching user: ${response.errors}"))
            } else {
                val customerEdges = response.data?.customers?.edges

                if (!customerEdges.isNullOrEmpty()) {
                    val customer = customerEdges[0].node

                    val customerInfo = CustomerInfo(
                        displayName = "${customer.firstName} ${customer.lastName}",
                        email = customer.email.toString(),
                        userId = customer.id,
                        userIdAsNumber = customer.id.split("/")[customer.id.split("/").size-1],
                        addresses = customer.addresses.map { it.toAddressModel() }
                    )
                    emit(State.Success(customerInfo))
                } else {
                    emit(State.Error("User not found"))
                }
            }
        } catch (e: Exception) {
            emit(State.Error(e.message.toString()))
        }
    }

    @OptIn(FlowPreview::class)
    override fun getOrdersByCustomer(customerEmail: String): Flow<State<List<OrderDTO>>> = flow {
        emit(State.Loading)
        try {
            val ordersResponse = apolloClient.query(GetOrdersByCustomerQuery(customerEmail)).execute()

            if (ordersResponse.data != null) {
                val orders = ordersResponse.data!!.orders

                if (orders != null && orders.edges.isNotEmpty()) {
                    val orderDTOList = orders.edges.map { edge ->
                        OrderDTO(
                            id = edge.node.id,
                            name = edge.node.name,
                            createdAt = edge.node.createdAt.toString(),
                            totalPrice = edge.node.totalPriceSet.shopMoney.amount.toString(),
                            address = edge.node.billingAddress?.address1.toString(),
                            country = edge.node.billingAddress?.country.toString(),
                            city = edge.node.billingAddress?.city.toString(),
                            currencyCode = edge.node.totalPriceSet.shopMoney.currencyCode.name,
                            lineItems = edge.node.lineItems.edges.map { itemEdge ->
                                LineItemDTO(
                                    id = itemEdge.node.id,
                                    name = itemEdge.node.name,
                                    quantity = itemEdge.node.quantity,
                                    unitPrice = itemEdge.node.originalUnitPriceSet.shopMoney.amount.toString(),
                                    currencyCode = itemEdge.node.originalUnitPriceSet.shopMoney.currencyCode.name,
                                    image =itemEdge.node.image?.url.toString()
                                )
                            }
                        )
                    }

                    emit(State.Success(orderDTOList))
                } else {
                    emit(State.Success(emptyList()))
                }
            } else {
                emit(State.Error("No orders found"))
            }
        } catch (e: Exception) {
            emit(State.Error("An error occurred: ${e.message}"))
        }
    }


    override fun loginWithEmail(email: String, password: String): Flow<Result<CustomerInfo>> =
        flow {
            val result = firebaseHandler.signInWithEmail(email, password)
            if (result.isSuccess) {
                val currentUser = firebaseHandler.getCurrentUser()
                emit(Result.success(CustomerInfo(email = currentUser?.email ?: Constants.UNKNOWN)))
            } else {
                emit(Result.failure(result.exceptionOrNull() ?: Throwable("Login failed")))
            }
        }

    override fun loginWithGoogle(idToken: String): Flow<Result<CustomerInfo>> = flow {
        val result = firebaseHandler.signInWithGoogle(idToken)
        if (result.isSuccess) {
            val currentUser = firebaseHandler.getCurrentUser()
            emit(Result.success(CustomerInfo(email = currentUser?.email ?: Constants.UNKNOWN)))
        } else {
            emit(Result.failure(result.exceptionOrNull() ?: Throwable("Google login failed")))
        }
    }

    override fun resetUserPassword(email: String): Flow<Result<CustomerInfo>> = flow {
        val result = firebaseHandler.resetPassword(email)
        if (result.isSuccess) {
            emit(Result.success(CustomerInfo(email = email)))
        } else {
            emit(
                Result.failure(
                    result.exceptionOrNull() ?: Throwable("Failed to send reset email")
                )
            )
        }
    }

    override fun signUp(email: String, password: String): Flow<Result<CustomerInfo>> = flow {
        val result = firebaseHandler.signUp(email, password)
        if (result.isSuccess) {
            val currentUser = firebaseHandler.getCurrentUser()
            emit(Result.success(CustomerInfo(email = currentUser?.email ?: Constants.UNKNOWN)))
        } else {
            emit(Result.failure(result.exceptionOrNull() ?: Throwable("Sign-up failed")))
        }
    }

    override fun signOut() {
         firebaseHandler.signOut()
    }

}