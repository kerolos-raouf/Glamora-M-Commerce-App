package com.example.glamora.fragmentLogin.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.customerModels.CustomerInfo
import com.example.glamora.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _loginState = MutableStateFlow<State<CustomerInfo>?>(null)
    val loginState: StateFlow<State<CustomerInfo>?> get() = _loginState

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> get() = _toastMessage

    private val _customerEmail = MutableStateFlow<String?>(null)
    val customerEmail: StateFlow<String?> get() = _customerEmail


    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.value = State.Loading
            repository.loginWithEmail(email, password).collect { result ->
                result.fold(
                    onSuccess = {
                        _customerEmail.value = it.email
                        _loginState.value = State.Success(it)
                    },
                    onFailure = {
                        _toastMessage.value = it.message
                        _loginState.value = State.Error(it.message ?: "Unknown error")
                    }
                )
            }
        }
    }

    fun resetUserPass(email: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.value = State.Loading
            repository.resetUserPassword(email).collect { result ->
                result.fold(
                    onSuccess = { _toastMessage.value = "Check your email" },
                    onFailure = {
                        _toastMessage.value = it.message
                        _loginState.value = State.Error(it.message ?: "Failed to send reset email")
                    }
                )
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _loginState.value = State.Loading
            repository.loginWithGoogle(idToken).collect { result ->
                result.fold(
                    onSuccess = {
                        _customerEmail.value = it.email
                        repository.getShopifyUserByEmail(it.email).collect { result2 ->
                            when (result2) {
                                is State.Error -> {
                                    if( result2.message == "User not found") {
                                        val nameParts = extractNameFromEmail(it.email)
                                        repository.createShopifyUser(
                                            it.email,
                                            nameParts.firstOrNull() ?: "",
                                            nameParts.getOrNull(1) ?: "",
                                            null
                                        ).collect{ addResult ->
                                            addResult.fold(
                                                onSuccess = {
                                                    _loginState.value = State.Success(it)
                                                },
                                                onFailure = {
                                                    _loginState.value = State.Error(it.message ?: "Unknown error")
                                                }
                                            )
                                        }
                                    }
                                    else{
                                        _toastMessage.value = result2.message
                                        _loginState.value = State.Error(result2.message)
                                    }
                                }
                                State.Loading -> {
                                    _loginState.value = State.Loading
                                }
                                is State.Success -> {
                                    _loginState.value = State.Success(it)
                                }
                            }
                        }
                    },
                    onFailure = {
                        _loginState.value = State.Error(it.message ?: "Unknown error")
                    }
                )
            }
        }
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }

    private fun extractNameFromEmail(email: String): List<String> {
        return email.split("@")[0].split("[_.]".toRegex())
    }
}