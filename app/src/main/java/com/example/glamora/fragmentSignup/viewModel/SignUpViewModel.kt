package com.example.glamora.fragmentSignup.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.contracts.Repository
import com.example.glamora.data.firebase.FirebaseHandler
import com.example.glamora.data.firebase.SignUpState
import com.example.glamora.util.getFirstAndLastName
import com.example.glamora.util.isNotShort
import com.example.glamora.util.isPasswordEqualRePassword
import com.example.glamora.util.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel  @Inject constructor(
    private val repository: Repository
): ViewModel() {

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> get() = _signUpState

    private val firebaseHandler = FirebaseHandler()

    fun validateAndSignUp(
        name: String,
        email: String,
        password: String,
        rePassword: String,
        phone: String
    ) {
        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading

            try {
                val validName = isNotShort(name)
                val validEmail = isValidEmail(email)
                val validPass = isNotShort(password)
                val validRepass = isPasswordEqualRePassword(password, rePassword)
                val validPhone = isNotShort(phone, 11)

                if (validName && validEmail && validPass && validRepass && validPhone) {
                    // Perform Firebase Sign Up
                    firebaseHandler.signUp(email, password) { success, errorMessage ->
                        if (success) {
                            val (firstName, lastName) = getFirstAndLastName(name)

                            // No need for nested launch, just call repository
                            viewModelScope.launch {
                                try {
                                    repository.createShopifyUser(email, firstName, lastName, phone)
                                    Log.d("Abanob", "After validateAndSignUp: User created in Shopify")
                                    _signUpState.value = SignUpState.Success
                                } catch (e: Exception) {
                                    _signUpState.value = SignUpState.Error("Failed to create Shopify user: ${e.message}")
                                }
                            }
                        } else {
                            _signUpState.value = SignUpState.Error(errorMessage ?: "Sign-up failed.")
                        }
                    }
                } else {
                    _signUpState.value = SignUpState.Error("Please fill in all fields correctly.")
                }
            } catch (e: Exception) {
                _signUpState.value = SignUpState.Error("Sign-up failed: ${e.message}")
            }
        }
    }
}
