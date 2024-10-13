package com.example.glamora.fragmentAddressDetails.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.AddressModel
import com.example.glamora.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class
AddressViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _address = MutableStateFlow<List<AddressModel>>(emptyList())
    val address: StateFlow<List<AddressModel>> = _address

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _message = MutableSharedFlow<String>()
    val message: SharedFlow<String> = _message



    fun updateCustomerAddress(
        customerId: String,
        email: String,
        address: AddressModel) {

        val newAddressList = _address.value.toMutableList()
        newAddressList.add(address)

        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCustomerAddress(customerId,newAddressList).collect{state->
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
                        _message.emit("Address updated successfully")
                        getCustomerAddressesByEmail(email)
                    }
                }
            }
        }
    }


    fun getCustomerAddressesByEmail(email : String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getCustomerAddressesByEmail(email).collect{state->
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
                        _address.value = state.data
                        _loading.value = false
                    }
                }
            }
        }
    }

}