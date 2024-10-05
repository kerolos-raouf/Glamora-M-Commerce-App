package com.example.glamora.data.network

import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.model.customerModels.CustomerDTO
import com.example.nimbusweatherapp.data.model.CitiesForSearch
import retrofit2.Response
import javax.inject.Inject

class RetrofitHandler @Inject constructor(
    private val retrofitInterface: RetrofitInterface,
    private val citiesSearchApi: CitiesSearchApi
) : RemoteDataSource {
    override suspend fun getCustomersUsingEmail(email: String): Response<CustomerDTO> {
        return retrofitInterface.getCustomerUsingEmail(email)
    }

    override suspend fun getCitiesForSearch(name: String): Response<CitiesForSearch> {
        return citiesSearchApi.getCountryListForSearch(name)
    }
}