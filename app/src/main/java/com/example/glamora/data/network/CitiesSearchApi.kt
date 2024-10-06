package com.example.glamora.data.network

import com.example.glamora.BuildConfig
import com.example.glamora.data.model.citiesModel.CitiesForSearch
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CitiesSearchApi {

    @GET("city")
    suspend fun getCountryListForSearch(
        @Query("name") name: String,
        @Query("limit") limit: Int = 10,
        @Query("X-Api-Key") apiKey: String = BuildConfig.COUNTRY_SEARCH_API_KEY,
    ): Response<CitiesForSearch>

}