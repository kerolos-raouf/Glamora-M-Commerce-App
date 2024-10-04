package com.example.glamora.data.model.CutomerModels


import com.google.gson.annotations.SerializedName

data class EmailMarketingConsent(
    @SerializedName("consent_updated_at")
    val consentUpdatedAt: Any?,
    @SerializedName("opt_in_level")
    val optInLevel: String?,
    @SerializedName("state")
    val state: String?
)