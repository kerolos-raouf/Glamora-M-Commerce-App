package com.example.glamora.data.network

import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.model.citiesModel.CitiesForSearch
import com.example.glamora.data.model.customerModels.CustomerDTO
import retrofit2.Response

class FakeRemoteDataSource : RemoteDataSource {
    override suspend fun getCustomersUsingEmail(email: String): Response<CustomerDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun getCitiesForSearch(name: String): Response<CitiesForSearch> {
        TODO("Not yet implemented")
    }

    override suspend fun convertCurrency(amount: String, currency: String): Double {
        TODO("Not yet implemented")
    }
}