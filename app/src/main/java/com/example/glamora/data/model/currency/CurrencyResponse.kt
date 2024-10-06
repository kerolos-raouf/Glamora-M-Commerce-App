package com.example.glamora.data.model.currency

data class CurrencyResponse(
    val date: String,
    val historical: String,
    val info: Info,
    val query: Query,
    val result: Double,
    val success: Boolean
)