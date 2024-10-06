package com.example.glamora.data.model.citiesModel


import com.google.gson.annotations.SerializedName

data class CityForSearchItem(
    @SerializedName("country")
    val country: String,
    @SerializedName("is_capital")
    val isCapital: Boolean,
    @SerializedName("latitude")
    val latitude: Double,
    @SerializedName("longitude")
    val longitude: Double,
    @SerializedName("name")
    val name: String,
    @SerializedName("population")
    val population: Int
)