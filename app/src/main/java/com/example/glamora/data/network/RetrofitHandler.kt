package com.example.glamora.data.network

import com.example.glamora.data.contracts.RemoteDataSource
import com.example.glamora.data.model.CutomerModels.CustomerDTO
import retrofit2.Response
import javax.inject.Inject

class RetrofitHandler @Inject constructor(
    private val retrofitInterface: RetrofitInterface,
    private val currencyApi: CurrencyApi
) : RemoteDataSource {
    override suspend fun getCustomersUsingEmail(email: String): Response<CustomerDTO> {
        return retrofitInterface.getCustomerUsingEmail(email)
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