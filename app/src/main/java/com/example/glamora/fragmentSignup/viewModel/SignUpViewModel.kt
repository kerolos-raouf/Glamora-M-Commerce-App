package com.example.glamora.fragmentSignup.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.model.customerModels.CustomerInfo
import com.example.glamora.util.State
import com.example.glamora.util.getFirstAndLastName
import com.example.glamora.util.isNotShort
import com.example.glamora.util.isPasswordEqualRePassword
import com.example.glamora.util.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel() {

    private val _signUpState = MutableStateFlow<State<CustomerInfo>?>(null)
    val signUpState: StateFlow<State<CustomerInfo>?> get() = _signUpState

    fun validateAndSignUp(
        name: String,
        email: String,
        password: String,
        rePassword: String,
        phone: String
    ) {
        _signUpState.value = State.Loading

        try {
            val validName = isNotShort(name)
            val validEmail = isValidEmail(email)
            val validPass = isNotShort(password)
            val validRepass = isPasswordEqualRePassword(password, rePassword)
            val validPhone = isNotShort(phone, 11)

            if (validName && validEmail && validPass && validRepass && validPhone) {
                val (firstName, lastName) = getFirstAndLastName(name)
                viewModelScope.launch(Dispatchers.IO) {
                    repository.createShopifyUser(email, firstName, lastName, phone)
                        .collect { result ->
                            result.fold(
                                onSuccess = { _ ->
                                    repository.signUp(email, password).collect { signUpResult ->
                                        signUpResult.fold(
                                            onSuccess = { customerInfo ->
                                                _signUpState.value = State.Success(customerInfo)
                                            },
                                            onFailure = { signUpError ->
                                                _signUpState.value = State.Error(
                                                    signUpError.message ?: "Sign-up failed"
                                                )
                                            }
                                        )
                                    }
                                },
                                onFailure = { createError ->
                                    _signUpState.value = State.Error(
                                        createError.message ?: "Failed to create Shopify user"
                                    )
                                }
                            )
                        }
                }
            } else {
                _signUpState.value = State.Error("Validation failed. Please check your inputs.")
            }
        } catch (e: Exception) {
            _signUpState.value = State.Error("Sign-up failed: ${e.message}")
        }
    }

}
