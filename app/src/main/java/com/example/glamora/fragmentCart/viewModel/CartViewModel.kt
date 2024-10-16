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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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



    private val _message = MutableSharedFlow<String>()
    val message : SharedFlow<String> = _message

    private val _loading = MutableStateFlow(false)
    val loading : StateFlow<Boolean> = _loading

     val showDoneBottomSheet = MutableSharedFlow<Boolean>()


    fun fetchCartItems(userId: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCartItemsForCustomer(userId).collect{state ->
                when(state){
                    is State.Error -> {
                        _message.emit(state.message)
                        _loading.value = false
                    }
                    State.Loading -> {
                        _loading.value = true
                    }
                    is State.Success -> {
                        withContext(Dispatchers.Main){
                            _cartItems.value = state.data
                        }
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

    fun deleteDraftOrder(draftOrderId: String, userId: String, showDoneDialog : Boolean = false){
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteDraftOrder(draftOrderId).collect{
                when(it){
                    is State.Error -> {
                        _message.emit(it.message)
                        _loading.value = false
                    }
                    State.Loading -> {
                        _loading.value = true
                    }
                    is State.Success -> {
                        fetchCartItems(userId)

                        if(showDoneDialog){
                            showDoneBottomSheet.emit(true)
                        }
                    }
                }
            }
        }
    }

    fun updateDraftOrder(
        draftOrderId: String,
        newCartItems: List<CartItemDTO>,
        userId: String,
        fetchAgain : Boolean = true
        ){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCartDraftOrder(draftOrderId,newCartItems).collect{state->
                when(state){
                    is State.Error -> {
                        _message.emit(state.message)
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
        address: AddressModel,
        tag: String = Constants.CART_DRAFT_ORDER_KEY
    ){
        if(!cartItems.value.isNullOrEmpty()){
            viewModelScope.launch(Dispatchers.IO) {
                if(!cartItems.value.isNullOrEmpty()){
                    repository.createFinalDraftOrder(
                        customerId,customerEmail,
                        cartItems.value ?: emptyList(),
                        discountAmount,
                        address,
                        tag)
                        .collect{state->
                            when(state){
                                is State.Error -> {
                                    _message.emit(state.message)
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
            viewModelScope.launch(Dispatchers.IO) {
                _message.emit("Your cart is empty")
            }
        }

    }

    //2 - create order from draft order
    private fun createOrderFromDraftOrder(finalDraftOrderId: String,oldDraftOrderId: String,userId : String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.createOrderFromDraftOrder(finalDraftOrderId).collect{ state ->
                when(state){
                    is State.Error -> {
                        _message.emit(state.message)
                        _loading.value = false
                    }
                    State.Loading -> {
                    }
                    is State.Success -> {
                        deleteDraftOrder(oldDraftOrderId,userId)
                        deleteDraftOrder(finalDraftOrderId,userId,true)
                    }
                }
            }
        }
    }


    //////////////////pay pal
    val openApprovalUrlState = MutableStateFlow("")

    private var orderId = ""

    // Function to start creating an order
    fun startOrder(price : String = "5.00") {
        val uniqueId = UUID.randomUUID().toString()

        // Construct the order request payload
        val orderRequest = OrderRequest(
            purchase_units = listOf(
                PurchaseUnit(
                    reference_id = uniqueId,
                    amount = Amount(currency_code = "USD", value = price)
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
                    openApprovalUrlState.value = approvalLink
                } else {
                    Log.e("Kerolos", "Approval link not found in the response.")

                }
            } catch (e: Exception) {
                Log.d("Kerolos", "startOrder: ${e.localizedMessage}")
            }
        }
    }



    //Fetch Access Token
    fun fetchAccessToken() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val accessToken = payPalRepository.fetchAccessToken()
                Log.d("Kerolos", "fetchAccessToken: $accessToken")
            } catch (e: Exception) {
                Log.d("Kerolos", "fetchAccessToken: ${e.localizedMessage}")
            }
        }
    }


}