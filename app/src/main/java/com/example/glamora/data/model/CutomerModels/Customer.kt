package com.example.glamora.data.model.CutomerModels


import com.google.gson.annotations.SerializedName

data class Customer(
    @SerializedName("addresses")
    val addresses: List<Addresse?>?,
    @SerializedName("admin_graphql_api_id")
    val adminGraphqlApiId: String?,
    @SerializedName("created_at")
    val createdAt: String?,
    @SerializedName("currency")
    val currency: String?,
    @SerializedName("default_address")
    val defaultAddress: DefaultAddress?,
    @SerializedName("email")
    val email: String?,
    @SerializedName("email_marketing_consent")
    val emailMarketingConsent: EmailMarketingConsent?,
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("id")
    val id: Long?,
    @SerializedName("last_name")
    val lastName: String?,
    @SerializedName("last_order_id")
    val lastOrderId: Any?,
    @SerializedName("last_order_name")
    val lastOrderName: Any?,
    @SerializedName("multipass_identifier")
    val multipassIdentifier: Any?,
    @SerializedName("note")
    val note: String?,
    @SerializedName("orders_count")
    val ordersCount: Int?,
    @SerializedName("phone")
    val phone: String?,
    @SerializedName("sms_marketing_consent")
    val smsMarketingConsent: SmsMarketingConsent?,
    @SerializedName("state")
    val state: String?,
    @SerializedName("tags")
    val tags: String?,
    @SerializedName("tax_exempt")
    val taxExempt: Boolean?,
    @SerializedName("tax_exemptions")
    val taxExemptions: List<Any?>?,
    @SerializedName("total_spent")
    val totalSpent: String?,
    @SerializedName("updated_at")
    val updatedAt: String?,
    @SerializedName("verified_email")
    val verifiedEmail: Boolean?
)