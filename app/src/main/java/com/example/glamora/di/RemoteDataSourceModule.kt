package com.example.glamora.di

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.network.http.LoggingInterceptor
import com.apollographql.apollo.network.okHttpClient
import com.example.glamora.BuildConfig
import com.example.glamora.data.network.ApolloClientInterceptor
import com.example.glamora.util.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object RemoteDataSourceModule {

    @Provides
    @Singleton
    fun provideApolloClient() : ApolloClient{

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(ApolloClientInterceptor(BuildConfig.ADMIN_API_ACCESS_TOKEN))
            .build()
        return ApolloClient.Builder()
            .serverUrl(Constants.BASE_URL)
            .okHttpClient(okHttpClient)
            .build()
    }

}