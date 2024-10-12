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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailsViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _cartItems = MutableStateFlow<List<CartItemDTO>>(emptyList())

    val _state = MutableStateFlow<State<Boolean>>(State.Loading)
    val state: StateFlow<State<Boolean>> = _state


    fun fetchCartItems(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.value = State.Loading
            repository.getCartItemsForCustomer(userId).collect { state ->
                when (state) {
                    is State.Error -> {
                        _state.value = State.Error("Error in Fetching Card Items")
                    }

                    State.Loading -> {
                        _state.value = State.Loading
                    }

                    is State.Success -> {
                        _cartItems.value = state.data
                        _state.value = State.Success(true)
                    }
                }
            }
        }
    }


    fun addToCard(variant: CartItemDTO, userId: String, userEmail: String) {

        viewModelScope.launch(Dispatchers.IO) {
            _state.value = State.Loading
            val currentState = _cartItems.value

            if (currentState.isEmpty()) {
                repository.createFinalDraftOrder(
                    userId,
                    userEmail,
                    listOf(variant),
                    0.0,
                    AddressModel(),
                    Constants.CART_DRAFT_ORDER_KEY
                ).collect {
                    when (it) {
                        is State.Error -> {
                            Log.d("Abanob", "addToCard: ${it.message}")
                        }
                        State.Loading -> {}
                        is State.Success -> {
                            val cardItem = variant.copy(draftOrderId = it.data)

                            repository.updateCartDraftOrder(cardItem.draftOrderId, listOf(cardItem))
                                .collect {
                                    when (it) {
                                        is State.Success -> {
                                            fetchCartItems(userId)
                                            _state.value = State.Success(true)
                                        }

                                        is State.Error -> {
                                            Log.d("Abanob", "${it.message}")
                                        }

                                        else -> {
                                        }
                                    }
                                }
                        }
                    }
                }
            } else {
                if (!currentState.any { it.id == variant.id }) {
                    val updatedList = currentState + variant
                    repository.updateCartDraftOrder(currentState[0].draftOrderId, updatedList)
                        .collect {
                            when (it) {
                                is State.Success -> {
                                    _cartItems.value = updatedList
                                    fetchCartItems(userId)
                                    _state.value = State.Success(true)
                                }

                                is State.Error -> {
                                    Log.d("Abanob", "Error: ${it.message}")
                                }

                                else -> {

                                }
                            }
                        }
                }
            }
        }
    }

}