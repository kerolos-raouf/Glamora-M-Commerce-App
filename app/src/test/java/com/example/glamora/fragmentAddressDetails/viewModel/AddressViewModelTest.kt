package com.example.glamora.fragmentAddressDetails.viewModel

import app.cash.turbine.test
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.AddressModel
import com.example.glamora.util.State
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`
import kotlin.time.Duration.Companion.seconds


class AddressViewModelTest {

    private lateinit var repository : Repository
    private lateinit var addressViewModel: AddressViewModel

    @Before
    fun setUp() {
        repository = mock(Repository::class.java)
        addressViewModel = AddressViewModel(repository)
    }

    @Test
    fun `updateCustomerAddress returns success state`() = runTest {
        val expectedAddresses = listOf<AddressModel>(
            mock(),
            mock())

        `when`(repository.updateCustomerAddress("customerId",expectedAddresses)).thenReturn(
            flowOf(
                State.Success(expectedAddresses[0])
            )
        )

        `when`(repository.getCustomerAddressesByEmail("test@example.com")).thenReturn(
            flowOf(
                State.Success(expectedAddresses)
            )
        )

        addressViewModel.updateCustomerAddress("customerId","test@example.com",expectedAddresses[0])

        addressViewModel.address.test(5.seconds){
            val result = awaitItem()
            assertEquals(emptyList<AddressModel>(),result)
        }
    }

    @Test
    fun `getCustomerAddressesByEmail returns success state with list of addresses`() = runTest {
        val expectedAddresses = listOf<AddressModel>(
            mock(),
            mock())

        `when`(repository.getCustomerAddressesByEmail("test@example.com")).thenReturn(
            flowOf(
                State.Success(expectedAddresses)
            )
        )

        addressViewModel.getCustomerAddressesByEmail("test@example.com")

        addressViewModel.address.test(5.seconds){
            val result = awaitItem()
            assertEquals(expectedAddresses,result)
        }
    }
}