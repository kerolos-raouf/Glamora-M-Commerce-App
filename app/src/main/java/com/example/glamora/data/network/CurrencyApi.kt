package com.example.glamora.data.network

import com.example.glamora.data.model.Currency.CurrencyResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyApi {

    @GET("convert")
    suspend fun getLatestExchangeRates(
        @Query("apikey") apikey: String,
        @Query("amount") amount: String,
        @Query("from") from: String,
        @Query("to") to: String) : CurrencyResponse
}