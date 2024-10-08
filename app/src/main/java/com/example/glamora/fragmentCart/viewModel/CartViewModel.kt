package com.example.glamora.fragmentCart.viewModel

import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.AddressModel
import com.example.glamora.data.model.CartItemDTO
import com.example.glamora.data.model.payPalModels.Amount
import com.example.glamora.data.model.payPalModels.ExperienceContext
import com.example.glamora.data.model.payPalModels.OrderRequest
import com.example.glamora.data.model.payPalModels.PayPalExperience
import com.example.glamora.data.model.payPalModels.PaymentSource
import com.example.glamora.data.model.payPalModels.PurchaseUnit
import com.example.glamora.data.repository.payPalRepo.IPayPalRepository
import com.example.glamora.data.repository.payPalRepo.PayPalRepository
import com.example.glamora.util.Constants
import com.example.glamora.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject
import kotlin.math.log

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: Repository,
    private val payPalRepository: IPayPalRepository
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
        discountAmount : Double,
        address: AddressModel
    ){
        if(!cartItems.value.isNullOrEmpty()){
            viewModelScope.launch {
                if(!cartItems.value.isNullOrEmpty()){
                    repository.createFinalDraftOrder(
                        customerId,customerEmail,
                        cartItems.value ?: emptyList(),
                        discountAmount,
                        address)
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


    //////////////////pay pal
    private val _openApprovalUrlState = MutableStateFlow("")
    val openApprovalUrlState : StateFlow<String> = _openApprovalUrlState

    private var orderId = ""

    // Function to start creating an order
    fun startOrder() {
        val uniqueId = UUID.randomUUID().toString()

        // Construct the order request payload
        val orderRequest = OrderRequest(
            purchase_units = listOf(
                PurchaseUnit(
                    reference_id = uniqueId,
                    amount = Amount(currency_code = "USD", value = "5.00")
                )
            ),
            payment_source = PaymentSource(
                paypal = PayPalExperience(
                    experience_context = ExperienceContext(
                        payment_method_preference = "IMMEDIATE_PAYMENT_REQUIRED",
                        brand_name = "Couture-Corner",
                        locale = "en-US",
                        landing_page = "LOGIN",
                        shipping_preference = "NO_SHIPPING",
                        user_action = "PAY_NOW",
                        return_url = Constants.RETURN_URL,
                        cancel_url = "https://example.com/cancelUrl"
                    )
                )
            )
        )

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = payPalRepository.createOrder(orderRequest)
                orderId = response.id
                val approvalLink = response.links[1].href

                if (approvalLink.isNotEmpty()) {
                    Log.i("Kerolos", "Approval Link: $approvalLink")
                    // Redirect the user to the approval link
                    _openApprovalUrlState.value = approvalLink
                } else {
                    Log.e("Kerolos", "Approval link not found in the response.")

                }
            } catch (e: Exception) {
                Log.d("Kerolos", "startOrder: ${e.localizedMessage}")
            }
        }
    }

    init {
        fetchAccessToken()
    }

    //Fetch Access Token
    private fun fetchAccessToken() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accessToken = payPalRepository.fetchAccessToken()
                Log.d("Kerolos", "fetchAccessToken: $accessToken")
            } catch (e: Exception) {
                Log.d("Kerolos", "fetchAccessToken: ${e.localizedMessage}")
            }
        }
    }

    // Function to capture an order
    private fun captureOrder(token: String, payerId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val capturedOrderId = payPalRepository.captureOrder(orderId, "10.00", token, payerId)
            } catch (e: Exception) {
                Log.d("Kerolos", "captureOrder: ${e.localizedMessage}")
            }
        }
    }

}