package com.example.glamora.data.repository

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.ApolloResponse
import com.benasher44.uuid.Uuid
import com.example.ProductQuery
import com.example.glamora.data.apolloClientHandler.FakeApolloClientHandler
import com.example.glamora.data.apolloHandler.ApolloClientHandler
import com.example.glamora.data.contracts.IApolloClient
import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.contracts.SettingsHandler
import com.example.glamora.data.firebase.FakeFirebaseHandler
import com.example.glamora.data.firebase.IFirebaseHandler
import com.example.glamora.data.internetStateObserver.ConnectivityObserver
import com.example.glamora.data.internetStateObserver.InternetStateObserver
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.network.FakeRemoteDataSource
import com.example.glamora.data.sharedPref.FakeSharedPrefHandler
import com.example.glamora.data.sharedPref.SharedPrefHandler
import com.example.glamora.util.State
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class RepositoryImplTest {


    private lateinit var apolloClientHandler : IApolloClient
    private lateinit var remoteDataSource : RemoteDataSource
    private lateinit var sharedPrefHandler : SettingsHandler
    private lateinit var connectivityObserver : ConnectivityObserver
    private lateinit var firebaseHandler : IFirebaseHandler

    lateinit var repository: RepositoryImpl

    @Before
    fun setUp()
    {
        apolloClientHandler = FakeApolloClientHandler()
        remoteDataSource = FakeRemoteDataSource()
        sharedPrefHandler = FakeSharedPrefHandler()
        connectivityObserver = InternetStateObserver(ApplicationProvider.getApplicationContext())
        firebaseHandler = FakeFirebaseHandler()

        repository = RepositoryImpl(apolloClientHandler, remoteDataSource, sharedPrefHandler, connectivityObserver, firebaseHandler)
    }

    @Test
    fun `getDiscountCodes returns list of discount codes`() = runTest {
        val expectedDiscountCodes = listOf(
            DiscountCodeDTO("1","Summer Sale",5.0),
            DiscountCodeDTO("2","Summer Sale",10.0),
            DiscountCodeDTO("3","Summer Sale",15.0),
            DiscountCodeDTO("4","Summer Sale",20.0),
            DiscountCodeDTO("5","Summer Sale",25.0),
        )

        repository.getDiscountCodes().test {
            val result = awaitItem() as State.Loading
            assertEquals(result, State.Loading)
            val data = awaitItem() as State.Success
            assertEquals(data.data, expectedDiscountCodes)
            awaitComplete()
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