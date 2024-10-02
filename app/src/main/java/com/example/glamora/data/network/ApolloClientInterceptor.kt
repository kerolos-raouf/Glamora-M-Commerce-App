package com.example.glamora.data.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class ApolloClientInterceptor(val token: String) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("X-Shopify-Access-Token", token)
            .build()

        //Log.d("Kerolos", "intercept: $token")
        return chain.proceed(request)
    }
}