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

    fun fetchOrderDetailsById(email: String, orderId: String) {
        Log.d("OrderDetailsViewModel", "Fetching order details for orderId: $orderId")

        viewModelScope.launch {
            repository.getOrdersByCustomer(email).collect { result ->
                Log.d("OrderDetailsViewModel", "Raw orders: $result")

                when (result) {
                    is State.Success -> {
                        val orders = result.data
                        val orderList = orders.filterIsInstance<OrderDTO>()
                            .filter { order -> order.id == orderId }

                        Log.d("OrderDetailsViewModel", "Filtered orders: $orderList")

                        _orderDetailsList.value = orderList
                    }
                    else -> {
                        Log.d("OrderDetailsViewModel", "No orders found or invalid type")
                    }
                }
            }
        }
    }


}