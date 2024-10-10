package com.example.glamora.util

import kotlinx.coroutines.flow.MutableStateFlow

sealed class State<out T> {
    data class Success<T>(val data : T) : State<T>()
    data class Error(val message : String) : State<Nothing>()
    data object Loading : State<Nothing>()

    fun toData() : T? = if(this is Success) data else null
}