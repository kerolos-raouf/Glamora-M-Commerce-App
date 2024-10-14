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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import javax.inject.Inject


@HiltViewModel
class HomeViewModel  @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _brandsList = MutableStateFlow<List<Brands>>(emptyList())
    val brandsList: StateFlow<List<Brands>> = _brandsList


    private val _loading = MutableStateFlow(false)
    val loading : StateFlow<Boolean> = _loading

    init {
        getALlBrands()
    }
    fun getALlBrands() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAllBrands().collect { state ->
                when (state) {
                    is State.Error -> {
                        Log.e("HomeViewModel", "Error fetching brands: ${state.message}")
                        _loading.value = false
                    }
                    State.Loading -> {
                        _loading.value = true
                    }
                    is State.Success -> {
                        _brandsList.value = state.data
                        _loading.value = false
                    }
                }
            }
        }
    }

}