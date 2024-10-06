package com.example.glamora.data.model.customerModels

import com.example.glamora.util.Constants

data class CustomerInfo (
    val displayName : String = Constants.UNKNOWN,
    val email : String = Constants.UNKNOWN,
    val userId : String = Constants.UNKNOWN
)