package com.example.glamora.fragmentOrders.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.ordersModel.OrderDTO
import com.example.glamora.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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

    private val _message = MutableStateFlow("")
    val message : StateFlow<String> = _message

    private val _loading = MutableStateFlow(false)
    val loading : StateFlow<Boolean> = _loading

    fun getOrdersByCustomer(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getOrdersByCustomer(email).collect { state ->
                when (state) {
                    is State.Error -> {
                        _message.value = state.message
                        _loading.value = false
                    }
                    State.Loading -> {
                        _loading.value = true
                    }
                    is State.Success -> {
                        _ordersList.value = state.data
                        _loading.value = false

                    }
                }
            }
        }
    }
}
