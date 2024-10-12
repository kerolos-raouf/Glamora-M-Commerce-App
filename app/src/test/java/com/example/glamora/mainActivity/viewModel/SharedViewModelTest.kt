package com.example.glamora.mainActivity.viewModel

import app.cash.turbine.test
import com.example.glamora.data.contracts.IApolloClient
import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.contracts.SettingsHandler
import com.example.glamora.data.firebase.IFirebaseHandler
import com.example.glamora.data.internetStateObserver.ConnectivityObserver
import com.example.glamora.data.model.AvailableProductsModel
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.model.ProductDTO
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
    fun setUp() {
        repository = mock()
        sharedViewModel = SharedViewModel(repository)
    }

    @Test
    fun `getDiscountCodes returns list of discount codes`() = runTest {
        val expectedDiscountCodes = listOf(
            DiscountCodeDTO("1", "Summer Sale", 5.0),
            DiscountCodeDTO("2", "Summer Sale", 10.0),
            DiscountCodeDTO("3", "Summer Sale", 15.0),
            DiscountCodeDTO("4", "Summer Sale", 20.0),
            DiscountCodeDTO("5", "Summer Sale", 25.0),
        )

        `when`(repository.getDiscountCodes()).thenReturn(flowOf(State.Success(expectedDiscountCodes)))

        sharedViewModel.discountCodes.test {
            assertEquals(awaitItem(), emptyList<DiscountCodeDTO>())

            sharedViewModel.fetchDiscountCodes()

            assertEquals(awaitItem(), expectedDiscountCodes)
        }

    }


    @Test
    fun `getProductList returns list of products`() = runTest {
        val expectedProductList = listOf(
            ProductDTO(
                id = "1",
                title = "Product 1",
                brand = "Brand 1",
                category = "Category 1",
                description = "Description 1",
                mainImage = "https://example.com/image1.jpg",
                availableProducts = listOf(AvailableProductsModel("1", "Small", "Blue", "XL", 5)),
                images = listOf("https://example.com/image1.jpg"),
                availableColors = listOf("Red"),
                availableSizes = listOf("Small")
            ),
            ProductDTO(
                id = "2",
                title = "Product 2",
                brand = "Brand 2",
                category = "Category 2",
                description = "Description 2",
                mainImage = "https://example.com/image2.jpg",
                availableProducts = listOf(AvailableProductsModel("2", "Medium", "RED", "L", 3)),
                images = listOf("https://example.com/image2.jpg"),
                availableColors = listOf("Blue"),
                availableSizes = listOf("Medium")
            )
        )

        `when`(repository.getProducts()).thenReturn(
            flowOf(
                State.Success(
                    expectedProductList
                )
            )
        )

        sharedViewModel.productList.test {
            assertEquals(awaitItem(), emptyList<ProductDTO>())

            sharedViewModel.fetchProducts()

            assertEquals(awaitItem(), expectedProductList)
        }

    }

//    @Test
//    fun `getFilteredResults returns filtered list of products`() = runTest {
//        val expectedProductList = listOf(
//            ProductDTO(
//                id = "1",
//                title = "Product 1",
//                brand = "Brand 1",
//                category = "Category 1",
//                description = "Description 1",
//                mainImage = "https://example.com/image1.jpg",
//                availableProducts = listOf(AvailableProductsModel("1", "Small", "Blue", "XL", 5)),
//                images = listOf("https://example.com/image1.jpg"),
//                availableColors = listOf("Red"),
//                availableSizes = listOf("Small")
//            ),
//            ProductDTO(
//                id = "2",
//                title = "Product 2",
//                brand = "Brand 2",
//                category = "Category 2",
//                description = "Description 2",
//                mainImage = "https://example.com/image2.jpg",
//                availableProducts = listOf(AvailableProductsModel("2", "Medium", "RED", "L", 3)),
//                images = listOf("https://example.com/image2.jpg"),
//                availableColors = listOf("Blue"),
//                availableSizes = listOf("Medium")
//            )
//        )
//
//        `when`(repository.getProducts()).thenReturn(
//            flowOf(
//                State.Success(
//                    expectedProductList
//                )
//            )
//        )
//
//        sharedViewModel.filteredResults.test {
//            assertEquals(awaitItem(), emptyList<ProductDTO>())
//
//            sharedViewModel.fetchProducts()
//
//            assertEquals(awaitItem(), expectedProductList)
//
//            sharedViewModel.filterList("Product 1")
//
//            assertEquals(awaitItem().size, 1)
//            assertEquals(awaitItem().first().title, "Product 1")
//        }
//
//    }


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