package com.example.glamora.fragmentLogin.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.glamora.data.firebase.FirebaseHandler
import com.example.glamora.data.firebase.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val firebaseHandler: FirebaseHandler = FirebaseHandler()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> get() = _loginState

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            firebaseHandler.signIn(email, password) { success, error ->
                if (success) {
                    _loginState.value = LoginState.Success(firebaseHandler.getCurrentUser())
                } else {
                    _loginState.value = LoginState.Error(error ?: "Unknown error")
                }
            }
        }
    }

    fun resetUserPass(email: String) {
        viewModelScope.launch {
            firebaseHandler.resetPassword(email) { success, error ->
                if (success) {
                    _loginState.value = LoginState.Success(null)
                } else {
                    _loginState.value = LoginState.Error(error ?: "Failed to send reset email")
                }
            }
        }
    }


    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            firebaseHandler.signInWithGoogle(idToken) { success, error ->
                if (success) {
                    _loginState.value = LoginState.Success(firebaseHandler.getCurrentUser())
                } else {
                    _loginState.value = LoginState.Error(error ?: "Unknown error")
                }
            }
        }
    }

    fun resetLoginState() {
        _loginState.value = LoginState.Idle
    }

}