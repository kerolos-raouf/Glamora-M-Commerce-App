package com.example.glamora.data.model.customerModels

import com.example.glamora.data.model.AddressModel
import com.example.glamora.util.Constants

data class CustomerInfo (
    val displayName : String = Constants.UNKNOWN,
    val email : String = Constants.UNKNOWN,
    val userId : String = Constants.UNKNOWN,
    val userIdAsNumber : String = Constants.UNKNOWN,
    var addresses : List<AddressModel> = emptyList()
)