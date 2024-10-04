package com.example.glamora.data.contracts

import com.example.glamora.data.model.CutomerModels.CustomerDTO
import retrofit2.Response

interface RemoteDataSource {

    suspend fun getCustomersUsingEmail(email: String) : Response<CustomerDTO>

}