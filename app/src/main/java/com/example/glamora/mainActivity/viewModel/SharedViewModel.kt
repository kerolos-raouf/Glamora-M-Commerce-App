package com.example.glamora.mainActivity.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.util.Constants
import com.example.glamora.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject


@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _discountCodes = MutableStateFlow<List<DiscountCodeDTO>>(emptyList())
    val discountCodes: StateFlow<List<DiscountCodeDTO>> = _discountCodes

    private val _ProductList = MutableStateFlow<List<ProductDTO>>(emptyList())
    val productList: StateFlow<List<ProductDTO>> get() = _ProductList


    private val _currencyChangedFlag = MutableStateFlow(false)
    val currencyChangedFlag: StateFlow<Boolean> get() = _currencyChangedFlag

    var selectedCurrency: MutableStateFlow<String> = MutableStateFlow("EGP")


    fun fetchProducts()
    {
        viewModelScope.launch {
            repository.getProducts().collect{state->
                when (state)
                {
                    is State.Error -> {
                        Log.d("Kerolos", "fetchProducts: ${state.message}")
                    }
                    State.Loading -> {

                    }
                    is State.Success -> {
                        _ProductList.value = state.data
                        Log.d("Kerolos", "fetchProducts: ${state.data.size}")
                        for(item in state.data)
                        {
                            Log.d("Kerolos", "fetchPriceRules: ${item.id} ${item.title}")
                        }
                    }
                }
            }
        }
    }

    fun fetchPriceRules()
    {
        viewModelScope.launch {
            repository.getPriceRules().collect{state->
                when (state)
                {
                    is State.Error -> {
                        Log.d("Kerolos", "fetchPriceRules: ${state.message}")
                    }
                    State.Loading -> {

                    }
                    is State.Success -> {
                        for(item in state.data)
                        {
                            Log.d("Kerolos", "fetchPriceRules: ${item.id} ${item.percentage}")
                        }
                    }
                }
            }
        }
    }

    fun fetchDiscountCodes()
    {
        viewModelScope.launch {
            repository.getDiscountCodes().collect{state->
                when (state)
                {
                    is State.Error -> {
                        Log.d("Kerolos", "fetchPriceRules: ${state.message}")
                    }
                    State.Loading -> {

                    }
                    is State.Success -> {
                        _discountCodes.value = state.data
                    }
                }
            }
        }
    }


    fun fetchCurrentCustomer() {
        viewModelScope.launch {
            repository.getCustomerUsingEmail("kerolos.raouf5600@gmail.com").collect { state ->
                when (state) {
                    is State.Error -> {
                        Log.d("Kerolos", "fetchCurrentCustomer: ${state.message}")
                    }

                    State.Loading -> {

                    }

                    is State.Success -> {
                        Log.d("Kerolos", "fetchCurrentCustomer: ${state.data}")
                    }
                }
            }
        }
    }

    fun setSharedPrefString(key: String,value: String){
    if (key == Constants.CURRENCY_KEY) {
        selectedCurrency.value = Constants.CURRENCY_KEY
        _currencyChangedFlag.value = true
    }
    repository.setSharedPrefString(key, value)
}

    fun getSharedPrefString(key: String, defaultValue: String) : String
    {
        return repository.getSharedPrefString(key, defaultValue)
    }

}