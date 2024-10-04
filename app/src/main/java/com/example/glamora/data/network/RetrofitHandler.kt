package com.example.glamora.data.network

import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.model.CutomerModels.CustomerDTO
import retrofit2.Response
import javax.inject.Inject

class RetrofitHandler @Inject constructor(
    private val retrofitInterface: RetrofitInterface
) : RemoteDataSource {
    override suspend fun getCustomersUsingEmail(email: String): Response<CustomerDTO> {
        return retrofitInterface.getCustomerUsingEmail(email)
    }
}