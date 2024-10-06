package com.example.glamora.data.model.customerModels


import com.google.gson.annotations.SerializedName

data class CustomerDTO(
    @SerializedName("customers")
    val customers: List<Customer?>?
)