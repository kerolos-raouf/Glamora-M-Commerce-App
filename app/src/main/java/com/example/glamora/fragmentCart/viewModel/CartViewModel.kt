package com.example.glamora.fragmentCart.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.CartItemDTO
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

    private val _cartItems = MutableLiveData<List<CartItemDTO>>(emptyList())
    val cartItems : LiveData<List<CartItemDTO>> = _cartItems



    private val _message = MutableStateFlow("")
    val message : StateFlow<String> = _message

    private val _loading = MutableStateFlow(false)
    val loading : StateFlow<Boolean> = _loading

    private val _showDoneBottomSheet = MutableStateFlow(false)
    val showDoneBottomSheet : StateFlow<Boolean> = _showDoneBottomSheet

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

    fun deleteCartItemFromDraftOrder(cartItemDTO: CartItemDTO,userId: String)
    {
        val newCartItems = _cartItems.value?.filter { it.id != cartItemDTO.id} ?: emptyList()
        if(newCartItems.isEmpty()){
            deleteDraftOrder(cartItemDTO.draftOrderId,userId)
        }else
        {
            updateDraftOrder(cartItemDTO.draftOrderId,newCartItems,userId)
        }
    }

    private fun deleteDraftOrder(draftOrderId: String, userId: String){
        viewModelScope.launch {
            repository.deleteDraftOrder(draftOrderId).collect{
                when(it){
                    is State.Error -> {
                        _message.value = it.message
                        _loading.value = false
                    }
                    State.Loading -> {
                        _loading.value = true
                    }
                    is State.Success -> {
                        fetchCartItems(userId)
                    }
                }
            }
        }
    }

    private fun updateDraftOrder(
        draftOrderId: String,
        newCartItems: List<CartItemDTO>,
        userId: String,
        fetchAgain : Boolean = true
        ){
        viewModelScope.launch {
            repository.updateCartDraftOrder(draftOrderId,newCartItems).collect{state->
                when(state){
                    is State.Error -> {
                        _message.value = state.message
                        _loading.value = false
                    }
                    State.Loading -> {
                        _loading.value = true
                    }
                    is State.Success -> {
                        _loading.value = false
                        if(fetchAgain)
                            fetchCartItems(userId)
                    }
                }
            }
        }
    }

    fun updateCartItemQuantity(draftOrderId: String, variantId : String, quantity : Int, userId: String = "7552199491722"){
        val newCartItems = _cartItems.value?.map {
            if(it.id == variantId){
                it.copy(quantity = quantity)
            }else{
                it
            }
        } ?: emptyList()
        updateDraftOrder(draftOrderId,newCartItems,userId,false)
    }


    ////create order steps
    //1 - create final draft order
    fun createFinalDraftOrder(
        customerId: String,
        customerEmail : String,
        discountAmount : Double
    ){
        if(!cartItems.value.isNullOrEmpty()){
            viewModelScope.launch {
                if(!cartItems.value.isNullOrEmpty()){
                    repository.createFinalDraftOrder(customerId,customerEmail, cartItems.value ?: emptyList(),discountAmount)
                        .collect{state->
                            when(state){
                                is State.Error -> {
                                    _message.value = state.message
                                    _loading.value = false
                                }
                                State.Loading -> {
                                    _loading.value = true
                                }
                                is State.Success -> {
                                    createOrderFromDraftOrder(state.data,cartItems.value!![0].draftOrderId,customerId.split("/")[customerId.split("/").size-1])
                                }
                            }
                        }
                }
            }
        }else
        {
            _message.value = "Your cart is empty"
        }

    }

    //2 - create order from draft order
    private fun createOrderFromDraftOrder(finalDraftOrderId: String,oldDraftOrderId: String,userId : String){
        viewModelScope.launch {
            repository.createOrderFromDraftOrder(finalDraftOrderId).collect{ state ->
                when(state){
                    is State.Error -> {
                        _message.value = state.message
                        _loading.value = false
                    }
                    State.Loading -> {
                    }
                    is State.Success -> {
                        deleteDraftOrder(oldDraftOrderId,userId)
                        _showDoneBottomSheet.value = true
                        deleteDraftOrder(finalDraftOrderId,userId)
                    }
                }
            }
        }
    }



}