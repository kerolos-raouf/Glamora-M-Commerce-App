package com.example.glamora.fragmentOrders.viewModel

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
class OrderViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _ordersList = MutableStateFlow<List<OrderDTO>>(emptyList())
    val ordersList: StateFlow<List<OrderDTO>> get() = _ordersList

    fun getOrdersByCustomer(email: String) {
        viewModelScope.launch {
            repository.getOrdersByCustomer(email).collect { state ->
                when (state) {
                    is State.Error -> {
                        Log.d("OrderViewModel", "fetchOrder: ${state.message}")
                    }
                    State.Loading -> {
                        Log.d("OrderViewModel", "Loading Order data...")
                    }
                    is State.Success -> {
                        _ordersList.value = state.data
                        Log.d("OrderViewModel", "Successfully fetched orders: ${state.data}")

                    }
                }
            }
        }
    }
}
