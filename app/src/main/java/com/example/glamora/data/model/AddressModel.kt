package com.example.glamora.data.model

import android.os.Parcelable
import com.example.glamora.util.Constants
import kotlinx.parcelize.Parcelize

@Parcelize
data class AddressModel(
    var addressId : String = Constants.UNKNOWN,
    var firstName : String = Constants.UNKNOWN,
    var lastName : String = Constants.UNKNOWN,
    var phone : String = Constants.UNKNOWN,
    var country : String = Constants.UNKNOWN,
    var city : String = Constants.UNKNOWN,
    var street : String = Constants.UNKNOWN,
    var isDefault : Boolean = false
) : Parcelable