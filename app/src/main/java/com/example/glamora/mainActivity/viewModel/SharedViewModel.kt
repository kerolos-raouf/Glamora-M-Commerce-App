package com.example.glamora.mainActivity.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.internetStateObserver.ConnectivityObserver
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.util.Constants
import com.example.glamora.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _discountCodes = MutableStateFlow<List<DiscountCodeDTO>>(emptyList())
    val discountCodes: StateFlow<List<DiscountCodeDTO>> = _discountCodes

    private val _productList = MutableStateFlow<List<ProductDTO>>(emptyList())
    val productList: StateFlow<List<ProductDTO>> get() = _productList

    private val _filteredResults = MutableSharedFlow<List<ProductDTO>>()
    val filteredResults = _filteredResults.asSharedFlow()


    private val _currencyChangedFlag = MutableStateFlow(false)
    val currencyChangedFlag: StateFlow<Boolean> get() = _currencyChangedFlag



    ///internet state
    private val _internetState = MutableStateFlow(ConnectivityObserver.InternetState.AVAILABLE)
    val internetState : StateFlow<ConnectivityObserver.InternetState> = _internetState


    init {
        observeOnInternetState()
    }

    private fun observeOnInternetState()
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeOnInternetState().collect{
                _internetState.value = it
            }
        }
    }

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
                        _productList.value = state.data
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

    fun setSharedPrefString(key: String,value: String)
    {
        repository.setSharedPrefString(key, value)
    }

    fun getSharedPrefString(key: String, defaultValue: String) : String
    {
        return repository.getSharedPrefString(key, defaultValue)
    }

    fun filterList(query: String) {
        CoroutineScope(Dispatchers.Default).launch {
            val results = if (query.isEmpty()) {
                //originalList
                emptyList()
            } else {
                _productList.value.filter { it.title.contains(query, ignoreCase = true) }
            }
            _filteredResults.emit(results)
        }
    }

}