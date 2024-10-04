package com.example.glamora.data.firebase

import com.google.firebase.auth.FirebaseUser

sealed class LoginState {
    data class Success(val user: FirebaseUser?) : LoginState()
    data class Error(val message: String) : LoginState()
    object Idle : LoginState()
}
