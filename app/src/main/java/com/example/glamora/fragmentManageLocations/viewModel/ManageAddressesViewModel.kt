package com.example.glamora.fragmentManageLocations.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.AddressModel
import com.example.glamora.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ManageAddressesViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel(){

    private val _customerAddresses = MutableStateFlow<List<AddressModel>?>(null)
    val customerAddresses : StateFlow<List<AddressModel>?> = _customerAddresses

    private val _message = MutableSharedFlow<String>()
    val message : SharedFlow<String> = _message

    private val _loading = MutableStateFlow(false)
    val loading : StateFlow<Boolean> = _loading

    fun deleteCustomerAddresses(customerID : String,addresses : List<AddressModel>)
    {
        viewModelScope.launch {
            repository.updateCustomerAddress(customerID,addresses).collect { state ->
                when(state)
                {
                    is State.Error -> {
                        _message.emit(state.message)
                        _loading.value = false
                    }
                    State.Loading -> {
                        _loading.value = true
                    }
                    is State.Success -> {
                        _customerAddresses.value = addresses
                        _loading.value = false
                    }
                }
            }
        }
    }

}