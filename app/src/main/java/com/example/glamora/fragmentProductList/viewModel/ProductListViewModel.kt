package com.example.glamora.fragmentProductList.viewModel

import android.icu.text.CaseMap.Title
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.ProductDTO
import com.example.glamora.util.Constants
import com.example.glamora.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _filteredProducts = MutableStateFlow<List<ProductDTO>>(emptyList())
    val filteredProducts: StateFlow<List<ProductDTO>> get() = _filteredProducts



    fun filterProductsByBrand(products: List<ProductDTO>, title: String) {
        viewModelScope.launch {
            try {
                val filtered = products.filter { product ->
                    product.brand == title
                }

                _filteredProducts.value = filtered
                Log.d("ProductListViewModel", "Fetched ${filtered.size} products for brand: $title")

            } catch (e: Exception) {
                Log.e("ProductListViewModel", "Exception filtering products: ${e.message}")
            }
        }
    }


    fun filterProductsByCategory(products: List<ProductDTO>,category: String) {
        viewModelScope.launch {
            try {
                val filtered = products.filter { product ->
                    product.category == category
                }

                _filteredProducts.value = filtered
                Log.d("ProductListViewModel", "Fetched ${filtered.size} products for brand: $category")

            } catch (e: Exception) {
                Log.e("ProductListViewModel", "Exception filtering products: ${e.message}")
            }
        }
    }

}
