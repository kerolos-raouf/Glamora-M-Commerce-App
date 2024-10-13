package com.example.glamora.fragmentCart.viewModel

import app.cash.turbine.test
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.data.repository.payPalRepo.IPayPalRepository
import com.example.glamora.util.State
import getOrAwaitValue
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModelTest {


    private val dispatcher = StandardTestDispatcher()
    private val repository = mock<Repository>()
    private val payPalRepository = mock<IPayPalRepository>()
    private lateinit var cartViewModel: CartViewModel


    @Before
    fun setup() {
        Dispatchers.setMain(dispatcher)
        cartViewModel = CartViewModel(repository, payPalRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `getCartItems returns list of cart items`() = runTest {
        val expectedCartItems = listOf<CartItemDTO>(
            mock(),
            mock(),
            mock(),
            mock(),
        )


        `when`(repository.getCartItemsForCustomer("1")).thenReturn(
            flowOf(
                State.Success(
                    expectedCartItems
                )
            )
        )

        cartViewModel.fetchCartItems("1")
        delay(1000)

        assertEquals(expectedCartItems, cartViewModel.cartItems.value )

    }




    fun getMessage() {
    }

    fun getLoading() {
    }

    fun getShowDoneBottomSheet() {
    }

    fun fetchCartItems() {
    }

    fun deleteCartItemFromDraftOrder() {
    }

    fun updateCartItemQuantity() {
    }

    fun createFinalDraftOrder() {
    }

    fun getOpenApprovalUrlState() {
    }

    fun startOrder() {
    }
}