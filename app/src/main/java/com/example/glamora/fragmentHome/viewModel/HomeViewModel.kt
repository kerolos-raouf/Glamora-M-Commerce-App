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
    }
    private val _brandsList = MutableStateFlow<List<Brands>>(emptyList())
    val brandsList: StateFlow<List<Brands>> get() = _brandsList


    private val _filteredProductList = MutableStateFlow<List<ProductDTO>>(emptyList())
    val filteredProductList: StateFlow<List<ProductDTO>> get() = _filteredProductList


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

//    fun filterProductsByCategory(category: String) {
//        viewModelScope.launch {
//            repository.getProducts().collect { state ->
//                when (state) {
//                    is State.Error -> {
//                        Log.d("HomeViewModel", "Error fetching products: ${state.message}")
//                    }
//                    State.Loading -> {
//                    }
//                    is State.Success -> {
//                        _filteredProductList.value = state.data
//                        Log.d("HomeViewModel", "Filtered products for category '$category': ${state.data}")
//                        }
//
//                    }
//                }
//            }
//        }
//    }


}