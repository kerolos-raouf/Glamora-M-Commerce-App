package com.example.glamora.mainActivity.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SharedViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {



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
                        Log.d("Kerolos", "fetchProducts: ${state.data.size}")
                    }
                }
            }
        }
    }

}