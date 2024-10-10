package com.example.glamora.data.network

import com.example.glamora.data.model.currency.CurrencyResponse
import com.example.glamora.data.model.currency.CurrencyResponseEx
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyApi {

    @GET("83de17802e528b46c05f898d/latest/EGP")
    suspend fun getLatestExchangeRates() : CurrencyResponse
}