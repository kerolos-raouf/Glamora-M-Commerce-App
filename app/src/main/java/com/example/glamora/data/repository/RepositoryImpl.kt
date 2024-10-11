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
import com.example.UpdateCustomerDefaultAddressMutation
import com.example.UpdateDraftOrderMutation
import com.example.glamora.data.apolloHandler.ApolloClientHandler
import com.example.glamora.data.contracts.IApolloClient
import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.contracts.SettingsHandler
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
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.timeout
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class RepositoryImpl @Inject constructor(
    private val apolloClientHandler: IApolloClient,
    private val remoteDataSource: RemoteDataSource,
    private val sharedPrefHandler: SettingsHandler,
    private val connectivityObserver: ConnectivityObserver,
    private val firebaseHandler: IFirebaseHandler
) : Repository {


    //graph queries
    override fun getProducts(): Flow<State<List<ProductDTO>>> = apolloClientHandler.getProducts()

    @OptIn(FlowPreview::class)
    override fun getPriceRules(): Flow<State<List<PriceRulesDTO>>> = apolloClientHandler.getPriceRules()

    @OptIn(FlowPreview::class)
    override fun getDiscountCodes(): Flow<State<List<DiscountCodeDTO>>> = apolloClientHandler.getDiscountCodes()

    @OptIn(FlowPreview::class)
    override fun getAllBrands(): Flow<State<List<Brands>>> = apolloClientHandler.getAllBrands()

    @OptIn(FlowPreview::class)
    override fun getCartItemsForCustomer(customerId: String) : Flow<State<List<CartItemDTO>>> =
        apolloClientHandler.getCartItemsForCustomer(customerId)

    @OptIn(FlowPreview::class)
    override fun getFavoriteItemsForCustomer(customerId: String) : Flow<State<List<FavoriteItemDTO>>> =
        apolloClientHandler.getFavoriteItemsForCustomer(customerId)

    @OptIn(FlowPreview::class)
    override fun updateCustomerAddress(
        customerId: String,
        address: List<AddressModel>
    ): Flow<State<AddressModel>> = apolloClientHandler.updateCustomerAddress(customerId, address)

    override fun deleteDraftOrder(draftOrderId: String): Flow<State<String>>  = apolloClientHandler.deleteDraftOrder(draftOrderId)

    override fun updateCartDraftOrder(
        draftOrderId: String,
        newCartItemsList: List<CartItemDTO>,
    ): Flow<State<String>> = apolloClientHandler.updateCartDraftOrder(draftOrderId, newCartItemsList)

    override fun updateFavoritesDraftOrder(
        draftOrderId: String,
        newFavoriteItemsList: List<FavoriteItemDTO>,
    ): Flow<State<String>> = apolloClientHandler.updateFavoritesDraftOrder(draftOrderId, newFavoriteItemsList)

    override fun createFinalDraftOrder(
        customerId: String,
        customerEmail: String,
        cartItems: List<CartItemDTO>,
        discountAmount: Double,
        address: AddressModel,
        tag: String
    ): Flow<State<String>> =
        apolloClientHandler.createFinalDraftOrder(customerId, customerEmail, cartItems, discountAmount, address, tag)

    override fun createOrderFromDraftOrder(draftOrderId: String): Flow<State<String>> =
        apolloClientHandler.createOrderFromDraftOrder(draftOrderId)

    override fun getCustomerAddressesByEmail(email: String): Flow<State<List<AddressModel>>> =
        apolloClientHandler.getCustomerAddressesByEmail(email)

    override fun updateCustomerDefaultAddress(
        customerId: String,
        addressId: String
    ): Flow<State<String>> = apolloClientHandler.updateCustomerDefaultAddress(customerId, addressId)

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
    ): Flow<Result<CustomerInfo>> = apolloClientHandler.createShopifyUser(email, firstName, lastName, phone)

    override fun getShopifyUserByEmail(email: String): Flow<State<CustomerInfo>> =
        apolloClientHandler.getShopifyUserByEmail(email)

    @OptIn(FlowPreview::class)
    override fun getOrdersByCustomer(customerEmail: String): Flow<State<List<OrderDTO>>> =
        apolloClientHandler.getOrdersByCustomer(customerEmail)


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