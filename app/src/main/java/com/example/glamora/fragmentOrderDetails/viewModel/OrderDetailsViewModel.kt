package com.example.glamora.fragmentOrderDetails.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.ordersModel.OrderDTO
import com.example.glamora.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel@Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _orderDetailsList = MutableStateFlow<List<OrderDTO>>(emptyList())
    val orderDetailsList: StateFlow<List<OrderDTO>> get() = _orderDetailsList

    private val _loading = MutableStateFlow(false)
    val loading : StateFlow<Boolean> = _loading

    private val _message = MutableStateFlow("")
    val message : StateFlow<String> = _message

    fun fetchOrderDetailsById(email: String, orderId: String) {
        _loading.value = true // Start loading

        viewModelScope.launch {
            repository.getOrdersByCustomer(email).collect { state ->
                when (state) {
                    is State.Success -> {
                        val orders = state.data
                        val orderList = orders.filterIsInstance<OrderDTO>()
                            .filter { order -> order.id == orderId }

                        _orderDetailsList.value = orderList

                        if (orderList.isEmpty()) {
                            _message.value = "No order found with ID: $orderId"
                        }

                        _loading.value = false
                    }
                    is State.Loading -> {
                        _loading.value = true
                    }
                    is State.Error -> {
                        _message.value = state.message
                        _loading.value = false
                    }
                }
            }
        }
    }


}