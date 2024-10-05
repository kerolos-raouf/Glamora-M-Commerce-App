package com.example.glamora.data.network

import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.model.CutomerModels.CustomerDTO
import com.example.nimbusweatherapp.data.model.CitiesForSearch
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
        return  currencyApi.getLatestExchangeRates(
            "iJE5wwtZSDqRo3gzln6ju5suoncyYMTm",
            amount,
            "EGP",
            currency
        ).result

    }
}