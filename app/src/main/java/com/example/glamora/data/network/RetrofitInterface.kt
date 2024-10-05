package com.example.glamora.data.network

import com.example.glamora.data.model.customerModels.CustomerDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInterface {


    @GET("customers.json")
    suspend fun getCustomerUsingEmail(
        @Query("email") email: String
    ) : Response<CustomerDTO>

}