package com.example.glamora.mainActivity.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.internetStateObserver.ConnectivityObserver
import com.example.glamora.data.model.DiscountCodeDTO
import com.example.glamora.data.model.FavoriteItemDTO
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.data.model.customerModels.CustomerInfo
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
import kotlinx.coroutines.withContext
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


    //current user info
    private val _currentCustomerInfo = MutableStateFlow(CustomerInfo())
    val currentCustomerInfo: StateFlow<CustomerInfo> = _currentCustomerInfo



    ///internet state
    private val _internetState = MutableStateFlow(ConnectivityObserver.InternetState.AVAILABLE)
    val internetState : StateFlow<ConnectivityObserver.InternetState> = _internetState

    ///operation Done with pay pal
    val operationDoneWithPayPal = MutableStateFlow(false)


    init {
        observeOnInternetState()
    }

    fun setCustomerInfo(customerInfo: CustomerInfo){
        _currentCustomerInfo.value = customerInfo
    }

    private fun observeOnInternetState()
    {
        viewModelScope.launch(Dispatchers.IO) {
            repository.observeOnInternetState().collect{
                _internetState.value = it
            }
        }
    }

    // Favorites
    private val _favoriteItemsState = MutableStateFlow<State<List<FavoriteItemDTO>>>(State.Loading)
    val favoriteItemsState: StateFlow<State<List<FavoriteItemDTO>>> = _favoriteItemsState

    fun fetchFavoriteItems() {
        if(_currentCustomerInfo.value.userId != Constants.UNKNOWN)
        {
            val userID = _currentCustomerInfo.value.userId.split("/")[4]
            viewModelScope.launch {
                repository.getFavoriteItemsForCustomer(userID)
                    .collect { state ->
                        _favoriteItemsState.value = state
                    }
            }
        }
    }

    // Delete item from favorites
    fun deleteFromFavorites(product: FavoriteItemDTO) {
        viewModelScope.launch {
            val currentState = _favoriteItemsState.value
            if (currentState is State.Success) {
                val currentList = currentState.data

                var updatedList = currentList

                if(updatedList.size == 1){
                    updatedList = emptyList()
                }
                else{
                    updatedList = currentList.filterNot { it.id == product.id }
                }

                if (updatedList.isEmpty()) {
                    withContext(Dispatchers.IO) {
                        repository.deleteDraftOrder(product.draftOrderId).collect{
                            fetchFavoriteItems()
                        }
                    }
                } else {
                    _favoriteItemsState.value = State.Loading
                    withContext(Dispatchers.IO) {
                        repository.updateFavoritesDraftOrder(product.draftOrderId, updatedList).collect{
                            fetchFavoriteItems()
                        }
                    }
                }
            }
        }
    }

    // Add item to favorites
    fun addToFavorites(product: FavoriteItemDTO) {
        viewModelScope.launch {
            val currentState = _favoriteItemsState.value
            if (currentState is State.Success) {
                val currentList = currentState.data

                if (currentList.any { it.id == product.id }) {
                    return@launch
                }

                val updatedList = currentList + product

                withContext(Dispatchers.IO) {
                    repository.updateFavoritesDraftOrder(product.draftOrderId, updatedList).collect{
                        fetchFavoriteItems()
                    }
                }
            }
        }
    }


    // ProductByID
    fun getProductByID(productID: String): ProductDTO? {
        return productList.value?.find { it.id.contains(productID) }
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

    fun setSharedPrefBoolean(key: String,value: Boolean)
    {
        repository.setSharedPrefBoolean(key, value)
    }

    fun getSharedPrefBoolean(key: String, defaultValue: Boolean) : Boolean
    {
        return repository.getSharedPrefBoolean(key, defaultValue)
    }

    fun convertCurrency()
    {
        viewModelScope.launch {
            repository.convertCurrency().collect{
                when(it)
                {
                    is State.Error -> {

                    }
                    State.Loading -> {

                    }
                    is State.Success -> {
                        setSharedPrefString(Constants.CURRENCY_MULTIPLIER_KEY,it.data.toString())
                    }
                }
            }
        }
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


    fun getCustomerInfo(userEmail : String)
    {
        viewModelScope.launch {
            repository.getShopifyUserByEmail(userEmail).collect{state->
                when(state)
                {
                    is State.Error -> {

                    }
                    State.Loading -> {

                    }
                    is State.Success -> {
                        _currentCustomerInfo.value = state.data
                    }
                }
            }
        }
    }

}