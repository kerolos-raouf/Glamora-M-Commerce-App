package com.example.glamora.fragmentCart.viewModel

import android.os.Looper
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.AddressModel
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.data.repository.payPalRepo.IPayPalRepository
import com.example.glamora.util.State
import getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsNull.nullValue
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class CartViewModelTest {


    private val dispatcher = StandardTestDispatcher()
    private val repository = mock<Repository>()
    private val payPalRepository = mock<IPayPalRepository>()
    private lateinit var cartViewModel: CartViewModel


    @Before
    fun setup() {
        mockStatic(Looper::class.java).use { mockedLooper ->
            `when`(Looper.getMainLooper()).thenReturn(mock(Looper::class.java))}
        Dispatchers.setMain(dispatcher)
        cartViewModel = CartViewModel(repository, payPalRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `getCartItems returns not null list of cart items`() = runTest {
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

        val result = cartViewModel.cartItems.getOrAwaitValue()

        assertThat(result,not(nullValue()))

    }


    @Test
    fun `fetchCartItems returns list of cart items`() = runTest {
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


        cartViewModel.cartItems.observeForever{
            if(it.isNotEmpty())
                assertThat(it, `is`(expectedCartItems))
        }
    }



    @Test
    fun `deleteDraftOrder returns success state and empty list of cart items`() = runTest {
        val expectedDeletedId = "1"

        `when`(repository.deleteDraftOrder(expectedDeletedId)).thenReturn(
            flowOf(
                State.Success(expectedDeletedId)
            )
        )

        `when`(repository.getCartItemsForCustomer("customerId")).thenReturn(
            flowOf(
                State.Success(emptyList())
            )
        )

        cartViewModel.deleteDraftOrder(expectedDeletedId,"customerId")


        cartViewModel.cartItems.observeForever{
            assertThat(it, `is`(emptyList()))
        }
    }


    @Test
    fun `update cart draft order returns success state and list of cart items`() = runTest {
        val draftOrderId = "1"
        val expectedCartItems = listOf<CartItemDTO>(
            mock(),
            mock(),
            mock(),
            mock(),
        )
        `when`(repository.updateCartDraftOrder(draftOrderId,expectedCartItems)).thenReturn(
            flowOf(
                State.Success(draftOrderId)
            )
        )

        `when`(repository.getCartItemsForCustomer("customerId")).thenReturn(
            flowOf(
                State.Success(expectedCartItems)
            )
        )

        cartViewModel.updateDraftOrder(draftOrderId,expectedCartItems,"customerId")


        cartViewModel.cartItems.observeForever{
            if(it.isNotEmpty())
               assertThat(it, `is`(expectedCartItems))
        }
    }



    @Test
    fun `create final draft order returns success state and new draft order id`() = runTest {
        val customerId = "customerId"
        val customerEmail = "test@example.com"
        val expectedCartItems = listOf<CartItemDTO>(
            mock(),
            mock(),
            mock(),
            mock(),
        )
        val expectedDiscountAmount = 100.00
        val expectedAddress = mock<AddressModel>()
        val expectedTag = "ORDER_FROM_FAVORITES"

        `when`(repository.createFinalDraftOrder(customerId,customerEmail,expectedCartItems,expectedDiscountAmount,expectedAddress,expectedTag)).thenReturn(
            flowOf(
                State.Success("newDraftOrderId")
            )
        )
        `when`(repository.createOrderFromDraftOrder("newDraftOrderId")).thenReturn(
            flowOf(
                State.Success("newDraftOrderId")
            )
        )

        `when`(repository.getCartItemsForCustomer(customerId)).thenReturn(
            flowOf(
                State.Success(expectedCartItems)
            )
        )

        `when`(repository.deleteDraftOrder("newDraftOrderId")).thenReturn(
            flowOf(
                State.Success("newDraftOrderId")
            )
        )

        cartViewModel.createFinalDraftOrder(
            customerId,
            customerEmail,
            expectedDiscountAmount,
            expectedAddress,
        )
        cartViewModel.cartItems.observeForever{
                assertThat(it, `is`(emptyList()))
        }

    }



}