package com.example.glamora.fragmentProductList.viewModel

import android.icu.text.CaseMap.Title
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.ProductDTO
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

    fun filterProductsByBrand(title: String) {
        viewModelScope.launch {
            try {
                repository.getProducts().collect { state ->
                    when (state) {
                        is State.Error -> {
                            Log.e("ProductListViewModel", "Error fetching products: ${state.message}")
                        }
                        State.Loading -> {
                            Log.d("ProductListViewModel", "Loading products for brand: $title")
                        }
                        is State.Success -> {
                            Log.d("ProductListViewModel", "All fetched products: ${state.data.map { it.title }}")


                            val filtered = state.data.filter { product ->
                                product.brand == title
                            }

                            _filteredProducts.value = filtered
                            Log.d("ProductListViewModel", "Fetched ${filtered.size} products for brand ID: $title")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ProductListViewModel", "Exception fetching products: ${e.message}")
            }
        }
    }

    fun filterProductsByCategory(category: String) {
        viewModelScope.launch {
            try {
                repository.getProducts().collect { state ->
                    when (state) {
                        is State.Error -> {
                            Log.e("ProductListViewModel", "Error fetching products: ${state.message}")
                        }
                        State.Loading -> {
                            //Log.d("ProductListViewModel", "Loading products for brand: $category")
                        }
                        is State.Success -> {
                            Log.d("ProductListViewModel", "All fetched products: ${state.data.map { it.category }}")


                            val filtered = state.data.filter { product ->
                                product.category == category
                            }

                            _filteredProducts.value = filtered
                            Log.d("ProductListViewModel", "Fetched ${filtered.size} products for brand ID: $category")
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("ProductListViewModel", "Exception fetching products: ${e.message}")
            }
        }
    }


//    fun filterProducts(title: String, productType: String?, fromPrice: Double?, toPrice: Double?) {
//        viewModelScope.launch {
//            try {
//                repository.getProducts().collect { state ->
//                    when (state) {
//                        is State.Error -> {
//                            Log.e("ProductListViewModel", "Error fetching products: ${state.message}")
//                        }
//                        State.Loading -> {
//                            Log.d("ProductListViewModel", "Loading products for brand: $title")
//                        }
//                        is State.Success -> {
//                            Log.d("ProductListViewModel", "All fetched products: ${state.data.map { it.title }}")
//
//                            // Filter based on brand first
//                            var filtered = state.data.filter { product ->
//                                product.brand == title
//                            }
//
//                            // Further filter by product type (category)
//                            productType?.let {
//                                filtered = filtered.filter { product ->
//                                    product.category == it
//                                }
//                            }
//
//                            // Filter by price range
//                            if (fromPrice != null || toPrice != null) {
//                                filtered = filtered.filter { product ->
//                                    product.availableProducts.any { availableProduct ->
//                                        val productPrice = availableProduct.price.toDouble()
//                                        (fromPrice == null || productPrice >= fromPrice) &&
//                                                (toPrice == null || productPrice <= toPrice)
//                                    }
//                                }
//                            }
//
//                            _filteredProducts.value = filtered
//                            Log.d("ProductListViewModel", "Filtered ${filtered.size} products for category: $productType and price range: $fromPrice to $toPrice")
//                        }
//                    }
//                }
//            } catch (e: Exception) {
//                Log.e("ProductListViewModel", "Exception fetching products: ${e.message}")
//            }
//        }
//    }


}
