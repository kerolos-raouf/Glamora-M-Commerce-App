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
import com.example.glamora.data.model.PriceRulesDTO
import com.example.glamora.data.model.brandModel.Brands
import com.example.glamora.data.model.brandModel.Image
import com.example.glamora.data.model.customerModels.CustomerInfo
import com.example.glamora.data.model.ordersModel.LineItemDTO
import com.example.glamora.data.model.ordersModel.OrderDTO
import com.example.glamora.data.network.FakeRemoteDataSource
import com.example.glamora.data.sharedPref.FakeSharedPrefHandler
import com.example.glamora.data.sharedPref.SharedPrefHandler
import com.example.glamora.util.State
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
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





    @Test
    fun `getAllBrands returns list of brands`() = runTest {
        val expectedBrands = listOf(
            Brands("1",Image("https://picsum.photos/200/300"), "https://picsum.photos/200/300"),
            Brands("2",Image("https://picsum.photos/200/300"), "https://picsum.photos/200/301"),
            Brands("3",Image("https://picsum.photos/200/300"), "https://picsum.photos/200/302"),
            Brands("4",Image("https://picsum.photos/200/300"), "https://picsum.photos/200/303"),
            Brands("5",Image("https://picsum.photos/200/300"), "https://picsum.photos/200/304"),
        )

        `when`(apolloClientHandler.getAllBrands()).thenReturn(flowOf(State.Success(expectedBrands)))

        repository.getAllBrands().test {
            val data = awaitItem() as State.Success
            assertEquals(data.data, expectedBrands)
            awaitComplete()
        }
    }


    @Test
    fun `getCartItemsForCustomer returns list of cart items`() = runTest {
        val expectedCartItems = listOf(
            CartItemDTO("1","1","1","Product 1",1,1,"1","1"),
            CartItemDTO("2","2","2","Product 2",2,2,"2","2"),
            CartItemDTO("3","3","3","Product 3",3,3,"3","3"),
            CartItemDTO("4","4","4","Product 4",4,4,"4","4"),
            CartItemDTO("5","5","5","Product 5",5,5,"5","5"),
        )

        `when`(apolloClientHandler.getCartItemsForCustomer("1")).thenReturn(flowOf(State.Success(expectedCartItems)))

        repository.getCartItemsForCustomer("1").test {
            val data = awaitItem() as State.Success
            assertEquals(data.data, expectedCartItems)
            awaitComplete()
        }
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


    @Test
    fun `createShopifyUser creates a user and returns it`() = runTest {
        val expectedUser = CustomerInfo("test@test.com","Test","Test","", listOf(AddressModel("1","Test","Test","Test","Test","Test","Test",false)))
        `when`(repository.createShopifyUser(expectedUser.email,"Kerolos","Raouf","123456789")).thenReturn(flowOf(Result.success(expectedUser)))

        repository.createShopifyUser(expectedUser.email,"Kerolos","Raouf","123456789").test {
            val result = awaitItem()
            assertEquals(result, Result.success(expectedUser))
            awaitComplete()
        }
    }


    @Test
    fun `getShopifyUserByEmail returns customer info model`() = runTest {
        val expectedUser = CustomerInfo("test@test.com","Test","Test","", listOf(AddressModel("1","Test","Test","Test","Test","Test","Test",false)))
        `when`(apolloClientHandler.getShopifyUserByEmail(expectedUser.email)).thenReturn(flowOf(State.Success(expectedUser)))

        repository.getShopifyUserByEmail(expectedUser.email).test {
            val result = awaitItem() as State.Success
            assertEquals(result.data, expectedUser)
            awaitComplete()
        }
    }



    @Test
    fun `getOrdersByCustomer returns list of orders`() = runTest {
        val expectedOrders = listOf(
            OrderDTO("1","1","1","1","1","1","1","1", listOf(LineItemDTO("1","1",1,"Product 1","example.com","EGP"))),
            OrderDTO("1","1","1","1","1","1","1","1", listOf(LineItemDTO("1","1",1,"Product 1","example.com","EGP"))),
            OrderDTO("1","1","1","1","1","1","1","1", listOf(LineItemDTO("1","1",1,"Product 1","example.com","EGP"))),
        )

        `when`(apolloClientHandler.getOrdersByCustomer("1")).thenReturn(flowOf(State.Success(expectedOrders)))

        repository.getOrdersByCustomer("1").test {
            val data = awaitItem() as State.Success
            assertEquals(data.data, expectedOrders)
            awaitComplete()
        }

    }


    @Test
    fun `loginWithEmail returns customer info model`() = runTest {
        val expectedUser = CustomerInfo(displayName="Unknown", email="Unknown", userId="Unknown", userIdAsNumber="Unknown", addresses= listOf())
        `when`(firebaseHandler.signInWithEmail("kerolos@gmail.com", "123456")).thenReturn(Result.success(Unit))

        repository.loginWithEmail("kerolos@gmail.com", "123456").test {
            val result = awaitItem()
            assertEquals(result, Result.success(expectedUser))
            awaitComplete()
        }
    }


    @Test
    fun `loginWithGoogle returns customer info model`() = runTest {
        val expectedUser = CustomerInfo(displayName="Unknown", email="Unknown", userId="Unknown", userIdAsNumber="Unknown", addresses= listOf())
        `when`(firebaseHandler.signInWithGoogle("idToken")).thenReturn(Result.success(Unit))

        repository.loginWithGoogle("idToken").test {
            val result = awaitItem()
            assertEquals(result, Result.success(expectedUser))
            awaitComplete()
        }
    }


    @Test
    fun `signUp returns customer info model`() = runTest {
        val expectedUser = CustomerInfo(displayName="Unknown", email="Unknown", userId="Unknown", userIdAsNumber="Unknown", addresses= listOf())
        `when`(firebaseHandler.signUp("kerolos@gmail.com", "123456")).thenReturn(Result.success(Unit))

        repository.signUp("kerolos@gmail.com", "123456").test {
            val result = awaitItem()
            assertEquals(result, Result.success(expectedUser))
            awaitComplete()
        }
    }



}