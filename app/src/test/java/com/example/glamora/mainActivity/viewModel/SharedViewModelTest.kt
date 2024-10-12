package com.example.glamora.mainActivity.viewModel

import app.cash.turbine.test
import com.example.glamora.data.contracts.IApolloClient
import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.contracts.SettingsHandler
import com.example.glamora.data.firebase.IFirebaseHandler
import com.example.glamora.data.internetStateObserver.ConnectivityObserver
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.repository.RepositoryImpl
import com.example.glamora.util.State
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`


class SharedViewModelTest {


    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var repository: RepositoryImpl

    @Before
    fun setUp()
    {
        repository = mock()
        sharedViewModel = SharedViewModel(repository)
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

        `when`(repository.getDiscountCodes()).thenReturn(flowOf(State.Success(expectedDiscountCodes)))

        sharedViewModel.discountCodes.test {
            assertEquals(awaitItem(), emptyList<DiscountCodeDTO>())

            sharedViewModel.fetchDiscountCodes()

            assertEquals(awaitItem(), expectedDiscountCodes)
        }

    }



    fun getProductList() {
    }

    fun getFilteredResults() {
    }

    fun getCurrentCustomerInfo() {
    }

    fun getInternetState() {
    }

    fun getOperationDoneWithPayPal() {
    }

    fun setCustomerInfo() {
    }

    fun getFavoriteItemsState() {
    }

    fun fetchFavoriteItems() {
    }

    fun deleteFromFavorites() {
    }

    fun addToFavorites() {
    }

    fun isFavorite() {
    }

    fun setFavoriteWithEmptyList() {
    }

    fun getProductByID() {
    }

    fun fetchProducts() {
    }

    fun fetchPriceRules() {
    }

    fun fetchDiscountCodes() {
    }

    fun fetchCurrentCustomer() {
    }

    fun setSharedPrefString() {
    }

    fun getSharedPrefString() {
    }

    fun setSharedPrefBoolean() {
    }

    fun getSharedPrefBoolean() {
    }

    fun convertCurrency() {
    }

    fun filterList() {
    }

    fun getCustomerInfo() {
    }
}