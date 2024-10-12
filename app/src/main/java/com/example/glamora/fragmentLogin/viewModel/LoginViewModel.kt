package com.example.glamora.fragmentLogin.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.customerModels.CustomerInfo
import com.example.glamora.util.State
import dagger.hilt.android.lifecycle.HiltViewModel
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
        viewModelScope.launch {
            _loginState.value = State.Loading
            repository.loginWithEmail(email, password).collect { result ->
                result.fold(
                    onSuccess = {
                        _customerEmail.value = it.email
                        _loginState.value = State.Success(it)
                    },
                    onFailure = { _loginState.value = State.Error(it.message ?: "Unknown error") }
                )
            }
        }
    }

    fun resetUserPass(email: String) {
        viewModelScope.launch {
            _loginState.value = State.Loading
            repository.resetUserPassword(email).collect { result ->
                result.fold(
                    onSuccess = { _toastMessage.value = "Check your email" },
                    onFailure = {
                        _loginState.value = State.Error(it.message ?: "Failed to send reset email")
                    }
                )
            }
        }
    }

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            _loginState.value = State.Loading
            repository.loginWithGoogle(idToken).collect { result ->
                result.fold(
                    onSuccess = {
                        _customerEmail.value = it.email
                        repository.getShopifyUserByEmail(it.email).collect { result ->
                            when (result) {
                                is State.Error -> {
                                    _toastMessage.value = result.message
                                }

                                State.Loading -> {
                                    _loginState.value = State.Loading
                                }

                                is State.Success -> {
                                    val email = result.data.email
                                    if (email != it.email) {
                                        val nameParts = extractNameFromEmail(it.email)
                                        repository.createShopifyUser(
                                            it.email,
                                            nameParts.firstOrNull() ?: "",
                                            nameParts.getOrNull(1) ?: "",
                                            null
                                        )
                                        _loginState.value = State.Success(it)

                                    } else {
                                        _loginState.value = State.Success(it)
                                    }
                                }
                            }
                        }
                        _loginState.value = State.Success(it)
                    },
                    onFailure = { _loginState.value = State.Error(it.message ?: "Unknown error") }
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