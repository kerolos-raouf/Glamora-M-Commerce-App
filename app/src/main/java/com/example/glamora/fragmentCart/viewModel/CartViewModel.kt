package com.example.glamora.fragmentCart.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.util.Constants
import com.example.glamora.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel(){

    private val _cartItems = MutableStateFlow<List<CartItemDTO>>(emptyList())
    val cartItems : StateFlow<List<CartItemDTO>> = _cartItems


    fun fetchCartItems(userId: String = "7552199491722"){
        viewModelScope.launch {
            repository.getCartItemsForCustomer(userId).collect{state ->
                when(state){
                    is State.Error -> {
                        Log.d("Kerolos", "fetchCartItems: ${state.message}")
                    }
                    State.Loading -> {
                    }
                    is State.Success -> {
                        _cartItems.value = state.data.filter { it.title != Constants.UNKNOWN }
                        Log.d("Kerolos", "fetchCartItems: ${_cartItems.value.size}")
                    }
                }
            }
        }
    }
}