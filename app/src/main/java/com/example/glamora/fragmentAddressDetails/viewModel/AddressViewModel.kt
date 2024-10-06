package com.example.glamora.fragmentAddressDetails.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.AddressModel
import com.example.glamora.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    fun updateCustomerAddress(customerId: String = "gid://shopify/Customer/7555870916746", address: AddressModel) {
        viewModelScope.launch {
            repository.updateCustomerAddress(customerId,address).collect{
                when(it)
                {
                    is State.Error -> {
                        _message.value = it.message.toString()
                        _loading.value = false
                    }
                    State.Loading -> {
                        _loading.value = true
                    }
                    is State.Success -> {
                        _message.value = "Address updated successfully"
                        _loading.value = false
                    }
                }
            }
        }
    }

}