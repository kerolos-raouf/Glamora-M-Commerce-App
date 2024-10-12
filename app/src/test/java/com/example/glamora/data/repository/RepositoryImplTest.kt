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
import com.example.glamora.data.model.AddressModel
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.model.FavoriteItemDTO
import com.example.glamora.data.model.PriceRulesDTO
import com.example.glamora.data.model.citiesModel.CityForSearchItem
import com.example.glamora.data.model.customerModels.Customer
import com.example.glamora.data.model.customerModels.CustomerDTO
import com.example.glamora.data.model.customerModels.CustomerInfo
import com.example.glamora.data.network.FakeRemoteDataSource
import com.example.glamora.data.sharedPref.FakeSharedPrefHandler
import com.example.glamora.data.sharedPref.SharedPrefHandler
import com.example.glamora.util.State
import com.shopify.checkoutsheetkit.lifecycleevents.Address
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
import retrofit2.Response

@RunWith(AndroidJUnit4::class)
class RepositoryImplTest {


    private lateinit var apolloClientHandler : IApolloClient
    private lateinit var remoteDataSource : RemoteDataSource
    private lateinit var sharedPrefHandler : SettingsHandler
    private lateinit var connectivityObserver : ConnectivityObserver
    private lateinit var firebaseHandler : IFirebaseHandler

    private lateinit var repository: RepositoryImpl

    @Before
    fun setUp()
    {
        apolloClientHandler = mock()
        remoteDataSource = mock()
        sharedPrefHandler = mock()
        connectivityObserver = mock()
        firebaseHandler = mock()

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

        `when`(apolloClientHandler.getDiscountCodes()).thenReturn(flowOf(State.Success(expectedDiscountCodes)))

        repository.getDiscountCodes().test {
            val data = awaitItem() as State.Success
            assertEquals(data.data, expectedDiscountCodes)
            awaitComplete()
        }
    }

    @Test
    fun `getPriceRules returns list of price rules`() = runTest {
        val expectedPriceRules = listOf(
            PriceRulesDTO("1",5.0),
            PriceRulesDTO("2",10.0),
            PriceRulesDTO("3",15.0),
            PriceRulesDTO("4",20.0),
            PriceRulesDTO("5",25.0),
        )

        `when`(apolloClientHandler.getPriceRules()).thenReturn(flowOf(State.Success(expectedPriceRules)))

        repository.getPriceRules().test {
            val data = awaitItem() as State.Success
            assertEquals(data.data, expectedPriceRules)
            awaitComplete()
        }
    }




    fun getDiscountCodes() {
    }

    fun getPriceRules() {
    }

    fun getAllBrands() {
    }

    fun getCartItemsForCustomer() {
    }

    @Test
    fun `getFavoriteItemsForCustomer returns list of favorite items`() = runTest {
        val expectedFavoriteItems = listOf(
            FavoriteItemDTO("1", "Product 1", "1", "title1", "100.0" , "https://example.com/image1.jpg"),
            FavoriteItemDTO("2", "Product 2", "2", "title2" , "200.0" , "https://example.com/image2.jpg"),
            FavoriteItemDTO("3", "Product 3", "3", "title3", "300.0" , "https://example.com/image3.jpg"),
            FavoriteItemDTO("4", "Product 4", "4", "title4", "400.0" , "https://example.com/image4.jpg"),
            FavoriteItemDTO("5", "Product 5", "5", "title5", "500.0" , "https://example.com/image5.jpg"),
        )

        `when`(apolloClientHandler.getFavoriteItemsForCustomer("1")).thenReturn(flowOf(State.Success(expectedFavoriteItems)))

        repository.getFavoriteItemsForCustomer("1").test {
            val data = awaitItem() as State.Success
            assertEquals(data.data, expectedFavoriteItems)
            awaitComplete()
        }
    }
    @Test
    fun `updateCustomerAddress updates customer address`() = runTest {
        val customerId = "1"
        val expectedCustomerAddress = listOf(
            AddressModel("1", "Home", "123 test st", "12345", "New York", "NY", "USA"),
            AddressModel("2", "Work", "456 test st", "67890", "San Francisco", "CA", "USA"),
        )

        `when`(apolloClientHandler.updateCustomerAddress(customerId, expectedCustomerAddress)).thenReturn(flowOf(State.Success(expectedCustomerAddress[0])))

        repository.updateCustomerAddress(customerId, expectedCustomerAddress).test {
            val data = awaitItem() as State.Success
            assertEquals(data.data, expectedCustomerAddress[0])
            awaitComplete()
        }
    }

    @Test
    fun `deleteDraftOrder deletes draft order`() = runTest {
        val expectedDeletedId = "1"

        `when`(apolloClientHandler.deleteDraftOrder(expectedDeletedId)).thenReturn(flowOf(State.Success(expectedDeletedId)))

        repository.deleteDraftOrder(expectedDeletedId).test {
            val data = awaitItem() as State.Success
            assertEquals(data.data, expectedDeletedId)
            awaitComplete()
        }
    }

    @Test
    fun `updateCartDraftOrder updates cart draft order`() = runTest {
        val draftOrderId = "1"
        val expectedUpdatedCartItems = listOf(
            CartItemDTO("1", "Product 1", "1", "title1",1 , 3, "100.0" , "https://example.com/image1.jpg",false),
            CartItemDTO("2", "Product 2", "2", "title2",2 , 5, "200.0" , "https://example.com/image1.jpg",true),
        )

        `when`(apolloClientHandler.updateCartDraftOrder(draftOrderId, expectedUpdatedCartItems)).thenReturn(flowOf(State.Success("Success")))

        repository.updateCartDraftOrder(draftOrderId, expectedUpdatedCartItems).test {
            val data = awaitItem() as State.Success
            assertEquals(data.data, "Success")
            awaitComplete()
        }
    }

    @Test
    fun `updateFavoritesDraftOrder updates favorites draft order`() = runTest {
        val draftOrderId = "1"
        val expectedUpdatedFavoritesItems = listOf(
            FavoriteItemDTO("1", "Product 1", "1", "title1" , "100.0" , "https://example.com/image1.jpg"),
            FavoriteItemDTO("2", "Product 2", "2", "title2" , "200.0" , "https://example.com/image1.jpg"),
        )

        `when`(apolloClientHandler.updateFavoritesDraftOrder(draftOrderId, expectedUpdatedFavoritesItems)).thenReturn(flowOf(State.Success("Success")))

        repository.updateFavoritesDraftOrder(draftOrderId, expectedUpdatedFavoritesItems).test {
            val data = awaitItem() as State.Success
            assertEquals(data.data, "Success")
            awaitComplete()
        }
    }

    @Test
    fun `createFinalDraftOrder creates final draft order`() = runTest {
        val customerId = "1"
        val customerEmail = "test@example.com"
        val expectedCartItems = listOf(
            CartItemDTO("1", "Product 1", "1", "title1",1 , 3, "100.0" , "https://example.com/image1.jpg",false),
            CartItemDTO("2", "Product 2", "2", "title2",2 , 5, "200.0" , "https://example.com/image1.jpg",true),
        )
        val address = AddressModel("1", "Home", "123 test st", "12345", "New York", "NY", "USA")
        val tag = "ORDER_FROM_FAVORITES"

        `when`(apolloClientHandler.createFinalDraftOrder(customerId, customerEmail, expectedCartItems, 10.0, address, tag)).thenReturn(flowOf(State.Success("Success")))

        repository.createFinalDraftOrder(customerId, customerEmail, expectedCartItems, 10.0, address, tag).test {
            val data = awaitItem() as State.Success
            assertEquals(data.data, "Success")
            awaitComplete()
        }
    }

    @Test
    fun `createOrderFromDraftOrder creates order from draft order`() = runTest {
        val draftOrderId = "1"

        `when`(apolloClientHandler.createOrderFromDraftOrder(draftOrderId)).thenReturn(flowOf(State.Success("Success")))

        repository.createOrderFromDraftOrder(draftOrderId).test {
            val data = awaitItem() as State.Success
            assertEquals(data.data, "Success")
            awaitComplete()
        }
    }

    @Test
    fun `getCustomerAddressesByEmail returns list of customer addresses`() = runTest {
        val email = "test@example.com"
        val expectedCustomerAddresses = listOf(
            AddressModel("1", "Home", "123 test st", "12345", "New York", "NY", "USA"),
            AddressModel("2", "Work", "456 test st", "67890", "San Francisco", "CA", "USA"),
        )

        `when`(apolloClientHandler.getCustomerAddressesByEmail(email)).thenReturn(flowOf(State.Success(expectedCustomerAddresses)))

        repository.getCustomerAddressesByEmail(email).test {
            val data = awaitItem() as State.Success
            assertEquals(data.data, expectedCustomerAddresses)
            awaitComplete()
        }
    }

    @Test
    fun `getCustomerAddressesByEmail returns empty list if email is wrong`() = runTest {
        val email = "wrong@example.com"
        val expectedCustomerAddresses = emptyList<AddressModel>()

        `when`(apolloClientHandler.getCustomerAddressesByEmail(email)).thenReturn(flowOf(State.Success(expectedCustomerAddresses)))

        repository.getCustomerAddressesByEmail(email).test {
            val data = awaitItem() as State.Success
            assertEquals(data.data, expectedCustomerAddresses)
            awaitComplete()
        }
    }

    @Test
    fun `updateCustomerDefaultAddress updates customer default address`() = runTest {
        val customerId = "1"
        val addressId = "1"

        `when`(apolloClientHandler.updateCustomerDefaultAddress(customerId, addressId)).thenReturn(flowOf(State.Success("Success")))

        repository.updateCustomerDefaultAddress(customerId, addressId).test {
            val data = awaitItem() as State.Success
            assertEquals(data.data, "Success")
            awaitComplete()
        }
    }


    // UnTesting
    fun getCustomerUsingEmail() {
    }

    fun getCitiesForSearch() {
    }

    fun convertCurrency() {
    }

    fun observeOnInternetState() {
    }


    @Test
    fun `setSharedPrefString sets shared preference string`() = runTest {
        val key = "test_key"
        val value = "test_value"

        `when`(repository.getSharedPrefString(key, value)).thenReturn(value)

        assertEquals(repository.getSharedPrefString(key, value), value)
    }

    @Test
    fun `getSharedPrefString gets shared preference string`() = runTest {
        val key = "test_key"
        val defaultValue = "default_value"
        val expectedValue = "test_value"

        `when`(sharedPrefHandler.getSharedPrefString(key, defaultValue)).thenReturn(expectedValue)

        assertEquals(repository.getSharedPrefString(key, defaultValue), expectedValue)
    }

    @Test
    fun `setSharedPrefBoolean sets shared preference boolean`() = runTest {
        val key = "test_key"
        val value = true

        `when`(sharedPrefHandler.setSharedPrefBoolean(key, value)).then {
            assertEquals(sharedPrefHandler.getSharedPrefBoolean(key, false), value)
        }
    }

    @Test
    fun `getSharedPrefBoolean gets shared preference boolean`() = runTest {
        val key = "test_key"
        val defaultValue = false
        val expectedValue = true

        `when`(sharedPrefHandler.getSharedPrefBoolean(key, defaultValue)).thenReturn(expectedValue)

        assertEquals(repository.getSharedPrefBoolean(key, defaultValue), expectedValue)
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