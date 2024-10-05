package com.example.glamora.fragmentHome.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.data.model.brandModel.Brands
import com.example.glamora.util.Constants
import com.example.glamora.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject


@HiltViewModel
class HomeViewModel  @Inject constructor(
    private val repository: Repository
) : ViewModel() {
    init {
        getALlBrands()
//        getRandomProducts()
    }
    private val _brandsList = MutableStateFlow<List<Brands>>(emptyList())
    val brandsList: StateFlow<List<Brands>> get() = _brandsList

//    private val _randomProductList = MutableStateFlow<List<ProductDTO>>(emptyList())
//    val randomProductList: StateFlow<List<ProductDTO>> get() = _randomProductList

    private val _filteredProductList = MutableStateFlow<List<ProductDTO>>(emptyList())
    val filteredProductList: StateFlow<List<ProductDTO>> get() = _filteredProductList


//    fun getRandomProducts() {
//        viewModelScope.launch {
//            repository.getProducts().collect { state ->
//                when (state) {
//                    is State.Error -> {
//                        Log.d("Kerolos", "fetchProducts: ${state.message}")
//                    }
//                    State.Loading -> {
//                    }
//                    is State.Success -> {
//                        val allProducts = state.data
//                        val randomProducts = allProducts.shuffled().take(10)
//                        _randomProductList.value = randomProducts
//                        Log.d("Kerolos", "Fetched random products: ${randomProducts.size}")
//                    }
//                }
//            }
//        }
//    }


    fun getALlBrands() {
        viewModelScope.launch {
            repository.getAllBrands().collect { state ->
                when (state) {
                    is State.Error -> {
                        Log.e("HomeViewModel", "Error fetching brands: ${state.message}")
                    }
                    State.Loading -> {
                        Log.d("HomeViewModel", "Loading brands data...")
                    }
                    is State.Success -> {
                        Log.d("HomeViewModel", "Success: fetched ${state.data.size} brands")
                        _brandsList.value = state.data
                    }
                }
            }
        }
    }

    fun getALlcategory() {
        viewModelScope.launch {
            repository.getAllBrands().collect { state ->
                when (state) {
                    is State.Error -> {
                        Log.e("HomeViewModel", "Error fetching brands: ${state.message}")
                    }
                    State.Loading -> {
                        Log.d("HomeViewModel", "Loading brands data...")
                    }
                    is State.Success -> {
                        Log.d("HomeViewModel", "Success: fetched ${state.data.size} brands")
                        _brandsList.value = state.data
                    }
                }
            }
        }
    }

    // Function to filter products by category (men, women, kids, sale)
    fun filterProductsByCategory(category: String) {
        viewModelScope.launch {
            repository.getProducts().collect { state ->
                when (state) {
                    is State.Error -> {
                        Log.d("Kerolos", "Error fetching products: ${state.message}")
                    }
                    State.Loading -> {
                        // Handle loading state if necessary
                    }
                    is State.Success -> {
                        val filteredProducts = when (category) {
                            "men" -> state.data.filter { it.category == Constants.PRODUCT_BY_MEN }
                            "women" -> state.data.filter { it.category == Constants.PRODUCT_BY_WOMEN }
                            "kids" -> state.data.filter { it.category == Constants.PRODUCT_BY_KIDS}
                            "sale" -> state.data.filter { it.category==Constants.PRODUCT_BY_SALE }
                            else -> state.data
                        }
                        _filteredProductList.value = filteredProducts
                        Log.d("Kerolos", "Filtered products for category '$category': ${filteredProducts.size}")
                    }
                }
            }
        }
    }


}