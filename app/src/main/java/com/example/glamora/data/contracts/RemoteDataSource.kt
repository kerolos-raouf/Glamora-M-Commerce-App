package com.example.glamora.data.contracts

import com.example.glamora.data.model.CutomerModels.CustomerDTO
import com.example.nimbusweatherapp.data.model.CitiesForSearch
import retrofit2.Response

interface RemoteDataSource {

    suspend fun getCustomersUsingEmail(email: String) : Response<CustomerDTO>

    suspend fun getCitiesForSearch(name: String) : Response<CitiesForSearch>

    suspend fun convertCurrency(amount: String, currency: String): Double

}