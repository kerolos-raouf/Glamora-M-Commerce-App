package com.example.glamora.fragmentCart.viewModel

import android.util.Log
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

    fun fetchCartItems(userId: String = "7552199491722"){
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
                        _cartItems.value = state.data.reversed()
                        _loading.value = false
                    }
                }
            }
        }
    }

    fun deleteCartItemFromDraftOrder(cartItemDTO: CartItemDTO,userId: String = "7552199491722")
    {
        val newCartItems = _cartItems.value?.filter { it.id != cartItemDTO.id} ?: emptyList()
        if(newCartItems.isEmpty()){
            deleteDraftOrder(cartItemDTO.draftOrderId,userId)
        }else
        {
            deleteDraftOrderItem(cartItemDTO.draftOrderId,newCartItems,userId)
        }
    }

    private fun deleteDraftOrder(draftOrderId: String, userId: String = "7552199491722"){
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
                        _message.value = "Draft order was deleted successfully"
                        fetchCartItems(userId)
                    }
                }
            }
        }
    }

    private fun deleteDraftOrderItem(draftOrderId: String, newCartItems: List<CartItemDTO>,userId: String = "7552199491722"){
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
                        _message.value = "Cart item was deleted successfully"
                        _loading.value = false
                        fetchCartItems()
                    }
                }
            }
        }
    }

    fun updateDraftOrder(draftOrderId: String,variantId : String,quantity : Int,userId: String = "7552199491722"){
//        viewModelScope.launch {
//            repository.updateCartDraftOrder(draftOrderId,variantId,quantity).collect{
//                when(it){
//                    is State.Error -> {
//                        _message.value = it.message
//                        _loading.value = false
//                    }
//                    State.Loading -> {
//                        _loading.value = true
//                    }
//                    is State.Success -> {
//                        _message.value = "Draft order was updated successfully"
//                        //fetchCartItems(userId)
//                        _loading.value = false
//                    }
//                }
//            }
//        }
    }


    ////create order steps
    //1 - create final draft order
    fun createFinalDraftOrder(
        customerId: String = "gid://shopify/Customer/7552199491722",
        customerEmail : String = "kerolos.raouf5600@gmail.com",
        discountAmount : Double
    ){
        viewModelScope.launch {
            if(!cartItems.value.isNullOrEmpty()){
                repository.createFinalDraftOrder(customerId,customerEmail, cartItems.value ?: emptyList(),discountAmount).collect{
                    when(it){
                        is State.Error -> {
                            _message.value = it.message
                            _loading.value = false
                        }
                        State.Loading -> {
                            _loading.value = true
                        }
                        is State.Success -> {
                            createOrderFromDraftOrder(it.data)
                        }
                    }
                }
            }
        }
    }

    //2 - create order from draft order
    private fun createOrderFromDraftOrder(draftOrderId: String){
        viewModelScope.launch {
            repository.createOrderFromDraftOrder(draftOrderId).collect{state ->
                when(state){
                    is State.Error -> {
                        _message.value = state.message
                        _loading.value = false
                    }
                    State.Loading -> {
                    }
                    is State.Success -> {

                    }
                }
            }
        }
    }



}