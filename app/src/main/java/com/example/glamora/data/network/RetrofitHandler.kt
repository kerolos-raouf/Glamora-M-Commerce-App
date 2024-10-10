package com.example.glamora.data.network

import android.util.Log
import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.model.citiesModel.CitiesForSearch
import com.example.glamora.data.model.customerModels.CustomerDTO
import com.example.glamora.util.Constants
import retrofit2.Response
import javax.inject.Inject

class RetrofitHandler @Inject constructor(
    private val retrofitInterface: RetrofitInterface,
    private val citiesSearchApi: CitiesSearchApi,
    private val currencyApi: CurrencyApi

) : RemoteDataSource {
    override suspend fun getCustomersUsingEmail(email: String): Response<CustomerDTO> {
        return retrofitInterface.getCustomerUsingEmail(email)
    }

    override suspend fun getCitiesForSearch(name: String): Response<CitiesForSearch> {
        return citiesSearchApi.getCountryListForSearch(name)
    }

    override  suspend fun convertCurrency(amount: String, currency: String): Double {
        val conversionRates =  currencyApi.getLatestExchangeRates().conversionRates
        return when(currency)
        {
            Constants.EGP -> 1.0
            Constants.AED -> conversionRates.aED ?: 1.0
            Constants.SAR -> conversionRates.sAR ?: 1.0
            Constants.USD -> conversionRates.uSD ?: 1.0
            Constants.EUR -> conversionRates.eUR ?: 1.0
            else -> 1.0
        }
    }
}