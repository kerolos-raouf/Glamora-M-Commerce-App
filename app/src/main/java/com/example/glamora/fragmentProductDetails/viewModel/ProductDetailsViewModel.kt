package com.example.glamora.fragmentProductDetails.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.AddressModel
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.data.model.FavoriteItemDTO
import com.example.glamora.util.Constants
import com.example.glamora.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel(){

    private val _cartItems = MutableStateFlow<List<CartItemDTO>>(emptyList())

    private val _message = MutableStateFlow("")
    val message : StateFlow<String> = _message

    private val _loading = MutableStateFlow(false)
    val loading : StateFlow<Boolean> = _loading

    fun fetchCartItems(userId: String){
        viewModelScope.launch {
            repository.getCartItemsForCustomer(userId).collect{state ->
                when(state){
                    is State.Error -> {
                        _message.value = state.message
                        _loading.value = false
                    }
                    State.Loading -> {
                        _loading.value = true
                    }
                    is State.Success -> {
                        _cartItems.value = state.data
                        _loading.value = false
                    }
                }
            }
        }
    }


    fun addToCard(variant: CartItemDTO, userId: String, userEmail: String) {

        viewModelScope.launch {
            val currentState = _cartItems.value


                if(currentState.isEmpty())
                {
                    repository.createFinalDraftOrder(
                        userId,
                        userEmail,
                        listOf(variant),
                        0.0,
                        AddressModel(),
                        Constants.CART_DRAFT_ORDER_KEY
                    ).collect{
                        when(it){
                            is State.Error -> {}
                            State.Loading -> {}
                            is State.Success -> {
                                val cardItem = variant.copy(draftOrderId = it.data)
                                repository.updateCartDraftOrder(cardItem.draftOrderId, listOf(cardItem)).collect{
                                    when(it){
                                        is State.Success -> {
                                            fetchCartItems(userId)
                                        }
                                        is State.Error -> {
                                            Log.d("Abanob", "${it.message}")
                                        }
                                        else -> {
                                            Log.d("Abanob", "Load")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else
                {
                    if (!currentState.any { it.id == variant.id }) {
                        val updatedList = currentState + variant
                    repository.updateCartDraftOrder(currentState[0].draftOrderId, updatedList).collect {
                        when (it) {
                            is State.Success -> {
                                _cartItems.value = updatedList
                                fetchCartItems(userId)
                            }
                            is State.Error -> {
                                Log.d("Abanob", "Error: ${it.message}")
                            }
                            else -> {
                                Log.d("Abanob", "Loading")
                            }
                        }
                    }
                }
            }
        }
    }

}