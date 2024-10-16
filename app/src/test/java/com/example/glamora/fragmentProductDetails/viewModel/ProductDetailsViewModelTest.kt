package com.example.glamora.fragmentProductDetails.viewModel

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.data.repository.RepositoryImpl
import com.example.glamora.mainActivity.viewModel.SharedViewModel
import com.example.glamora.util.State
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
@Config(manifest = Config.NONE)
class ProductDetailsViewModelTest {

    private lateinit var productDetailsViewModel: ProductDetailsViewModel
    private lateinit var repository: RepositoryImpl

    @Before
    fun setUp() {
        repository = mock()
        productDetailsViewModel = ProductDetailsViewModel(repository)
    }

    @Test
    fun `fetchCartItems should update state to Loading when fetching items`() = runTest {
        // Given
        val userId = "user123"
        `when`(repository.getCartItemsForCustomer(userId)).thenReturn(
            flowOf(State.Loading)
        )

        // When
        productDetailsViewModel.fetchCartItems(userId)

        // Then
        assert(productDetailsViewModel.state.value is State.Loading)
    }


}
