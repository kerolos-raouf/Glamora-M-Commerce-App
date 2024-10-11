package com.example.glamora.data.repository

import app.cash.turbine.test
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.benasher44.uuid.Uuid
import com.example.ProductQuery
import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.firebase.IFirebaseHandler
import com.example.glamora.data.internetStateObserver.ConnectivityObserver
import com.example.glamora.data.sharedPref.SharedPrefHandler
import com.example.glamora.util.State
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever


class RepositoryImplTest {


    lateinit var apolloClient : ApolloClient
    lateinit var remoteDataSource : RemoteDataSource
    lateinit var sharedPrefHandler : SharedPrefHandler
    lateinit var connectivityObserver : ConnectivityObserver
    lateinit var firebaseHandler : IFirebaseHandler

    lateinit var repository: RepositoryImpl

    @Before
    fun setUp()
    {
        apolloClient = mock(ApolloClient::class.java)
        remoteDataSource = mock(RemoteDataSource::class.java)
        sharedPrefHandler = mock(SharedPrefHandler::class.java)
        connectivityObserver = mock(ConnectivityObserver::class.java)
        firebaseHandler = mock(IFirebaseHandler::class.java)

        repository = RepositoryImpl(apolloClient, remoteDataSource, sharedPrefHandler, connectivityObserver, firebaseHandler)
    }

    @Test
    fun `getProducts returns Error when data is null`() = runTest {
        val mockApolloResponse: ApolloResponse<ProductQuery.Data> = mock()

        Mockito.`when`(mockApolloResponse.data).thenReturn(null)


        Mockito.`when`(apolloClient.query(Mockito.any<ProductQuery>()).execute())
            .thenReturn(mockApolloResponse)

        Mockito.`when`(mockApolloResponse.data).thenReturn(null)

        repository.getProducts().test {

            assertEquals(State.Loading, awaitItem())

            val errorState = awaitItem() as State.Error
            assertEquals("No products found", errorState.message)

            cancelAndConsumeRemainingEvents()
        }

    }

    fun getPriceRules() {
    }

    fun getDiscountCodes() {
    }

    fun getAllBrands() {
    }

    fun getCartItemsForCustomer() {
    }

    fun getFavoriteItemsForCustomer() {
    }

    fun updateCustomerAddress() {
    }

    fun deleteDraftOrder() {
    }

    fun updateCartDraftOrder() {
    }

    fun updateFavoritesDraftOrder() {
    }

    fun createFinalDraftOrder() {
    }

    fun createOrderFromDraftOrder() {
    }

    fun getCustomerAddressesByEmail() {
    }

    fun updateCustomerDefaultAddress() {
    }

    fun getCustomerUsingEmail() {
    }

    fun getCitiesForSearch() {
    }

    fun convertCurrency() {
    }

    fun observeOnInternetState() {
    }

    fun setSharedPrefString() {
    }

    fun getSharedPrefString() {
    }

    fun setSharedPrefBoolean() {
    }

    fun getSharedPrefBoolean() {
    }

    fun createShopifyUser() {
    }

    fun getShopifyUserByEmail() {
    }

    fun getOrdersByCustomer() {
    }

    fun loginWithEmail() {
    }

    fun loginWithGoogle() {
    }

    fun resetUserPassword() {
    }

    fun signUp() {
    }

    fun signOut() {
    }
}